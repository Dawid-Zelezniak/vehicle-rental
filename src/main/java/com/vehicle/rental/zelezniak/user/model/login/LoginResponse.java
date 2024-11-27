package com.vehicle.rental.zelezniak.user.model.login;

import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoginResponse {

    private ClientDto client;
    private String jwt;
}
