package com.vehicle.rental.zelezniak.common_value_objects.location;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record City(
        @NotBlank(message = "City name cannot be blank. Please provide a valid city name.")
        String cityName) {
}

