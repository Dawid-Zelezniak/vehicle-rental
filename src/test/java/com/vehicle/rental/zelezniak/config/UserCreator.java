package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Country;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import com.vehicle.rental.zelezniak.user.model.user.Address;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.Role;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.PhoneNumber;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.util.TimeFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

import static com.vehicle.rental.zelezniak.user.model.user.Role.USER;

@Component
public class UserCreator {

    public static User createTestUser() {
        User user = new User();
        user.setName(new UserName("Uncle", "Bob"));
        user.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));
        user.setCreatedAt(TimeFormatter.getFormattedActualDateTime());
        Address address = new Address(null, new Country("Poland"), new City("Warsaw"),
                new Street("teststreet"), "5", "150", "00-001");
        user.setAddress(address);
        return user;
    }

    public User createUserWithId2() {
        User user = new User();
        user.setId(2L);
        user.setName(new UserName("UserTwo", "Two"));
        user.setCredentials(new UserCredentials("usertwo@gmail.com", "$2a$10$53viTAvUEN.0LdWJ9Hwbq.uyqFWiyhSVkMa//Blhi9Zk12SqePz5a"));
        Address address = buildAddress();
        user.setCreatedAt(LocalDateTime.of(2024, 1, 2, 13, 0, 0));
        user.setAddress(address);
        user.setRoles(Set.of(createRoleUser()));
        user.setPhoneNumber(new PhoneNumber("+48 111222333"));
        return user;
    }

    private Role createRoleUser() {
        Role role = new Role();
        role.setId(1);
        role.setRoleName(USER);
        return role;
    }

    private Address buildAddress() {
        return Address.builder()
                .id(2L)
                .street(new Street("teststreet"))
                .houseNumber("5")
                .flatNumber("150")
                .city(new City("Warsaw"))
                .postalCode("00-001")
                .country(new Country("Poland"))
                .build();
    }
}
