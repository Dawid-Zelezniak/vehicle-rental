package com.vehicle.rental.zelezniak.security.authentication;

import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.Role;
import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.repository.UserRepository;
import com.vehicle.rental.zelezniak.user.repository.RoleRepository;
import com.vehicle.rental.zelezniak.user.service.UserMapper;
import com.vehicle.rental.zelezniak.user.service.validation.UserValidator;
import com.vehicle.rental.zelezniak.util.TimeFormatter;
import com.vehicle.rental.zelezniak.util.validation.EmailPatternValidator;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.USER_NOT_NULL;

@Component
@RequiredArgsConstructor
@Slf4j
class RegistrationService {

    private static final String ROLE_USER = "USER";

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final InputValidator inputValidator;
    private final RoleRepository roleRepository;
    private final UserValidator userValidator;

    @Transactional
    public UserDto registerUser(User user) {
        String email = user.getEmail();
        log.info("Starting registration process for client: {}", email);
        validateData(user);
        saveClient(user);
        log.info("Client: {} has been registered", email);
        return UserMapper.toDto(user);
    }

    private void validateData(User user) {
        inputValidator.throwExceptionIfObjectIsNull(user, USER_NOT_NULL);
        EmailPatternValidator.validate(user.getEmail());
        userValidator.validateUserDoesNotExists(user.getEmail());
    }

    private void saveClient(User user) {
        setRequiredDataAndEncodePassword(user);
        log.info("Saving new client to database");
        repository.save(user);
    }

    private void setRequiredDataAndEncodePassword(User user) {
        user.setCreatedAt(TimeFormatter.getFormattedActualDateTime());
        String encoded = encoder.encode(user.getPassword());
        user.setCredentials(new UserCredentials(user.getEmail(), encoded));
        Role roleUser = findOrCreateRoleUser();
        user.addRole(roleUser);
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
