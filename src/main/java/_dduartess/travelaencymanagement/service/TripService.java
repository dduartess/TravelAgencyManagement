package _dduartess.travelaencymanagement.service;

import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;
import _dduartess.travelaencymanagement.dtos.trip.TripCreateDto;
import _dduartess.travelaencymanagement.dtos.trip.TripPassengerStatsDto;
import _dduartess.travelaencymanagement.entities.customers.Customer;
import _dduartess.travelaencymanagement.entities.trip.Trip;
import _dduartess.travelaencymanagement.repositories.CustomerRepository;
import _dduartess.travelaencymanagement.repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final CustomerRepository customerRepository;

    public TripService(TripRepository tripRepository, CustomerRepository customerRepository) {
        this.tripRepository = tripRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Trip createTrip(TripCreateDto dto) {
        if (dto.startDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de início não pode ser anterior à data atual.");
        }
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("A data de fim não pode ser menor que a data de início.");
        }

        Trip trip = new Trip();
        trip.setDestination(dto.destination());
        trip.setStartDate(dto.startDate());
        trip.setEndDate(dto.endDate());
        trip.setRoomPrices(new HashMap<>(dto.roomPrices()));

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip addPassenger(Long tripId, CustomerDto customerDto) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada com ID: " + tripId));

        Customer customer = customerRepository.findByDocumentNumber(customerDto.documentNumber())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(customerDto.name());
                    newCustomer.setDocumentNumber(customerDto.documentNumber());
                    newCustomer.setBirthDate(customerDto.birthDate());
                    newCustomer.setPhoneNumber(customerDto.phoneNumber());
                    return customerRepository.save(newCustomer);
                });

        if (trip.getPassengers().contains(customer)) {
            throw new IllegalArgumentException("Este passageiro já está cadastrado nesta viagem.");
        }

        trip.getPassengers().add(customer);
        return tripRepository.save(trip);
    }

    @Transactional(readOnly = true)
    public TripPassengerStatsDto getTripPassengers(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada com ID: " + tripId));

        Set<CustomerDto> passengerDtos = trip.getPassengers().stream()
                .map(c -> new CustomerDto(
                        c.getName(),
                        c.getDocumentNumber(),
                        c.getBirthDate(),
                        c.getPhoneNumber()
                ))
                .collect(Collectors.toSet());

        return new TripPassengerStatsDto(passengerDtos, passengerDtos.size());
    }
}
