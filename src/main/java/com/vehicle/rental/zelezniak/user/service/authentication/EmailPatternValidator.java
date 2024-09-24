package com.vehicle.rental.zelezniak.user.service.authentication;


import java.util.regex.Pattern;

public class EmailPatternValidator {

    private static final String EMAIL_PATTERN =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";


    private EmailPatternValidator(){

    }

    public static void validate(String email) {
        if (doesNotMatch(email)) {
            throwException(email);
        }
    }

    private static boolean doesNotMatch(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        boolean matches = pattern.matcher(email).matches();
        return !matches;
    }

    private static void throwException(String email) {
        throw new IllegalArgumentException(
                "Email " + email + " has invalid pattern.");
    }
}
