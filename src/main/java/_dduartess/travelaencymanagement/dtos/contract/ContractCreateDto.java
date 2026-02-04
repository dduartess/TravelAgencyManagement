package _dduartess.travelaencymanagement.dtos.contract;

import jakarta.validation.constraints.NotNull;

public record ContractCreateDto(
        @NotNull Long tripId,
        String description
) {}
