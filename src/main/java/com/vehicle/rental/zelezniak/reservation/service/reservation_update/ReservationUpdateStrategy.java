package com.vehicle.rental.zelezniak.reservation.service.reservation_update;

import com.vehicle.rental.zelezniak.reservation.model.Reservation;

public interface ReservationUpdateStrategy<T> {

    Reservation update(Reservation existing,T newData);
}
