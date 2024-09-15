package com.vehicle.rental.zelezniak.user_domain.model.login;

import jakarta.validation.constraints.NotNull;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

public record LoginRequest(
        @NotNull(message = "Email" + CAN_NOT_BE_NULL)
        String email,
        @NotNull(message = "Password" + CAN_NOT_BE_NULL)
        String password) {
}
