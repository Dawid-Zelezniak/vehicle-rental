package com.vehicle.rental.zelezniak.user.model.login;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "Email can not be null.")
        String email,
        @NotNull(message = "Password can not be null.")
        String password) {
}
