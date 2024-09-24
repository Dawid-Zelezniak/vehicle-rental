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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
class RegistrationService {

    private static final String ROLE_USER = "USER";

    private final ClientRepository repository;
    private final PasswordEncoder encoder;
    private final InputValidator inputValidator;
    private final RoleRepository roleRepository;
    private final ClientValidator clientValidator;

    @Transactional
    public Client registerUser(Client client) {
        String email = client.getEmail();
        log.debug("Starting registration process for client: {}", email);
        validateData(client);
        saveClient(client);
        log.info("Client: {} has been registered", email);
        return client;
    }

    private void validateData(Client client) {
        inputValidator.throwExceptionIfObjectIsNull(client, InputValidator.CLIENT_NOT_NULL);
        EmailPatternValidator.validate(client.getEmail());
        clientValidator.validateUserDoesNotExists(client.getEmail());
    }

    private void saveClient(Client client) {
        setRequiredDataAndEncodePassword(client);
        log.info("Saving new client to database");
        repository.save(client);
    }

    private void setRequiredDataAndEncodePassword(Client client) {
        client.setCreatedAt(TimeFormatter.getFormattedActualDateTime());
        String encoded = encoder.encode(client.getPassword());
        client.setCredentials(new UserCredentials(client.getEmail(), encoded));
        Role roleUser = findOrCreateRoleUser();
        client.addRole(roleUser);
    }

    private Role findOrCreateRoleUser() {
        Role role = roleRepository.findByRoleName(ROLE_USER);
        if (role == null) {
            role = roleRepository.save(new Role(ROLE_USER));
            log.info("Role USER has been created");
        }
        return role;
    }
}
