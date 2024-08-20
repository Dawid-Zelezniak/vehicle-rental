package com.vehicle.rental.zelezniak.util.validation;

import com.vehicle.rental.zelezniak.constants.ValidationMessages;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class InputValidator {

    public static final String VEHICLE_ID_NOT_NULL = "Vehicle id" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String VEHICLE_NOT_NULL = "Vehicle" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String CLIENT_ID_NOT_NULL = "Client id" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String CLIENT_NOT_NULL = "Client" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String CLIENT_EMAIL_NOT_NULL = "Client email" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String RENT_ID_NOT_NULL = "Rent id" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String RENT_NOT_NULL = "Rent" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String RESERVATION_ID_NOT_NULL = "Reservation id" + ValidationMessages.CAN_NOT_BE_NULL;
    public static final String RESERVATION_NOT_NULL = "Reservation " + ValidationMessages.CAN_NOT_BE_NULL;

    public <T> void throwExceptionIfObjectIsNull(T input, String message) {
        if (isNull(input)) {
            throwException(message);
        }
    }

    private void throwException(String message) {
        throw new IllegalArgumentException(message);
    }
}
