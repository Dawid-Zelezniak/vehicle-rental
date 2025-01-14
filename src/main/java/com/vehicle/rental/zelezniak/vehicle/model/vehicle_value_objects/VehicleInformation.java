package com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects;

import jakarta.persistence.*;
import jakarta.validation.Valid;
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
public class VehicleInformation {

    @NotBlank(message = "Brand can not be blank.")
    private final String brand;

    @NotBlank(message = "Model can not be blank.")
    private final String model;

    @Min(value = 1, message = "Seats number can not be lower than 1")
    private final int seatsNumber;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "registration",
            column = @Column(name = "registration_number"))
    private final RegistrationNumber registrationNumber;

    @Embedded
    @Valid
    @AttributeOverride(
            name = "year",
            column = @Column(name = "production_year"))
    private final Year productionYear;

    @NotBlank(message = "Description can not be blank.")
    private final String description;

    @Embedded
    @Valid
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