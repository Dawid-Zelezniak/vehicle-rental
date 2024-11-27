package com.vehicle.rental.zelezniak.payment.service;

import com.vehicle.rental.zelezniak.payment.model.PaymentInfo;
import com.vehicle.rental.zelezniak.reservation.service.validation.ReservationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentStrategyFactory strategyFactory;
    private final ReservationValidator validator;

    public String completeThePayment(PaymentInfo info) {
        validator.validateReservationDataBeforePayment(info.reservationId());
        PaymentProvider provider = strategyFactory.pickStrategy(info);
        return provider.initiatePayment(info);
    }

}
