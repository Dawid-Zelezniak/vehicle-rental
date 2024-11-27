package com.vehicle.rental.zelezniak.common_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

@Embeddable
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor(force = true)
public class RentDuration {

    @NotNull(message = "Rental start" + CAN_NOT_BE_NULL)
    private final LocalDateTime rentalStart;

    @NotNull(message = "Rental end" + CAN_NOT_BE_NULL)
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
