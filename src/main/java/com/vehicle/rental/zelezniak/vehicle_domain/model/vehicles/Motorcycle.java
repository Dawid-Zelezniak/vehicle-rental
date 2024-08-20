package com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(name = "motorcycles")
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Motorcycle extends Vehicle {

    @Enumerated(EnumType.STRING)
    private MotorcycleType motorcycleType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Motorcycle that = (Motorcycle) o;
        return motorcycleType == that.motorcycleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                motorcycleType);
    }

    @Override
    public String toString() {
        return "Motorcycle{" +
                "motorcycleType=" + motorcycleType +
                '}' + super.toString();
    }

    public enum MotorcycleType {
        SPORT,
        CRUISER,
        CHOPPER,
        BOBBER
    }
}
