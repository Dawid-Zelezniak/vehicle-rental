package com.vehicle.rental.zelezniak.user.model.client.user_value_objects;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PhoneNumber {

    private static final String E164_REGEX = "^\\+\\d{1,3}\\s\\d{7,15}$";
    private String number;

    public PhoneNumber(String number) {
        if (!isValid(number)) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        this.number = number;
    }

    private boolean isValid(String number) {
        Pattern pattern = Pattern.compile(E164_REGEX);
        return pattern.matcher(number).matches();
    }

    @Override
    public String toString() {
        return number;
    }
}
