package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Country;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import com.vehicle.rental.zelezniak.user.model.client.Address;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.Role;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.PhoneNumber;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.util.TimeFormatter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

import static com.vehicle.rental.zelezniak.user.model.client.Role.USER;

@Component
public class ClientCreator {

    public static Client createTestClient() {
        Client client = new Client();
        client.setName(new UserName("Uncle", "Bob"));
        client.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));
        client.setCreatedAt(TimeFormatter.getFormattedActualDateTime());
        Address address = new Address(null, new Country("Poland"), new City("Warsaw"),
                new Street("teststreet"), "5", "150", "00-001");
        client.setAddress(address);
        return client;
    }

    public Client createClientWithId2() {
        Client client = new Client();
        client.setId(2L);
        client.setName(new UserName("UserTwo", "Two"));
        client.setCredentials(new UserCredentials("usertwo@gmail.com", "$2a$10$53viTAvUEN.0LdWJ9Hwbq.uyqFWiyhSVkMa//Blhi9Zk12SqePz5a"));
        Address address = buildAddress();
        client.setCreatedAt(LocalDateTime.of(2024, 1, 2, 13, 0, 0));
        client.setAddress(address);
        client.setRoles(Set.of(createRoleUser()));
        client.setPhoneNumber(new PhoneNumber("+48 111222333"));
        return client;
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
