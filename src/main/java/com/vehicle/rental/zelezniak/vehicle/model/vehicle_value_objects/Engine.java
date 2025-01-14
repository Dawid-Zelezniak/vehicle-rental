package com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Embeddable
@Builder(toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Engine {

    private static final String CANT_BE_BELOW_ONE = " can not be lower than 1";

    @NotBlank(message = "Engine type can not be blank.")
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
