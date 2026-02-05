package _dduartess.travelaencymanagement.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import _dduartess.travelaencymanagement.dtos.contract.*;
import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;
import _dduartess.travelaencymanagement.dtos.customer.CustomerResponseDto;
import _dduartess.travelaencymanagement.entities.contract.*;
import _dduartess.travelaencymanagement.entities.customers.Customer;
import _dduartess.travelaencymanagement.entities.trip.RoomType;
import _dduartess.travelaencymanagement.entities.trip.Trip;
import _dduartess.travelaencymanagement.entities.trip.TripPassenger;
import _dduartess.travelaencymanagement.repositories.*;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractPassengerRepository contractPassengerRepository;
    private final TripRepository tripRepository;
    private final CustomerRepository customerRepository;
    private final TripPassengerRepository tripPassengerRepository;

    public ContractService(
            ContractRepository contractRepository,
            ContractPassengerRepository contractPassengerRepository,
            TripRepository tripRepository,
            CustomerRepository customerRepository,
            TripPassengerRepository tripPassengerRepository
    ) {
        this.contractRepository = contractRepository;
        this.contractPassengerRepository = contractPassengerRepository;
        this.tripRepository = tripRepository;
        this.customerRepository = customerRepository;
        this.tripPassengerRepository = tripPassengerRepository;
    }

    @Transactional
    public ContractResponseDto createWithPassengers(ContractCreateWithPassengersDto dto) {
        Trip trip = tripRepository.findById(dto.tripId())
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada com ID: " + dto.tripId()));

        Contract contract = new Contract();
        contract.setTrip(trip);
        contract.setDescription(dto.description());
        Contract savedContract = contractRepository.save(contract);

        for (ContractPassengerCreateDto passengerDto : dto.passengers()) {
            addPassengerInternal(savedContract, trip, passengerDto);
        }

        contractPassengerRepository.flush();
        tripPassengerRepository.flush();

        return getContract(savedContract.getId());
    }

    @Transactional
    public ContractResponseDto addPassenger(Long contractId, ContractPassengerCreateDto dto) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado com ID: " + contractId));

        Trip trip = contract.getTrip();
        addPassengerInternal(contract, trip, dto);

        contractPassengerRepository.flush();
        tripPassengerRepository.flush();

        return getContract(contractId);
    }

    @Transactional
    public ContractResponseDto removePassenger(Long contractId, Long customerId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado com ID: " + contractId));

        ContractPassenger item = contractPassengerRepository.findByContractIdAndCustomerId(contractId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Passageiro não encontrado neste contrato."));

        contractPassengerRepository.delete(item);

        Long tripId = contract.getTrip().getId();
        long remainingInTripContracts =
                contractPassengerRepository.countByContractTripIdAndCustomerId(tripId, customerId);

        if (remainingInTripContracts == 0) {
            Trip trip = contract.getTrip();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + customerId));
            trip.getPassengers().remove(customer);
            tripRepository.save(trip);
            tripRepository.flush();
        }

        return getContract(contractId);
    }

    @Transactional(readOnly = true)
    public ContractResponseDto getContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado com ID: " + contractId));

        List<ContractPassengerResponseDto> passengers =
                contractPassengerRepository.findByContractId(contractId).stream()
                        .map(this::toPassengerResponse)
                        .toList();

        return new ContractResponseDto(
                contract.getId(),
                contract.getTrip().getId(),
                contract.getDescription(),
                contract.getCreatedAt(),
                passengers
        );
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> listTripPassengers(Long tripId) {
        return tripPassengerRepository.findByTripId(tripId).stream()
                .map(TripPassenger::getCustomer)
                .map(c -> new CustomerResponseDto(
                        c.getId(),
                        c.getName(),
                        c.getDocumentNumber(),
                        c.getBirthDate(),
                        c.getPhoneNumber()
                ))
                .toList();
    }

    private void addPassengerInternal(Contract contract, Trip trip, ContractPassengerCreateDto dto) {
        Customer customer = getOrCreateCustomer(dto.customer());

        if (contractPassengerRepository.existsByContractIdAndCustomerId(contract.getId(), customer.getId())) {
            throw new IllegalArgumentException("Este passageiro já está cadastrado neste contrato.");
        }

        if (contractPassengerRepository.existsByContractTripIdAndCustomerId(trip.getId(), customer.getId())) {
            throw new IllegalArgumentException("Este passageiro já está cadastrado em outro contrato desta viagem.");
        }

        validatePayingRules(contract.getId(), customer, dto.chargeType());

        ContractPassenger item = new ContractPassenger();
        item.setContract(contract);
        item.setCustomer(customer);
        item.setChargeType(dto.chargeType());
        item.setRoomType(dto.roomType());
        item.setPriceSnapshot(resolvePriceSnapshot(trip, dto.roomType(), dto.chargeType()));
        item.setNotes(dto.notes());

        contractPassengerRepository.save(item);
        ensureTripPassenger(trip, customer);
    }

    private void ensureTripPassenger(Trip trip, Customer customer) {
        if (!trip.getPassengers().contains(customer)) {
            trip.getPassengers().add(customer);
            tripRepository.save(trip);
            tripRepository.flush();
        }
    }

    private Customer getOrCreateCustomer(CustomerDto dto) {
        return customerRepository.findByDocumentNumber(dto.documentNumber())
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setName(dto.name());
                    customer.setDocumentNumber(dto.documentNumber());
                    customer.setBirthDate(dto.birthDate());
                    customer.setPhoneNumber(dto.phoneNumber());
                    return customerRepository.save(customer);
                });
    }

    private void validatePayingRules(Long contractId, Customer customer, ChargeType chargeType) {
        if (chargeType != ChargeType.PAYING) return;

        int age = Period.between(customer.getBirthDate(), LocalDate.now()).getYears();
        if (age < 5) {
            throw new IllegalArgumentException("Passageiros pagantes devem ter pelo menos 5 anos.");
        }

        long payingCount =
                contractPassengerRepository.countByContractIdAndChargeType(contractId, ChargeType.PAYING);
        if (payingCount >= 4) {
            throw new IllegalArgumentException("Um contrato pode ter no máximo 4 passageiros pagantes.");
        }
    }

    private BigDecimal resolvePriceSnapshot(Trip trip, RoomType roomType, ChargeType chargeType) {
        if (chargeType != ChargeType.PAYING) return BigDecimal.ZERO;
        if (roomType == null) return null;
        return trip.getRoomPrices().get(roomType);
    }

    private ContractPassengerResponseDto toPassengerResponse(ContractPassenger cp) {
        Customer c = cp.getCustomer();
        CustomerResponseDto customer =
                new CustomerResponseDto(
                        c.getId(),
                        c.getName(),
                        c.getDocumentNumber(),
                        c.getBirthDate(),
                        c.getPhoneNumber()
                );
        return new ContractPassengerResponseDto(
                customer,
                cp.getChargeType(),
                cp.getRoomType(),
                cp.getPriceSnapshot(),
                cp.getNotes()
        );
    }
}
