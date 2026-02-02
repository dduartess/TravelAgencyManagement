package _dduartess.travelaencymanagement.dtos.customer;

import java.time.LocalDate;

public record CustomerResponseDto(
        Long id,
        String name,
        String documentNumber,
        LocalDate birthDate,
        String phoneNumber
) {}
