package com.vehicle.rental.zelezniak.reservation_domain.model.util;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;
import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import org.springframework.stereotype.Component;

@Component
public class NewReservationBuilder {

    public Reservation build(Client client, RentDuration duration) {
        RentInformation information = RentInformation.builder()
                .rentDuration(duration)
                .build();
        return Reservation.builder()
                .client(client)
                .reservationStatus(Reservation.ReservationStatus.NEW)
                .rentInformation(information)
                .build();
    }
}
