package _dduartess.travelaencymanagement.dtos.trip;

import java.util.Set;

import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;

public record TripPassengerStatsDto(
    Set<CustomerDto> passengers,
    int total
) {}