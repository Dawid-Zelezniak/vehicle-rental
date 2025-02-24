package com.vehicle.rental.zelezniak.user.service;

import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;

public class UserMapper {

    public static UserDto toDto(User user) {
        UserDto c = new UserDto();
        c.setId(user.getId());
        c.setEmail(user.getEmail());
        c.setName(user.getName());
        c.setPhoneNumber(user.getPhoneNumber());
        c.setCreatedAt(user.getCreatedAt());
        c.setAddress(user.getAddress());
        c.setRoles(user.getRoles());
        return c;
    }
}
