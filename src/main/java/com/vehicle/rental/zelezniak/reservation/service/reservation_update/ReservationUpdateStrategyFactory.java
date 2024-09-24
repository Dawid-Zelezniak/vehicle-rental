package com.vehicle.rental.zelezniak.reservation.service.reservation_update;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationUpdateStrategyFactory {

    public <T> ReservationUpdateStrategy<T> getStrategy(Class<T> type) {
        if (type.equals(Reservation.class)) {
            return (ReservationUpdateStrategy<T>) new LocationUpdateStrategy();
        } else if (type.equals(RentDuration.class)) {
            return (ReservationUpdateStrategy<T>) new RentDurationUpdateStrategy();
        } else throw new IllegalArgumentException("Unsupported type for update strategy");
    }
}
