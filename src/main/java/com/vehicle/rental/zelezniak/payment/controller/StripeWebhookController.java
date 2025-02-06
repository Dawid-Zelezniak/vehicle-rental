package com.vehicle.rental.zelezniak.payment.controller;

import com.vehicle.rental.zelezniak.payment.provider.stripe.StripeWebhookService;
import com.vehicle.rental.zelezniak.payment.provider.stripe.dto.StripeWebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private static final String PAYMENT_SUCCEEDED = "payment_intent.succeeded";

    private final StripeWebhookService webhookService;

    @PostMapping
    public void handle(@RequestBody StripeWebhookEvent event) {
        if (event.getEventData() == null || event.getEventData().getStripeObject() == null) {
            log.error("Received event with missing data: {}", event);
            throw new IllegalArgumentException("Stripe event has missing data.");
        }
        String eventType = event.getType();
        if (PAYMENT_SUCCEEDED.equals(eventType)) {
            StripeWebhookEvent.StripeEventData eventData = event.getEventData();
            webhookService.handleWebhook(eventData);
        }
    }
}
