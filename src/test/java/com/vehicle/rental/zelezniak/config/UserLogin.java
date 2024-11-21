package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.security.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLogin {

    private final AuthenticationService auth;

    public String loginUser(String email, String password) {
        LoginResponse login = auth.login(new LoginRequest(email, password));
        return login.getJwt();
    }

}
