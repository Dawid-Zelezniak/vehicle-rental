package com.vehicle.rental.zelezniak.user.service;

import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.Role;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.repository.UserRepository;
import com.vehicle.rental.zelezniak.user.repository.RoleRepository;
import com.vehicle.rental.zelezniak.util.validation.EmailPatternValidator;
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

    private static final String ROLE_ADMIN = Role.ADMIN;

    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.email}")
    private String adminEmail;

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public void createAdmin() {
        EmailPatternValidator.validate(adminEmail);
        Optional<User> byCredentialsEmail = repository.findByCredentialsEmail(adminEmail);
        if (byCredentialsEmail.isEmpty()) {
            createAndSave();
        }
    }

    private void createAndSave() {
        User user = new User();
        Role roleAdmin = findOrCreateRoleAdmin();
        user.addRole(roleAdmin);
        String encoded = encoder.encode(adminPassword);
        user.setCredentials(new UserCredentials(adminEmail, encoded));
        User saved = repository.save(user);
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
