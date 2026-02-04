package _dduartess.travelaencymanagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import _dduartess.travelaencymanagement.dtos.contract.ContractCreateWithPassengersDto;
import _dduartess.travelaencymanagement.dtos.contract.ContractPassengerCreateDto;
import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;
import _dduartess.travelaencymanagement.entities.contract.ChargeType;
import _dduartess.travelaencymanagement.entities.contract.Contract;
import _dduartess.travelaencymanagement.entities.contract.ContractPassenger;
import _dduartess.travelaencymanagement.entities.customers.Customer;
import _dduartess.travelaencymanagement.entities.trip.RoomType;
import _dduartess.travelaencymanagement.entities.trip.Trip;
import _dduartess.travelaencymanagement.entities.trip.TripPassenger;
import _dduartess.travelaencymanagement.repositories.ContractPassengerRepository;
import _dduartess.travelaencymanagement.repositories.ContractRepository;
import _dduartess.travelaencymanagement.repositories.CustomerRepository;
import _dduartess.travelaencymanagement.repositories.TripPassengerRepository;
import _dduartess.travelaencymanagement.repositories.TripRepository;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

    @Mock private ContractRepository contractRepository;
    @Mock private ContractPassengerRepository contractPassengerRepository;
    @Mock private TripRepository tripRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private TripPassengerRepository tripPassengerRepository;

    @InjectMocks private ContractService contractService;

    private Trip trip;

    @BeforeEach
    void setup() {
        trip = new Trip();
        trip.setId(1L);
        trip.setRoomPrices(Map.of(
                RoomType.CASAL, new BigDecimal("850.00"),
                RoomType.QUADRUPLO, new BigDecimal("750.00")
        ));
    }

    @Test
    void createWithPassengers_shouldCreateContractPassengers_andTripPassengers() {
        var dto = new ContractCreateWithPassengersDto(
                1L,
                "Contrato família",
                List.of(
                        new ContractPassengerCreateDto(
                                new CustomerDto("João", "111", LocalDate.of(2000, 1, 1), "38999999999"),
                                ChargeType.PAYING,
                                RoomType.CASAL,
                                "ok"
                        ),
                        new ContractPassengerCreateDto(
                                new CustomerDto("Maria", "222", LocalDate.of(2001, 2, 2), "38988888888"),
                                ChargeType.NON_PAYING,
                                null,
                                ""
                        )
                )
        );

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
            Contract c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        when(customerRepository.findByDocumentNumber("111")).thenReturn(Optional.empty());
        when(customerRepository.findByDocumentNumber("222")).thenReturn(Optional.empty());

        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            if (c.getDocumentNumber().equals("111")) c.setId(101L);
            if (c.getDocumentNumber().equals("222")) c.setId(102L);
            return c;
        });

        when(contractPassengerRepository.existsByContractIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);
        when(contractPassengerRepository.existsByContractTripIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);

        when(contractPassengerRepository.countByContractIdAndChargeType(10L, ChargeType.PAYING)).thenReturn(0L);

        when(tripPassengerRepository.existsByTripIdAndCustomerId(1L, 101L)).thenReturn(false);
        when(tripPassengerRepository.existsByTripIdAndCustomerId(1L, 102L)).thenReturn(false);

        when(contractRepository.findById(10L)).thenAnswer(inv -> {
            Contract c = new Contract();
            c.setId(10L);
            c.setTrip(trip);
            c.setDescription("Contrato família");
            return Optional.of(c);
        });

        when(contractPassengerRepository.findByContractId(10L)).thenReturn(List.of(
                makeContractPassenger(10L, 101L, "João", "111", ChargeType.PAYING, RoomType.CASAL, new BigDecimal("850.00"), "ok"),
                makeContractPassenger(10L, 102L, "Maria", "222", ChargeType.NON_PAYING, null, BigDecimal.ZERO, "")
        ));

        var response = contractService.createWithPassengers(dto);

        assertEquals(10L, response.id());
        assertEquals(1L, response.tripId());
        assertEquals(2, response.passengers().size());

        verify(contractPassengerRepository, times(2)).save(any(ContractPassenger.class));
        verify(tripPassengerRepository, times(2)).save(any(TripPassenger.class));

        ArgumentCaptor<ContractPassenger> passengerCaptor = ArgumentCaptor.forClass(ContractPassenger.class);
        verify(contractPassengerRepository, times(2)).save(passengerCaptor.capture());

        var savedItems = passengerCaptor.getAllValues();
        assertTrue(savedItems.stream().anyMatch(p -> p.getChargeType() == ChargeType.PAYING && new BigDecimal("850.00").compareTo(p.getPriceSnapshot()) == 0));
        assertTrue(savedItems.stream().anyMatch(p -> p.getChargeType() == ChargeType.NON_PAYING && BigDecimal.ZERO.compareTo(p.getPriceSnapshot()) == 0));
    }

    private ContractPassenger makeContractPassenger(
            Long contractId,
            Long customerId,
            String name,
            String document,
            ChargeType chargeType,
            RoomType roomType,
            BigDecimal priceSnapshot,
            String notes
    ) {
        Trip t = new Trip();
        t.setId(1L);

        Contract c = new Contract();
        c.setId(contractId);
        c.setTrip(t);

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName(name);
        customer.setDocumentNumber(document);
        customer.setBirthDate(LocalDate.of(2000, 1, 1));
        customer.setPhoneNumber("000");

        ContractPassenger cp = new ContractPassenger();
        cp.setContract(c);
        cp.setCustomer(customer);
        cp.setChargeType(chargeType);
        cp.setRoomType(roomType);
        cp.setPriceSnapshot(priceSnapshot);
        cp.setNotes(notes);
        return cp;
    }

    @Test
    void createWithPassengers_shouldFail_whenMoreThanFourPayingPassengers() {
        var passengers = List.of(
                paying("1"), paying("2"), paying("3"), paying("4"), paying("5")
        );

        var dto = new ContractCreateWithPassengersDto(1L, "Contrato", passengers);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(contractRepository.save(any(Contract.class))).thenAnswer(inv -> {
            Contract c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        when(customerRepository.findByDocumentNumber(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(Long.parseLong(c.getDocumentNumber()));
            return c;
        });

        when(contractPassengerRepository.existsByContractIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);
        when(contractPassengerRepository.existsByContractTripIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);

        when(tripPassengerRepository.existsByTripIdAndCustomerId(anyLong(), anyLong())).thenReturn(false);

        when(contractPassengerRepository.countByContractIdAndChargeType(10L, ChargeType.PAYING))
                .thenReturn(0L, 1L, 2L, 3L, 4L);

        var ex = assertThrows(IllegalArgumentException.class, () -> contractService.createWithPassengers(dto));
        assertTrue(ex.getMessage().contains("no máximo 4"));
    }

    private ContractPassengerCreateDto paying(String documentNumber) {
        return new ContractPassengerCreateDto(
                new CustomerDto("P" + documentNumber, documentNumber, LocalDate.of(2000, 1, 1), "38900000000"),
                ChargeType.PAYING,
                RoomType.CASAL,
                null
        );
    }

}
