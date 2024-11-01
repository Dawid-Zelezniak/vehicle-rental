package com.vehicle.rental.zelezniak.common_value_objects.address;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_BLANK;

@Embeddable
public record City(

        @NotBlank(message = "City name" + CAN_NOT_BE_BLANK)
        String cityName) {
}
