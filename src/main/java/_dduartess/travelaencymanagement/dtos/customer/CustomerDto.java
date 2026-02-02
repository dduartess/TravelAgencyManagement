package _dduartess.travelaencymanagement.dtos.customer;

import java.time.LocalDate;

public record CustomerDto(
    String name,
    String documentNumber,
    LocalDate birthDate,
    String phoneNumber
) {}