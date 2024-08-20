package com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.*;

@Embeddable
@Builder(toBuilder = true)
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class VehicleInformation {

    @NotBlank(message = "Brand" + CAN_NOT_BE_BLANK)
    private final String brand;

    @NotBlank(message = "Model" + CAN_NOT_BE_BLANK)
    private final String model;

    @Min(value = 1, message = "Seats number can not be lower than 1")
    private final int seatsNumber;

    @Embedded
    @AttributeOverride(
            name = "registration",
            column = @Column(name = "registration_number"))
    private final RegistrationNumber registrationNumber;

    @Embedded
    @AttributeOverride(
            name = "year",
            column = @Column(name = "production_year"))
    private final Year productionYear;

    @NotBlank(message = "Description" + CAN_NOT_BE_BLANK)
    private final String description;

    @Embedded
    private final Engine engine;

    @Enumerated(EnumType.STRING)
    private final GearType gearType;

    public enum GearType {
        MANUAL,
        AUTOMATIC,
        CVT,
        DUAL_CLUTCH
    }
}