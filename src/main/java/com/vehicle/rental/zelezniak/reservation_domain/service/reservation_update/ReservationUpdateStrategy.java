package com.vehicle.rental.zelezniak.reservation_domain.service.reservation_update;

import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;

public interface ReservationUpdateStrategy<T> {

    Reservation update(Reservation existing,T newData);
}
