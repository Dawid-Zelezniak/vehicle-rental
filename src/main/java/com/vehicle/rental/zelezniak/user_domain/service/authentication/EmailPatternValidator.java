package com.vehicle.rental.zelezniak.user_domain.service.authentication;


public class EmailPatternValidator {

    private static final String EMAIL_PATTERN =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";


    public static void validate(String email){
        if(doesNotMatch(email)){
            throwException(email);
        }
    }

    private static boolean doesNotMatch(String email) {
        return email == null || !email.matches(EMAIL_PATTERN);
    }

    private static void throwException(String email) {
    throw new IllegalArgumentException(
            "Email " + email + " has invalid pattern.");
    }
}
