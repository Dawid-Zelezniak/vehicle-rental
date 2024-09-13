package com.vehicle.rental.zelezniak.payment_domain.model;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

public record PaymentInfo(
        @NotNull(message = "Currency" + CAN_NOT_BE_NULL)
        String currency,
        @NotNull(message = "To pat" + CAN_NOT_BE_NULL)
        Money toPay,
        @NotNull(message = "Payment method" + CAN_NOT_BE_NULL)
        String paymentMethod,
        @Min(value = 0, message = "Reservation id can not be lower than 0.")
        Long reservationId) {
}
