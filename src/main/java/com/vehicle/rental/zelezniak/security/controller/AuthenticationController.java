package com.vehicle.rental.zelezniak.security.controller;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.security.authentication.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public Client register(@RequestBody @Valid Client client){
    return authService.register(client);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest){
       return authService.login(loginRequest);
    }
}
