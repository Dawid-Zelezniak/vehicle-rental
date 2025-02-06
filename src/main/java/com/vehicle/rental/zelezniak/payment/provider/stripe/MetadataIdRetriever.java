package com.vehicle.rental.zelezniak.payment.provider.stripe;

import com.vehicle.rental.zelezniak.payment.provider.stripe.dto.StripeWebhookEvent;

import static java.util.Objects.isNull;

public class MetadataIdRetriever {

    public static Long getReservationIdFromMetadata(StripeWebhookEvent.StripeObject stripeObject) {
        StripeWebhookEvent.Metadata metadata = stripeObject.getMetadata();
        Object reservationId = metadata.getReservationId();
        if (isNull(reservationId)) {
            throw new IllegalArgumentException("Reservation id not found in metadata");
        }
        try {
            return reservationId instanceof String string ?
                    Long.parseLong(string) :
                    (Long) reservationId;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid id format in metadata");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Can not cast " + reservationId.getClass() + " to number");
        }
    }
}
