package com.vehicle.rental.zelezniak.reservation_domain.model.util;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

/**
 * Instance of this class is used to create a reservation in status NEW.
 */
@Getter
@Setter
@AllArgsConstructor
public class ReservationCreationRequest {


    @Min(value = 1, message = "Client id can not be lower than 1")
    private Long clientId;

    @NotNull(message = "Rent duration" + CAN_NOT_BE_NULL)
    private RentDuration duration;
}
