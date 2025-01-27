package com.vehicle.rental.zelezniak.reservation.service.reservation_update;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import org.springframework.stereotype.Component;

@Component
public class ReservationUpdateStrategyProvider {

    public <T> ReservationUpdateStrategy<T> getStrategy(Class<T> type) {
        if (type.equals(RentInformation.class)) {
            return (ReservationUpdateStrategy<T>) new LocationUpdateStrategy();
        } else if (type.equals(RentDuration.class)) {
            return (ReservationUpdateStrategy<T>) new RentDurationUpdateStrategy();
        } else throw new IllegalArgumentException("Unsupported type for update strategy");
    }
}
