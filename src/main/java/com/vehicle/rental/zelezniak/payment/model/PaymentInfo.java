package com.vehicle.rental.zelezniak.payment.model;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentInfo(
        @NotNull(message = "Currency is required and cannot be null.")
        String currency,
        @NotNull(message = "The amount to pay is required and cannot be null.")
        Money toPay,
        @NotNull(message = "Please specify a payment method.")
        String paymentMethod,
        @Min(value = 1, message = "Reservation ID must be 1 or greater.")
        Long reservationId) {
}

