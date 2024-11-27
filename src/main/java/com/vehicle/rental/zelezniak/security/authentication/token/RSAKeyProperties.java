package com.vehicle.rental.zelezniak.security.authentication.token;

import lombok.*;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Setter
@Component
public class RSAKeyProperties {

private RSAPublicKey publicKey;
private RSAPrivateKey privateKey;

    public RSAKeyProperties() {
        KeyPair keyPair = CustomKeyPairGenerator.generateRSAKey();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }
}
