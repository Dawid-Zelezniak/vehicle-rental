package com.vehicle.rental.zelezniak.reservation_domain.service.reservation_update;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;

/**
 * Updates the rental duration information in the customer's reservation.
 */
public class RentDurationUpdateStrategy implements ReservationUpdateStrategy<RentDuration> {

    public Reservation update(Reservation reservation, RentDuration duration) {
        RentInformation updated = updateDuration(reservation, duration);
        return reservation.toBuilder()
                .rentInformation(updated)
                .build();
    }

    private RentInformation updateDuration(Reservation reservation, RentDuration duration) {
        var information = reservation.getRentInformation();
        return information.toBuilder()
                .rentDuration(duration)
                .build();
    }
}
