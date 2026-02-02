package _dduartess.travelaencymanagement.controllers;

import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;
import _dduartess.travelaencymanagement.dtos.trip.TripCreateDto;
import _dduartess.travelaencymanagement.dtos.trip.TripPassengerStatsDto;
import _dduartess.travelaencymanagement.dtos.trip.TripResponseDto;
import _dduartess.travelaencymanagement.service.TripService;
import jakarta.validation.Valid;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<Set<TripResponseDto>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @PostMapping
    public ResponseEntity<TripResponseDto> createTrip(@RequestBody @Valid TripCreateDto dto) {
        TripResponseDto created = tripService.createTrip(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{tripId}/passengers")
    public ResponseEntity<TripResponseDto> addPassenger(@PathVariable Long tripId, @RequestBody @Valid CustomerDto dto) {
        TripResponseDto updated = tripService.addPassenger(tripId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{tripId}/passengers")
    public ResponseEntity<TripPassengerStatsDto> getTripPassengers(@PathVariable Long tripId) {
        TripPassengerStatsDto stats = tripService.getTripPassengers(tripId);
        return ResponseEntity.ok(stats);
    }
}
