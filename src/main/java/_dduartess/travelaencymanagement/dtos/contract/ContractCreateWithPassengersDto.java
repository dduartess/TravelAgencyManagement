package _dduartess.travelaencymanagement.dtos.contract;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ContractCreateWithPassengersDto(
        @NotNull Long tripId,
        String description,
        @Valid @NotEmpty List<ContractPassengerCreateDto> passengers
) {}
