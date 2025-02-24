package com.vehicle.rental.zelezniak.reservation.service;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.user.model.user.User;
import org.springframework.stereotype.Component;

/**
 * Class responsible for building new reservations
 * */
@Component
class NewReservationBuilder {

    public Reservation build(User user, RentDuration duration) {
        RentInformation information = buildRentInformation(duration);
        return buildReservation(information, user);
    }

    private RentInformation buildRentInformation(RentDuration duration) {
        return RentInformation.builder()
                .rentDuration(duration)
                .build();
    }

    private Reservation buildReservation(RentInformation information, User user) {
        return Reservation.builder()
                .user(user)
                .reservationStatus(Reservation.ReservationStatus.NEW)
                .rentInformation(information)
                .build();
    }
}
