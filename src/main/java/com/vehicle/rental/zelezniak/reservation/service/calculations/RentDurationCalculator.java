package com.vehicle.rental.zelezniak.reservation.service.calculations;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
class RentDurationCalculator {

    private static final int END_DAY = 1;

    Integer calculateDuration(Reservation reservation) {
        var rentInformation = reservation.getRentInformation();
        RentDuration duration = rentInformation.getRentDuration();
        LocalDateTime rentalStart = duration.getRentalStart();
        LocalDateTime rentalEnd = duration.getRentalEnd();
        long days = ChronoUnit.DAYS.between(rentalStart, rentalEnd) + END_DAY;
        return (int) days;
    }
}
