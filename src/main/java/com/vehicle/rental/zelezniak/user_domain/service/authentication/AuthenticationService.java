package com.vehicle.rental.zelezniak.user_domain.service.authentication;

import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.user_domain.model.client.Role;
import com.vehicle.rental.zelezniak.user_domain.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user_domain.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user_domain.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.user_domain.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user_domain.repository.RoleRepository;
import com.vehicle.rental.zelezniak.user_domain.service.ClientService;
import com.vehicle.rental.zelezniak.user_domain.service.ClientValidator;
import com.vehicle.rental.zelezniak.util.TimeFormatter;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service responsible for registering and logging users.
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private static final String ROLE_USER = "USER";

    private final ClientService clientService;
    private final ClientRepository repository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final ClientValidator clientValidator;
    private final InputValidator inputValidator;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        inputValidator.throwExceptionIfObjectIsNull(username, "Email can not be a null.");
        return clientService.findByEmail(username);
    }

    public Client register(Client client) {
        validateData(client);
        saveClient(client);
        return client;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        return tryLoginUser(email, password);
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

    private LoginResponse tryLoginUser(String email, String password) {
        String token = null;
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            token = jwtGenerator.generateJWT(auth);
        } catch (AuthenticationException e) {
            throwException();
        }
        return new LoginResponse(clientService.findByEmail(email), token);
    }

    private void throwException() {
        throw new IllegalArgumentException("Bad credentials");
    }
}
