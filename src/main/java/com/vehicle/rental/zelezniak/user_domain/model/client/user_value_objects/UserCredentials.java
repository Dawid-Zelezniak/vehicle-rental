package com.vehicle.rental.zelezniak.user_domain.model.client.user_value_objects;

import com.vehicle.rental.zelezniak.constants.ValidationMessages;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.*;

@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class UserCredentials {

    private static final String INVALID_PASSWORD = "Password must contains at least 5 characters.";

    @NotNull(message = "Email address" + ValidationMessages.CAN_NOT_BE_NULL)
    private final String email;

    @NotNull(message = INVALID_PASSWORD)
    @Size(min = 5, message = INVALID_PASSWORD)
    private final String password;
}
