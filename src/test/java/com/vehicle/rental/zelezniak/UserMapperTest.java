package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.UserCreator;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;
import com.vehicle.rental.zelezniak.user.service.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private static User userWithId2;

    @BeforeEach
    void setupDatabase() {
        UserCreator userCreator = new UserCreator();
        userWithId2 = userCreator.createUserWithId2();
    }

    @Test
    void shouldMapUserToDto() {
        UserDto user5Dto = UserMapper.toDto(userWithId2);

        assertEquals(user5Dto.getId(), userWithId2.getId());
        assertEquals(user5Dto.getName(), userWithId2.getName());
        assertEquals(user5Dto.getEmail(), userWithId2.getEmail());
        assertEquals(user5Dto.getCreatedAt(), userWithId2.getCreatedAt());
        assertEquals(user5Dto.getPhoneNumber(), userWithId2.getPhoneNumber());
        assertEquals(user5Dto.getAddress(), userWithId2.getAddress());
        assertEquals(user5Dto.getRoles(), userWithId2.getRoles());
    }
}
