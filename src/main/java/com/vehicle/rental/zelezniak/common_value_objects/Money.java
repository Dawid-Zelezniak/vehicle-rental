package com.vehicle.rental.zelezniak.common_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.*;

@Embeddable
public record Money(
        @Min(value = 0, message = "Money value can not be lower than 0.")
        BigDecimal value) {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int ONE_HUNDRED_CENTS = 100;

    public Money(BigDecimal value) {
        validate(value);
        this.value = format(value);
    }

    public long convertToCents() {
        BigDecimal cents = value.multiply(BigDecimal.valueOf(ONE_HUNDRED_CENTS));
        return cents.longValueExact();
    }

    private void validate(BigDecimal money) {
        if (money == null || isLowerThanZero(money)) {
            throw new IllegalArgumentException("Money value must not be null or negative.");
        }
    }

    private boolean isLowerThanZero(BigDecimal money) {
        return money.compareTo(ZERO) < 0;
    }

    private BigDecimal format(BigDecimal money) {
        return money.setScale(2, RoundingMode.HALF_UP);
    }
}
