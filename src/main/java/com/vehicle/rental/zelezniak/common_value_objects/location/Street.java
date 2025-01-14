package com.vehicle.rental.zelezniak.common_value_objects.location;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record Street(
        @NotBlank(message = "Street name can not be blank.")
        String streetName) {
}
