package com.vehicle.rental.zelezniak.user_domain.service.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;
/**
 * Class responsible for generating JSON Web Tokens
 */
@Service
@RequiredArgsConstructor
public class JWTGenerator {

    private final JwtEncoder jwtEncoder;

    public String generateJWT(Authentication authentication) {
        JwtClaimsSet jwtClaimsSet = handleGetClaims(authentication);
        return getToken(jwtClaimsSet);
    }

    private JwtClaimsSet handleGetClaims(Authentication authentication) {
        String authorities = getAuthoritiesAsString(authentication);
        return buildClaimsSet(authorities, authentication);
    }

    private String getAuthoritiesAsString(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }

    private JwtClaimsSet buildClaimsSet(String authorities, Authentication authentication) {
        return JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .subject(authentication.getName())
                .claim("roles", authorities)
                .build();
    }

    private String getToken(JwtClaimsSet jwtClaimsSet) {
        return jwtEncoder.encode(
                        JwtEncoderParameters.from(jwtClaimsSet))
                .getTokenValue();
    }

}
