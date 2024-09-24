package com.vehicle.rental.zelezniak.vehicle.model.vehicles;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

@Entity
@Table(name = "cars")
@SuperBuilder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Car extends Vehicle {

    @NotNull(message = "Number of doors" + CAN_NOT_BE_NULL)
    private Integer doorsNumber;

    @Enumerated(EnumType.STRING)
    private DriveType driveType;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Override
    public String toString() {
        return "Car{" +
                "doorsNumber=" + doorsNumber +
                ", driveType=" + driveType +
                ", bodyType=" + bodyType +
                '}' + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Car car = (Car) o;
        return Objects.equals(doorsNumber, car.doorsNumber)
                && driveType == car.driveType
                && bodyType == car.bodyType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                doorsNumber,
                driveType,
                bodyType);
    }

    public enum DriveType {
        FOUR_WHEEL_DRIVE,
        FRONT_WHEEL_DRIVE,
        REAR_WHEEL_DRIVE,
    }

    public enum BodyType {
        SUV,
        SEDAN,
        COUPE,
        HATCHBACK
    }
}
