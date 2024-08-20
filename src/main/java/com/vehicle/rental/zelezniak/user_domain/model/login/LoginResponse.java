package com.vehicle.rental.zelezniak.user_domain.model.login;

import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private Client client;
    private String jwt;
}
