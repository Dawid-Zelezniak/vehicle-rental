package com.vehicle.rental.zelezniak.payment.controller;

import com.vehicle.rental.zelezniak.payment.model.PaymentInfo;
import com.vehicle.rental.zelezniak.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/pay")
    public String pay(@RequestBody PaymentInfo info) {
        return service.processThePayment(info);
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam Long reservationId) {
        return "Reservation with id:" + reservationId + " has been successfully paid.";
    }

    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam Long reservationId) {
        return "Error during payment of reservation with id:" + reservationId;
    }
}
