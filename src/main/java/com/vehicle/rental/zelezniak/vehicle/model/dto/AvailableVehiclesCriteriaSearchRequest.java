package com.vehicle.rental.zelezniak.vehicle.model.dto;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AvailableVehiclesCriteriaSearchRequest(
        @Valid
        RentDuration duration,
        @NotNull(message = "Criteria search request can not be null.")
        CriteriaSearchRequest searchRequest
) {
}
