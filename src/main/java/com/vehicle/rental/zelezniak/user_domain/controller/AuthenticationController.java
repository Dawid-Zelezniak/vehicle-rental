package com.vehicle.rental.zelezniak.user_domain.controller;

import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.user_domain.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user_domain.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.user_domain.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 Controller class responsible for user registration and login.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Client register(@RequestBody @Validated Client client){
    return authService.register(client);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LoginResponse login(@RequestBody @Validated LoginRequest loginRequest){
       return authService.login(loginRequest);
    }
}
