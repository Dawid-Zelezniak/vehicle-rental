package com.vehicle.rental.zelezniak.reservation.service;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import org.springframework.stereotype.Component;

/**
 * Class responsible for building new reservations
 * */
@Component
class NewReservationBuilder {

    public Reservation build(Client client, RentDuration duration) {
        RentInformation information = buildRentInformation(duration);
        return buildReservation(information, client);
    }

    private RentInformation buildRentInformation(RentDuration duration) {
        return RentInformation.builder()
                .rentDuration(duration)
                .build();
    }

    private Reservation buildReservation(RentInformation information, Client client) {
        return Reservation.builder()
                .client(client)
                .reservationStatus(Reservation.ReservationStatus.NEW)
                .rentInformation(information)
                .build();
    }
}
