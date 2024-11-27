package com.vehicle.rental.zelezniak.util.validation;

import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class InputValidator {

    public <T> void throwExceptionIfObjectIsNull(T input, String message) {
        if (isNull(input)) {
            throwException(message);
        }
    }

    private void throwException(String message) {
        throw new IllegalArgumentException(message);
    }
}
