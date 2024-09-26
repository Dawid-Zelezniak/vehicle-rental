package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.user.service.authentication.EmailPatternValidator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailPatternValidatorTest {

    @Test
    void shouldTestEmailPatternIsCorrect() {
        String[] validEmails = {
                "correct@gmail.com",
                "test.email@example.com",
                "user.name+tag@example.com",
                "email@subdomain.example.com",
                "firstname.lastname@example.com",
                "email@domain.com",
                "email@domain.co.uk",
                "email@domain.name",
                "email@domain.museum",
        };

        for (String email : validEmails) {
            assertDoesNotThrow(() -> EmailPatternValidator.validate(email),
                    "Should not throw exception for valid email: " + email);
        }
    }

    @Test
    void shouldTestEmailPatternIsIncorrect() {
        String[] invalidEmails = {
                "plainaddress",
                "@missingusername.com",
                "email.example.com",
                "email@example@example.com",
                "Abc.example.com",
                "A@b@c@example.com",
                "wrongemail@gmail"
        };

        for (String email : invalidEmails) {
            assertThrows(IllegalArgumentException.class, () -> EmailPatternValidator.validate(email),
                    "Should throw exception for invalid email: " + email);
        }
    }
}
