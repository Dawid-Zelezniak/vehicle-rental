package com.vehicle.rental.zelezniak.common_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor(force = true)
public class RentDuration {

    @NotNull(message = "Rental start can not be null.")
    private final LocalDateTime rentalStart;

    @NotNull(message = "Rental end can not be null.")
    private final LocalDateTime rentalEnd;

    public RentDuration(LocalDateTime start, LocalDateTime end) {
        validateArguments(start, end);
        this.rentalStart = start;
        this.rentalEnd = end;
    }

    private void validateArguments(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Rental start can not be after rental end.");
        }
    }
}
