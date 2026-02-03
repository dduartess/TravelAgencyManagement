package _dduartess.travelaencymanagement.dtos.trip;

import java.util.Set;
import _dduartess.travelaencymanagement.dtos.customer.CustomerResponseDto;

public record TripPassengerStatsDto(
        Set<CustomerResponseDto> passengers,
        int total
) {}