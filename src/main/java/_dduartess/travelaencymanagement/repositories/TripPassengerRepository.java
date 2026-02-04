package _dduartess.travelaencymanagement.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import _dduartess.travelaencymanagement.entities.trip.TripPassenger;

public interface TripPassengerRepository extends JpaRepository<TripPassenger, Long> {

    boolean existsByTripIdAndCustomerId(Long tripId, Long customerId);

    long countByTripIdAndCustomerId(Long tripId, Long customerId);

    List<TripPassenger> findByTripId(Long tripId);

    void deleteByTripIdAndCustomerId(Long tripId, Long customerId);
}