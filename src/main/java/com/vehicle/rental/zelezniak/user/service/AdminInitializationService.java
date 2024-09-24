package com.vehicle.rental.zelezniak.user.service;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.Role;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user.repository.RoleRepository;
import com.vehicle.rental.zelezniak.user.service.authentication.EmailPatternValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInitializationService {

    private static final String ROLE_ADMIN = "ADMIN";

    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.email}")
    private String adminEmail;

    private final ClientRepository repository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public void createAdmin() {
        EmailPatternValidator.validate(adminEmail);
        Optional<Client> byCredentialsEmail = repository.findByCredentialsEmail(adminEmail);
        if (byCredentialsEmail.isEmpty()) {
            saveAdmin();
        }
    }

    private void saveAdmin() {
        Client client = new Client();
        Role roleAdmin = findOrCreateRoleAdmin();
        client.addRole(roleAdmin);
        String encoded = encoder.encode(adminPassword);
        client.setCredentials(new UserCredentials(adminEmail, encoded));
        Client saved = repository.save(client);
        log.info("Admin account has been created ,email: [{}],id: [{}]", saved.getEmail(), saved.getId());
    }

    private Role findOrCreateRoleAdmin() {
        Role role = roleRepository.findByRoleName(ROLE_ADMIN);
        if (role == null) {
            role = roleRepository.save(new Role(ROLE_ADMIN));
            log.info("Role ADMIN has been created.");
        }
        return role;
    }
}
