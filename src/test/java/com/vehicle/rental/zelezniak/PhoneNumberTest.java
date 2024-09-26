package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.PhoneNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class PhoneNumberTest {

    @Test
    void shouldTestPhoneNumberIsValid() {
        String[] validNumbers = {
                "+1234567890",
                "+491234567890",
                "+447911123456",
                "+14155552671",
                "+33123456789",
                "+61234567890",
                "+819012345678",
                "+919876543210",
                "+5511987654321",
                "+27831234567"
        };

        for (String validNumber : validNumbers) {
            assertDoesNotThrow(() -> new PhoneNumber(validNumber));
        }
    }

    @Test
    void shouldTestPhoneNumberIsInvalid() {
        String[] invalidNumbers = {
                "1234567890",
                "+123",
                "+1234567890123456",
                "491234567890",
                "+49123abc7890",
                "+4912345678/90",
                "+(123)4567890",
                ""
        };

        for (String invalidNumber : invalidNumbers) {
            assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(invalidNumber));
        }
    }
}
