package com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_BLANK;

@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class RegistrationNumber {

    @NotBlank(message = "Registration number" + CAN_NOT_BE_BLANK)
    private final String registration;
}
