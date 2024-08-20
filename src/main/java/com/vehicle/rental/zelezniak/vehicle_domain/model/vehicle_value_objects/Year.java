package com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;


@Embeddable
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(force = true)
public class Year {

    private final int year;

    public Year(int year) {
        validate(year);
        this.year = year;
    }

    private void validate(int year) {
        if(isInFuture(year) || year < 1900){
            throwException();
        }
    }

    private boolean isInFuture(int year) {
        return year > LocalDate.now().getYear();
    }

    private void throwException() {
    throw new IllegalArgumentException("Year incorrect.");
    }
}
