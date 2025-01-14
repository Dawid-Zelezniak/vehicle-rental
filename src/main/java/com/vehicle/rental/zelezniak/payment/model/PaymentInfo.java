package com.vehicle.rental.zelezniak.payment.model;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentInfo(
        @NotNull(message = "Currency can not be null.")
        String currency,
        @NotNull(message = "To pay can not be null.")
        Money toPay,
        @NotNull(message = "Payment method can not be null.")
        String paymentMethod,
        @Min(value = 0, message = "Reservation id can not be lower than 0.")
        Long reservationId) {
}
