package com.vehicle.rental.zelezniak.payment.provider;

import com.vehicle.rental.zelezniak.payment.model.PaymentInfo;

public interface PaymentMethod {

    /**
     Initiates payment - returns URL for redirection
     */
    String initiatePayment(PaymentInfo paymentInfo);
}
