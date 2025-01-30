package com.vehicle.rental.zelezniak.common_value_objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
public record Money(

        @DecimalMin(value = "0.00", message = "Money value can not be lower than 0.00")
        BigDecimal value
) {

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
            throw new IllegalArgumentException("Money must be positive and can not be null.");
        }
    }

    private boolean isLowerThanZero(BigDecimal money) {
        return money.compareTo(ZERO) < 0;
    }

    private BigDecimal format(BigDecimal money) {
        return money.setScale(2, RoundingMode.HALF_UP);
    }
}
