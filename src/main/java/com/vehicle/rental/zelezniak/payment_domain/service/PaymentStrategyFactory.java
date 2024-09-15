package com.vehicle.rental.zelezniak.payment_domain.service;

import com.vehicle.rental.zelezniak.payment_domain.model.PaymentInfo;
import com.vehicle.rental.zelezniak.payment_domain.service.stripe.StripePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {

    private final StripePayment stripePayment;

    public PaymentProvider pickStrategy(PaymentInfo info) {
        String method = info.paymentMethod().toLowerCase();
        return switch (method) {
            case "stripe" -> stripePayment;
            default -> throw new IllegalArgumentException("Unknown payment method");
        };
    }
}
