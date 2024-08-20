package com.vehicle.rental.zelezniak.user_domain.service.authentication;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Class for generating RSA key pairs.
 */
public class CustomKeyPairGenerator {

    private static final String RSA_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;

    static KeyPair generateRSAKey(){
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("The specified algorithm is not available: " + RSA_ALGORITHM, e);
        }
        return keyPair;
    }
}
