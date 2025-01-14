package com.vehicle.rental.zelezniak.security.authentication;

import com.vehicle.rental.zelezniak.security.authentication.token.JWTGenerator;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.user.service.ClientMapper;
import com.vehicle.rental.zelezniak.user.service.ClientService;
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
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class LoginService implements UserDetailsService {

    private final InputValidator inputValidator;
    private final ClientService clientService;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        inputValidator.throwExceptionIfObjectIsNull(username, "Email can not be null.");
        return clientService.findByEmail(username);
    }

    LoginResponse loginUser(LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();
        return tryLoginUser(email, password);
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
        log.info("User logged in");
        ClientDto dto = ClientMapper.toDto(clientService.findByEmail(email));
        return new LoginResponse(dto, token);
    }

    private void throwException() {
        throw new IllegalArgumentException("Bad credentials");
    }
}
