package com.vehicle.rental.zelezniak.common_value_objects.location;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_BLANK;

@Embeddable
public record Country(
        @NotBlank(message = "Country name" + CAN_NOT_BE_BLANK)
        String countryName) {
}
