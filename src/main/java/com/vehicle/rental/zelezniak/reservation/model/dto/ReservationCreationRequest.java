package com.vehicle.rental.zelezniak.reservation.model.dto;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Instance of this class is used to create a reservation in status NEW.
 */
public record ReservationCreationRequest(
        @Min(value = 1, message = "Client id can not be lower than 1")
        Long clientId,
        @NotNull(message = "Rent duration can not be null.")
        RentDuration duration) {

}
