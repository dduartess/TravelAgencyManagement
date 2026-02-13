package _dduartess.travelaencymanagement.dtos.contract;

import java.math.BigDecimal;
import _dduartess.travelaencymanagement.dtos.customer.CustomerResponseDto;
import _dduartess.travelaencymanagement.entities.contract.ChargeType;
import _dduartess.travelaencymanagement.entities.trip.RoomType;

public record ContractPassengerResponseDto(
        CustomerResponseDto customer,
        ChargeType chargeType,
        RoomType roomType,
        BigDecimal priceSnapshot,
        String notes
) {}
