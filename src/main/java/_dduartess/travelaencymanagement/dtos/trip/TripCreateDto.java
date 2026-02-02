package _dduartess.travelaencymanagement.dtos.trip;

import _dduartess.travelaencymanagement.entities.trip.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record TripCreateDto(
    String destination,
    LocalDate startDate,
    LocalDate endDate,
    Map<RoomType, BigDecimal> roomPrices
) {}