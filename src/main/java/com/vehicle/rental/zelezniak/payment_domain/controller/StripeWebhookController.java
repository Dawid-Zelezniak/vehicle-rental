package com.vehicle.rental.zelezniak.payment_domain.controller;

import com.stripe.exception.SignatureVerificationException;
import com.vehicle.rental.zelezniak.payment_domain.service.stripe.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final StripeWebhookService webhookService;

    @PostMapping
    public void handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signatureHeader) {
        try {
            webhookService.handleWebhook(payload, signatureHeader);
        } catch (SignatureVerificationException e) {
            throw new IllegalArgumentException("Invalid signature.");
        }
    }
}
