package com.vehicle.rental.zelezniak.payment.provider.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vehicle.rental.zelezniak.payment.model.PaymentInfo;
import com.vehicle.rental.zelezniak.payment.provider.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StripePayment implements PaymentMethod {

    private static final String PAYMENT_DESCRIPTION = "Reservation payment";
    private static final String METADATA_KEY = "reservation_id";
    private final String successUrl;
    private final String cancelUrl;

    public StripePayment(@Value("${stripe.test-secret.key}") String secretKey,
                         @Value("${stripe.success-url}") String successUrl,
                         @Value("${stripe.cancel-url}") String cancelUrl) {
        Stripe.apiKey = secretKey;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
    }

    public String initiatePayment(PaymentInfo paymentInfo) {
        Long id = paymentInfo.reservationId();
        SessionCreateParams reservationPayment = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + id)
                .setCancelUrl(cancelUrl + id)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(paymentInfo.currency())
                                                .setUnitAmount(paymentInfo.toPay().convertToCents())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(PAYMENT_DESCRIPTION)
                                                                .build()
                                                ).build()
                                ).setQuantity(1L)
                                .build()
                )
                .putMetadata(METADATA_KEY, paymentInfo.reservationId().toString())
                .build();
        try {
            Session session = Session.create(reservationPayment);
            return session.getUrl();
        } catch (StripeException e) {
            log.error("Exception in stripe payment:{}", e.getMessage());
            throw new IllegalArgumentException("Error while creating Stripe payment:", e);
        }
    }
}



