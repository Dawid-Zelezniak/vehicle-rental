package com.vehicle.rental.zelezniak.user.model.user.user_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserCredentials {

    private static final String INVALID_PASSWORD = "Password must contains at least 5 characters.";

    @NotNull(message = "Email address can not be null.")
    private final String email;

    @NotNull(message = INVALID_PASSWORD)
    @Size(min = 5, message = INVALID_PASSWORD)
    private final String password;
}
