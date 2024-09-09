package com.vehicle.rental.zelezniak.payment_domain.model;

import com.vehicle.rental.zelezniak.common_value_objects.Money;

public record PaymentInfo(String currency,
                          Money toPay) {
}
