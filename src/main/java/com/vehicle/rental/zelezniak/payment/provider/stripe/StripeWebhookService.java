package com.vehicle.rental.zelezniak.payment.service.stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Class responsible for handling webhook events from Stripe.
 */
// add tests
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final ReservationRepository repository;
    private final ReservationService service;

    @Value("${stripe.endpoint.secret}")
    private String endpointSecret;

    @Async
    public void handleWebhook(String payload, String signatureHeader) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, signatureHeader, endpointSecret);
        handleEvent(event);
    }

    private void handleEvent(Event event) {
        String eventType = event.getType();
        if ("payment_intent.succeeded" .equals(eventType)) {
            handlePaymentIntentSucceeded(event);
        }else {
            log.warn("Unhandled event type: {}", eventType);
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        var deserializer = event.getDataObjectDeserializer();
        Optional<StripeObject> object = deserializer.getObject();
        if (object.isPresent()) {
            StripeObject stripeObject = object.get();
            service.setReservationStatusAsACTIVE(stripeObject);
            // send email and sms confirmation
        } else {
            log.error("Stripe object is not present in the event.");
            throw new IllegalArgumentException("Stripe object is not present in the event.");
        }
    }
}
