package com.vehicle.rental.zelezniak;


import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class InputValidatorTest {

    @Autowired
    private InputValidator inputValidator;

    @Test
    void shouldThrowException() {
        String given = null;
        assertThrows(IllegalArgumentException.class,
                () -> inputValidator.throwExceptionIfObjectIsNull(given,"Should not be a null."));
    }

    @Test
    void shouldNotThrowException() {
        String given = "string";
        assertDoesNotThrow(() -> inputValidator.throwExceptionIfObjectIsNull(given,"Should not be a null."));
    }
}
