package com.vehicle.rental.zelezniak.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.vehicle.rental.zelezniak.security.authentication.token.RSAKeyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;

import static com.vehicle.rental.zelezniak.user.model.client.Role.ADMIN;
import static com.vehicle.rental.zelezniak.user.model.client.Role.USER;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableAsync
@EnableMethodSecurity
public class SecurityConfig {

    private final RSAKeyProperties keyProperties;

    private static final String[] ADMIN_ENDPOINTS = {
            "/clients",
            "/clients/email/**",
            "/reservations/vehicles/from_reservation/**"
    };

    private static final String[] USER_AND_ADMIN_ENDPOINTS = {
            "/clients/{id}",
            "/vehicles/criteria",
            "/vehicles/available/in_period",
            "/vehicles/{id}",
            "/vehicles",
            "/reservations/{id}",
            "/reservations/{id}",
            "/reservations/client/**",
            "/reservations/add/**",
            "/reservations/calculate/**"
    };

    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(@Lazy UserDetailsService userDetailsService) {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(config -> config
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole(ADMIN)
                        .requestMatchers(USER_AND_ADMIN_ENDPOINTS).hasAnyRole(USER, ADMIN)
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        RSAPublicKey key = keyProperties.getPublicKey();
        return NimbusJwtDecoder.withPublicKey(key)
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey
                .Builder(keyProperties.getPublicKey())
                .privateKey(keyProperties.getPrivateKey())
                .build();
        JWKSet set = new JWKSet(jwk);
        ImmutableJWKSet<SecurityContext> jwkSet = new ImmutableJWKSet<>(set);
        return new NimbusJwtEncoder(jwkSet);
    }

    /**
     * Configures JwtAuthenticationConverter to convert JWT to Spring Security authentication.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        var jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtAuthConverter;
    }
}
