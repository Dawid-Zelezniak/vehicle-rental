package com.vehicle.rental.zelezniak.common_value_objects;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.*;

@Embeddable
public record Money(BigDecimal value) {

    private static final BigDecimal ZERO = BigDecimal.ZERO;


    public Money(BigDecimal value) {
        validate(value);
        this.value = format(value);
    }

    private void validate(BigDecimal money) {
        if (money == null || isLowerThanZero(money)) {
            throwException();
        }
    }

    private boolean isLowerThanZero(BigDecimal money) {
        return money.compareTo(ZERO) < 0;
    }

    private void throwException() {
        throw new IllegalArgumentException(
                "Money value is incorrect");
    }

    private BigDecimal format(BigDecimal money) {
        return money.setScale(2, RoundingMode.HALF_UP);
    }
}
