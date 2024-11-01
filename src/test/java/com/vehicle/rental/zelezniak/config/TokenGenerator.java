package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.user.service.authentication.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TokenGenerator {

    @Autowired
    private JWTGenerator jwtGenerator;

    public String generateToken(String role) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UserDetails userDetails = new User(role.toLowerCase(), "password", authorities);
        return jwtGenerator.generateJWT(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
    }
}
