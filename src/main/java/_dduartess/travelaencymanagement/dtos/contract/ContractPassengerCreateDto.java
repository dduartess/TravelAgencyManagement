package _dduartess.travelaencymanagement.dtos.contract;

import _dduartess.travelaencymanagement.dtos.customer.CustomerDto;
import _dduartess.travelaencymanagement.entities.contract.ChargeType;
import _dduartess.travelaencymanagement.entities.trip.RoomType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ContractPassengerCreateDto(
        @Valid @NotNull CustomerDto customer,
        @NotNull ChargeType chargeType,
        RoomType roomType,
        String notes
) {}
