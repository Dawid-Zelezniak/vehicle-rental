package com.vehicle.rental.zelezniak.security.validation;

import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.Role;
import com.vehicle.rental.zelezniak.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AccessValidator {

    private static final int ROLE_ADMIN_ID = 2;
    private final UserService service;

    public void validateUserAccess(UserAccess access) {
        String name = access.getPrincipalName();
        User currentUser = service.findByEmail(name);
        Set<Role> roles = currentUser.getRoles();
        boolean hasAdminRole = roles.contains(new Role(ROLE_ADMIN_ID, "ADMIN"));

        if (idsNotSame(access.unconfirmedId(), currentUser) && !hasAdminRole) {
            throw new AccessDeniedException(access.exceptionMessage());
        }
    }

    private static boolean idsNotSame(Long unconfirmedId, User currentUser) {
        return !currentUser.getId().equals(unconfirmedId);
    }
}
