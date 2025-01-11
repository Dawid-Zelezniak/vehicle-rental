package com.vehicle.rental.zelezniak.security;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.Role;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AccessValidator {

    private static final int ROLE_ADMIN_ID = 2;
    private final ClientService service;

    public void validateClientAccess(Long unconfirmedId, Principal p) {
        String name = p.getName();
        Client currentUser = service.findByEmail(name);
        Set<Role> roles = currentUser.getRoles();
        boolean hasAdminRole = roles.contains(new Role(ROLE_ADMIN_ID, "ADMIN"));

        if (idsNotSame(unconfirmedId, currentUser) && !hasAdminRole) {
            throw new AccessDeniedException("You are not authorized to update another client data.");
        }
    }

    private static boolean idsNotSame(Long unconfirmedId, Client currentUser) {
        return !currentUser.getId().equals(unconfirmedId);
    }
}
