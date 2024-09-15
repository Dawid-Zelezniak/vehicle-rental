package com.vehicle.rental.zelezniak.payment_domain.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.vehicle.rental.zelezniak.payment_domain.model.PaymentInfo;
import com.vehicle.rental.zelezniak.payment_domain.service.PaymentProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StripePayment implements PaymentProvider {

    @Value("${stripe.test-secret.key}")
    private String secretKey;

    @PostConstruct
    void initSecretKey() {
        Stripe.apiKey = secretKey;
    }

    public String initiatePayment(PaymentInfo paymentInfo) {
        Long id = paymentInfo.reservationId();
        SessionCreateParams reservationPayment = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/payments/success?reservationId=" + id)
                .setCancelUrl("http://localhost:8080/payments/cancel?reservationId=" + id)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(paymentInfo.currency())
                                                .setUnitAmount(paymentInfo.toPay().convertToCents())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Reservation payment")
                                                                .build()
                                                ).build()
                                ).setQuantity(1L)
                                .build()
                )
                .putMetadata("reservation_id", paymentInfo.reservationId().toString())
                .build();
        Session session = null;
        try {
            session = Session.create(reservationPayment);
        } catch (StripeException e) {
            log.error("Exception in stripe payment:{}", e.getMessage());
            throw new IllegalArgumentException("Error while creating Stripe payment:", e);
        }
        return session.getUrl();
    }
}



