package com.vehicle.rental.zelezniak.user.model.login;

import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class LoginResponse {

    private UserDto user;
    private String jwt;
}
