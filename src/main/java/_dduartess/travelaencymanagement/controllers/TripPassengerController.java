package _dduartess.travelaencymanagement.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import _dduartess.travelaencymanagement.dtos.customer.CustomerResponseDto;
import _dduartess.travelaencymanagement.service.ContractService;

@RestController
@RequestMapping("/api/v1")
public class TripPassengerController {

    private final ContractService contractService;

    public TripPassengerController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("/trips/{tripId}/passengers")
    public ResponseEntity<List<CustomerResponseDto>> listTripPassengers(@PathVariable Long tripId) {
        return ResponseEntity.ok(contractService.listTripPassengers(tripId));
    }
}
