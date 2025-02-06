package com.vehicle.rental.zelezniak.payment.provider.stripe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StripeWebhookEvent {

    private String id;
    private String type;
    @JsonProperty("data")
    private StripeEventData eventData;

    @Data
    public static class StripeEventData {
        @JsonProperty("object")
        private StripeObject stripeObject;
    }

    @Data
    public static class StripeObject {
        private String id;
        private String object;
        private long amount;
        private String currency;
        private String status;

        @JsonProperty("payment_intent")
        private String paymentIntent;
        @JsonProperty("metadata")
        private Metadata metadata;
    }

    @Data
    public static class Metadata {
        @JsonProperty("reservation_id")
        private Object reservationId;
    }
}