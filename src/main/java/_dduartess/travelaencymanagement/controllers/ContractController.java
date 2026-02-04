package _dduartess.travelaencymanagement.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import _dduartess.travelaencymanagement.dtos.contract.*;
import _dduartess.travelaencymanagement.service.ContractService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping("/contracts/with-passengers")
    public ResponseEntity<ContractResponseDto> createWithPassengers(@RequestBody @Valid ContractCreateWithPassengersDto dto) {
        return ResponseEntity.ok(contractService.createWithPassengers(dto));
    }

    @PostMapping("/contracts/{contractId}/passengers")
    public ResponseEntity<ContractResponseDto> addPassenger(@PathVariable Long contractId, @RequestBody @Valid ContractPassengerCreateDto dto) {
        return ResponseEntity.ok(contractService.addPassenger(contractId, dto));
    }

    @DeleteMapping("/contracts/{contractId}/passengers/{customerId}")
    public ResponseEntity<ContractResponseDto> removePassenger(@PathVariable Long contractId, @PathVariable Long customerId) {
        return ResponseEntity.ok(contractService.removePassenger(contractId, customerId));
    }

    @GetMapping("/contracts/{contractId}")
    public ResponseEntity<ContractResponseDto> get(@PathVariable Long contractId) {
        return ResponseEntity.ok(contractService.getContract(contractId));
    }
}
