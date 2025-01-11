package com.vehicle.rental.zelezniak.vehicle.model.dto;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

public record AvailableVehiclesCriteriaSearchRequest(
        @Valid
        RentDuration duration,
        @NotNull(message = "Criteria search request" + CAN_NOT_BE_NULL)
        CriteriaSearchRequest searchRequest
) {
}
