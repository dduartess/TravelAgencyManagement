package _dduartess.travelaencymanagement.dtos.contract;

import java.time.LocalDateTime;
import java.util.List;

public record ContractResponseDto(
        Long id,
        Long tripId,
        String description,
        LocalDateTime createdAt,
        List<ContractPassengerResponseDto> passengers
) {}
