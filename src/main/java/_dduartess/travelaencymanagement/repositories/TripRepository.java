package _dduartess.travelaencymanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import _dduartess.travelaencymanagement.entities.trip.Trip;

public interface TripRepository extends JpaRepository<Trip, Long>{

}
