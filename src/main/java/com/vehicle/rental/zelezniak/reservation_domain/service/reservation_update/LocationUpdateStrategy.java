package com.vehicle.rental.zelezniak.reservation_domain.service.reservation_update;

import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;

/**
 * Updates the pickup and drop-off location information in the customer's reservation.
 */
public class LocationUpdateStrategy implements ReservationUpdateStrategy<Reservation> {

    public Reservation update(Reservation existing, Reservation newData) {
        return existing.toBuilder()
                .rentInformation(updateLocation(existing, newData))
                .build();
    }

    private RentInformation updateLocation(Reservation existing, Reservation newData) {
        RentInformation rentInformation = existing.getRentInformation();
        RentInformation newRentInformation = newData.getRentInformation();

        return rentInformation.toBuilder()
                .pickUpLocation(newRentInformation.getPickUpLocation())
                .dropOffLocation(newRentInformation.getDropOffLocation())
                .build();
    }
}
