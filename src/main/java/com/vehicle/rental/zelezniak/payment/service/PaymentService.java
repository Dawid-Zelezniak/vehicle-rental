package com.vehicle.rental.zelezniak.payment.service;

import com.vehicle.rental.zelezniak.payment.model.PaymentInfo;
import com.vehicle.rental.zelezniak.payment.provider.PaymentMethod;
import com.vehicle.rental.zelezniak.payment.provider.PaymentProvider;
import com.vehicle.rental.zelezniak.reservation.service.validation.ReservationPaymentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProvider provider;
    private final ReservationPaymentValidator validator;

    public String processThePayment(PaymentInfo info) {
        validator.validateReservationDataBeforePayment(info.reservationId());
        PaymentMethod method = provider.pickPaymentProvider(info.paymentMethod());
        return method.initiatePayment(info);
    }

}
