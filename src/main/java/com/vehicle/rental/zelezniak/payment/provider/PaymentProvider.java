package com.vehicle.rental.zelezniak.payment.provider;

import com.vehicle.rental.zelezniak.payment.provider.stripe.StripePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProvider {

    private final StripePayment stripePayment;

    public PaymentMethod pickPaymentProvider(String method) {
        return switch (method) {
            case "stripe" -> stripePayment;
            default -> throw new IllegalArgumentException("Unknown payment method");
        };
    }
}
