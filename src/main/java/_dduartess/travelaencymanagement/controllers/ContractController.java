package _dduartess.travelaencymanagement.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import _dduartess.travelaencymanagement.dtos.contract.*;
import _dduartess.travelaencymanagement.service.ContractPdfService;
import _dduartess.travelaencymanagement.service.ContractService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ContractController {

    private final ContractService contractService;
    private final ContractPdfService contractPdfService;

    public ContractController(ContractService contractService, ContractPdfService contractPdfService) {
        this.contractService = contractService;
        this.contractPdfService = contractPdfService;
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

    @GetMapping(value = "/contracts/{contractId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdf(@PathVariable Long contractId) {
        var pdf = contractPdfService.generateContractPdf(contractId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, pdf.contentDisposition())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.bytes());
    }
}
