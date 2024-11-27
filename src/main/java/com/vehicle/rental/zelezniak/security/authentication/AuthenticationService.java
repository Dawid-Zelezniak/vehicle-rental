package com.vehicle.rental.zelezniak.security.authentication;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.model.login.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service responsible for registering and logging users.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RegistrationService registrationService;
    private final LoginService loginService;

    @Transactional
    public ClientDto register(Client client) {
      return registrationService.registerUser(client);
    }

    public LoginResponse login(LoginRequest request) {
        return loginService.loginUser(request);
    }
}
