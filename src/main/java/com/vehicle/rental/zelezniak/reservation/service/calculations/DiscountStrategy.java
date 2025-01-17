package com.vehicle.rental.zelezniak.reservation.service.calculations;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DiscountStrategy {

    private static final BigDecimal FIVE_PERCENT_DISCOUNT = BigDecimal.valueOf(0.05);
    private static final BigDecimal TEN_PERCENT_DISCOUNT = BigDecimal.valueOf(0.1);
    private static final int FIVE_DAYS = 5;
    private static final int TEN_DAYS = 10;

    BigDecimal determineDiscount(Integer days) {
        if (days <= FIVE_DAYS) {
            return BigDecimal.ZERO;
        } else if (days <= TEN_DAYS) {
            return FIVE_PERCENT_DISCOUNT;
        } else return TEN_PERCENT_DISCOUNT;
    }
}
