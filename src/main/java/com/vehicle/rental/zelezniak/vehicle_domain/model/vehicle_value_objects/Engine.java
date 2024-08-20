package com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_BLANK;

@Embeddable
@Builder(toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Engine {

    private static final String CANT_BE_BELOW_ONE = " can not be lower than 1";

    @NotBlank(message = "Engine type" + CAN_NOT_BE_BLANK)
    private final String engineType;

    @Enumerated(EnumType.STRING)
    private final FuelType fuelType;

    @Min(value = 1,message = "Horse power" + CANT_BE_BELOW_ONE)
    private final int horsepower;

    @Min(value = 1,message = "Cylinders number" + CANT_BE_BELOW_ONE)
    private final int cylinders;

    @Min(value = 1,message = "Displacement" + CANT_BE_BELOW_ONE)
    private final double displacement;

    public enum FuelType {
        GASOLINE,
        GASOLINE_WITH_GAS,
        DIESEL,
        ELECTRIC,
        HYBRID,
    }
}
