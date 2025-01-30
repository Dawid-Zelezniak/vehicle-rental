package com.vehicle.rental.zelezniak.payment.service;

import com.vehicle.rental.zelezniak.payment.model.PaymentInfo;

public interface PaymentMethod {

    /**
     Initiates payment - returns URL for redirection
     */
    String initiatePayment(PaymentInfo paymentInfo);
}
