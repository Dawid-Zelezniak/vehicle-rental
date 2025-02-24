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
public class UserName {

    private static final String FIRST_NAME_SIZE_INVALID =  "First name must contains at least 3 characters";
    private static final String LAST_NAME_SIZE_INVALID =  "Last name must contains at least 2 characters";

    @NotNull(message = "First name must not be null")
    @Size(min = 3, message = FIRST_NAME_SIZE_INVALID)
    private final String firstName;

    @NotNull(message = "Last name must not be null")
    @Size(min = 2, message = LAST_NAME_SIZE_INVALID)
    private final String lastName;
}
