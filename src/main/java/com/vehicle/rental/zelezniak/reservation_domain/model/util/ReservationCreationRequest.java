package com.vehicle.rental.zelezniak.reservation_domain.model.util;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Instance of this class is used to create a reservation in status NEW.
 */
@Getter
@Setter
@AllArgsConstructor
public class ReservationCreationRequest {

    private Long clientId;
    private RentDuration duration;
}
