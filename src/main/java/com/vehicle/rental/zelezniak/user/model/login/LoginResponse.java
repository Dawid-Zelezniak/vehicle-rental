package com.vehicle.rental.zelezniak.user.model.login;

import com.vehicle.rental.zelezniak.user.model.client.Client;
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
