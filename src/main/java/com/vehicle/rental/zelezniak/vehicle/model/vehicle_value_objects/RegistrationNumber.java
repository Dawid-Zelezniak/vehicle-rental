package com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class RegistrationNumber {

    @NotBlank(message = "Registration number can not be blank.")
    private final String registration;
}
