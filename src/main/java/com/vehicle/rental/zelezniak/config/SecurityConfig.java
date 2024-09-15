package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.user_domain.service.authentication.RSAKeyProperties;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableAsync
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";

    private static final String[] ADMIN_ENDPOINTS = {
            "/clients",
            "/clients/{id}",
            "/clients/delete/**",
            "/clients/email/**",
            "/vehicles/add",
            "/vehicles/update/**",
            "/vehicles/delete/**",
            "/reservations"
    };

    private static final String[] USER_AND_ADMIN_ENDPOINTS = {
            "/clients/update/**",
            "/vehicles/criteria",
            "/vehicles/{id}",
            "/reservations/{id}",
            "/vehicles",
            "/reservations/{id}",
            "/reservations/client/**",
            "/reservations/create",
            "/reservations/update/**",
            "/reservations/delete/**",
            "/reservations/add/**",
            "/reservations/calculate/**"
    };

    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**"
    };

    private final RSAKeyProperties keyProperties;

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
