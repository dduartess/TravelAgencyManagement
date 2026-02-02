package _dduartess.travelaencymanagement.dtos.trip;

import _dduartess.travelaencymanagement.dtos.customer.CustomerResponseDto;
import _dduartess.travelaencymanagement.entities.trip.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public record TripResponseDto(
        Long id,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        Map<RoomType, BigDecimal> roomPrices,
        Set<CustomerResponseDto> passengers
) {}
