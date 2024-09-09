package com.vehicle.rental.zelezniak.user_domain.service.authentication;

import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.user_domain.model.client.Role;
import com.vehicle.rental.zelezniak.user_domain.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user_domain.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user_domain.repository.RoleRepository;
import com.vehicle.rental.zelezniak.user_domain.service.ClientValidator;
import com.vehicle.rental.zelezniak.util.TimeFormatter;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class RegistrationService {

    private static final String ROLE_USER = "USER";

    private final ClientRepository repository;
    private final PasswordEncoder encoder;
    private final InputValidator inputValidator;
    private final RoleRepository roleRepository;
    private final ClientValidator clientValidator;

    Client registerUser(Client client) {
        validateData(client);
        saveClient(client);
        return client;
    }

    private void validateData(Client client) {
        inputValidator.throwExceptionIfObjectIsNull(client, InputValidator.CLIENT_NOT_NULL);
        EmailPatternValidator.validate(client.getEmail());
        clientValidator.ifUserExistsThrowException(client.getEmail());
    }

    private void saveClient(Client client) {
        setRequiredDataAndEncodePassword(client);
        repository.save(client);
    }

    private void setRequiredDataAndEncodePassword(Client client) {
        client.setCreatedAt(TimeFormatter.getFormattedActualDateTime());
        String encoded = encoder.encode(client.getPassword());
        client.setCredentials(new UserCredentials(client.getEmail(), encoded));
        handleAddRoleForUser(client);
    }

    private void handleAddRoleForUser(Client client) {
        Role roleUser = findOrCreateRoleUser();
        client.addRole(roleUser);
    }

    private Role findOrCreateRoleUser() {
        Role role = roleRepository.findByRoleName(ROLE_USER);
        if (role == null) {
            role = new Role(ROLE_USER);
            roleRepository.save(role);
        }
        return role;
    }
}
