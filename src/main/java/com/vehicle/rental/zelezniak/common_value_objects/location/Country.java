package com.vehicle.rental.zelezniak.common_value_objects.location;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record Country(
        @NotBlank(message = "Country name cannot be blank. Please provide a valid country name.")
        String countryName) {
}

