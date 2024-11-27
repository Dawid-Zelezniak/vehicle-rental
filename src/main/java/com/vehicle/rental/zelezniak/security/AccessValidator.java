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

    private final ClientService service;

    public void validateClientAccess(Long toUpdateUserId, Principal p) {
        String name = p.getName();
        Client currentUser = service.findByEmail(name);
        Set<Role> roles = currentUser.getRoles();
        boolean isAdmin = roles.contains(new Role(2, "ADMIN"));

        if (!currentUser.getId().equals(toUpdateUserId) && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to update another client data.");
        }
    }
}
