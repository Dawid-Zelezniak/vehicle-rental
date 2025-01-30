package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTest {

    @Test
    void shouldNotCreateMoneyBelowZero() {
        BigDecimal incorrectValue = BigDecimal.valueOf(-1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Money(incorrectValue));
        assertEquals("Money must be positive and can not be null.", exception.getMessage());
    }

    @Test
    void shouldNotCreateMoneyWhenValueIsNull() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Money(null));
        assertEquals("Money must be positive and can not be null.", exception.getMessage());
    }
}
