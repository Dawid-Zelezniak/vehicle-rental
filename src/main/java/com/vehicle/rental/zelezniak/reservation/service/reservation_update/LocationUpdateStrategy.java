package com.vehicle.rental.zelezniak.reservation.service.reservation_update;

import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import lombok.extern.slf4j.Slf4j;

/**
 * Updates the pickup and drop-off location information in the customer's reservation.
 */
@Slf4j
public class LocationUpdateStrategy implements ReservationUpdateStrategy<Reservation> {

    public Reservation update(Reservation existing, Reservation newData) {
        log.info("Updating location for reservation with id : {}", existing.getId());
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
