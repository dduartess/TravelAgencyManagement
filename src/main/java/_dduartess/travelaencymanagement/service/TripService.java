package _dduartess.travelaencymanagement.service;

import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;
import _dduartess.travelaencymanagement.dtos.customer.CustomerResponseDto;
import _dduartess.travelaencymanagement.dtos.trip.TripCreateDto;
import _dduartess.travelaencymanagement.dtos.trip.TripPassengerStatsDto;
import _dduartess.travelaencymanagement.dtos.trip.TripResponseDto;
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
    public TripResponseDto createTrip(TripCreateDto dto) {
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

        Trip saved = tripRepository.save(trip);
        return toResponse(saved);
    }

    @Transactional
    public TripResponseDto addPassenger(Long tripId, CustomerDto customerDto) {
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
        Trip saved = tripRepository.save(trip);

        return toResponse(saved);
    }

    public TripPassengerStatsDto getTripPassengers(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada com ID: " + tripId));

    Set<CustomerResponseDto> passengers = trip.getPassengers().stream()
            .map(c -> new CustomerResponseDto(
                    c.getId(),
                    c.getName(),
                    c.getDocumentNumber(),
                    c.getBirthDate(),
                    c.getPhoneNumber()
            ))
            .collect(Collectors.toSet());

    return new TripPassengerStatsDto(passengers, passengers.size());
}
    private TripResponseDto toResponse(Trip trip) {
        Set<CustomerResponseDto> passengers = trip.getPassengers().stream()
                .map(c -> new CustomerResponseDto(
                        c.getId(),
                        c.getName(),
                        c.getDocumentNumber(),
                        c.getBirthDate(),
                        c.getPhoneNumber()
                ))
                .collect(Collectors.toSet());

        return new TripResponseDto(
                trip.getId(),
                trip.getDestination(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getRoomPrices(),
                passengers
        );
    }

    @Transactional(readOnly = true)
    public Set<TripResponseDto> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toSet());
    }

    public TripResponseDto updateTrip(Long tripId, TripCreateDto dto) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada com ID: " + tripId));

        if (dto.startDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de início não pode ser anterior à data atual.");
        }
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new IllegalArgumentException("A data de fim não pode ser menor que a data de início.");
        }

        trip.setDestination(dto.destination());
        trip.setStartDate(dto.startDate());
        trip.setEndDate(dto.endDate());
        trip.setRoomPrices(new HashMap<>(dto.roomPrices()));

        Trip updated = tripRepository.save(trip);
        return toResponse(updated);
    }

    public void deleteTrip(Long tripId) {
        tripRepository.deleteById(tripId);
    }

    public void removePassenger(Long tripId, Long customerId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada com ID: " + tripId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + customerId));

        if (!trip.getPassengers().contains(customer)) {
            throw new IllegalArgumentException("Este passageiro não está cadastrado nesta viagem.");
        }

        trip.getPassengers().remove(customer);
        tripRepository.save(trip);
    }

    public TripResponseDto updatePassenger(Long tripId, Long customerId, CustomerDto dto) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada com ID: " + tripId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + customerId));

        if (!trip.getPassengers().contains(customer)) {
            throw new IllegalArgumentException("Este passageiro não está cadastrado nesta viagem.");
        }

        customer.updateFromDto(dto);

        customerRepository.save(customer);

        return toResponse(trip);
    }

}
