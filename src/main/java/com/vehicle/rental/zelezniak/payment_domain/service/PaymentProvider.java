package com.vehicle.rental.zelezniak.payment_domain.service;

import com.vehicle.rental.zelezniak.payment_domain.model.PaymentInfo;

public interface PaymentProvider  {

    /**
     Initiates payment - returns URL for redirection
     */
    String initiatePayment(PaymentInfo paymentInfo);
}
