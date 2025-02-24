package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.PhoneNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneNumberTest {

    @Test
    void shouldTestPhoneNumberIsValid() {
        String[] validNumbers = {
                "+1 123456789",
                "+12 123456789",
                "+123 123456789",
                "+48 123456789",
                "+49 123456789",
                "+44 123456789",
                "+55 123456789",
                "+91 123456789",
                "+81 123456789",
                "+1 800123456",
                "+36 612345678",
                "+34 612345678",
                "+39 612345678",
                "+81 3012345678"
        };

        for (String validNumber : validNumbers) {
            assertDoesNotThrow(() -> new PhoneNumber(validNumber));
        }
    }

    @Test
    void shouldTestPhoneNumberIsInvalid() {
        String[] invalidNumbers = {
                "1234567890",
                "+1234567890",
                "+49 123",
                "+1 1234567890123456",
                "+123 abcdefghij",
                "+49 1234/5678",
                "+123 4567890 12345 678",
                "",
                "+12345 123456789",
                "+3 1234567890123456",
                "+4 123456"
        };

        for (String invalidNumber : invalidNumbers) {
            assertThrows(IllegalArgumentException.class, () -> {
                new PhoneNumber(invalidNumber);
            });
        }
    }
}
