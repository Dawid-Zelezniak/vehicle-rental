package com.vehicle.rental.zelezniak.user_domain.service.authentication;

import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.user_domain.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user_domain.model.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service responsible for registering and logging users.
 */

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final RegistrationService registrationService;
    private final LoginService loginService;

    public Client register(Client client) {
      return registrationService.registerUser(client);
    }

    public LoginResponse login(LoginRequest request) {
        return loginService.loginUser(request);
    }
}
