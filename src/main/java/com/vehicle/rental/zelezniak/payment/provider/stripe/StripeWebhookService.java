package com.vehicle.rental.zelezniak.payment.provider.stripe;

import com.vehicle.rental.zelezniak.payment.provider.stripe.dto.StripeWebhookEvent;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.rmi.UnexpectedException;

/**
 * Class responsible for handling webhook events from Stripe.
 */
// add tests
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final ReservationService service;

    @Value("${stripe.endpoint.secret}")
    private String endpointSecret;

    public void handleWebhook(StripeWebhookEvent.StripeEventData event) {
        log.info("Processing stripe webhook");
        try {
            StripeWebhookEvent.StripeObject stripeObject = event.getStripeObject();
            service.setReservationStatusAsACTIVE(stripeObject);
        } catch (Exception e) {
            log.error("Error processing Stripe event {}", e.getMessage());
            throw new IllegalArgumentException("Unexpected exception while handling stripe webhook", e);
        }
    }
}
