package com.vehicle.rental.zelezniak.security.validation;

import java.security.Principal;

public record UserAccess(
        Principal principal,
        Long unconfirmedId,
        String exceptionMessage
) {

    public String getPrincipalName() {
        return principal.getName();
    }
}
