package com.vehicle.rental.zelezniak;


import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class InputValidatorTest {

    private InputValidator inputValidator;

    @BeforeEach
    void initialize() {
        inputValidator = new InputValidator();
    }

    @Test
    void shouldThrowException() {
        String given = null;
        assertThrows(IllegalArgumentException.class,
                () -> inputValidator.throwExceptionIfObjectIsNull(given, "Should not be a null."));
    }

    @Test
    void shouldNotThrowException() {
        String given = "string";
        assertDoesNotThrow(() -> inputValidator.throwExceptionIfObjectIsNull(given, "Should not be a null."));
    }
}
