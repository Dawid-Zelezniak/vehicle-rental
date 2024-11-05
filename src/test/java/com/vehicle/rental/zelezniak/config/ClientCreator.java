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
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ClientCreator {

    public Client createClientWithId5() {
        Client client = new Client();
        client.setId(5L);
        client.setName(new UserName("UserFive", "Five"));
        client.setCredentials(new UserCredentials("userfive@gmail.com", "somepass"));
        Address address = buildAddress();
        client.setAddress(address);
        client.setRoles(Set.of(buildRoleUser()));
        client.setPhoneNumber(new PhoneNumber("+48 111222333"));
        return client;
    }

    private Role buildRoleUser() {
        Role role = new Role();
        role.setId(1);
        role.setRoleName("USER");
        return role;
    }

    private Address buildAddress(){
        return Address.builder()
                .id(5L)
                .street(new Street("teststreet"))
                .houseNumber("5")
                .flatNumber("150")
                .city(new City("Warsaw"))
                .postalCode("00-001")
                .country(new Country("Poland"))
                .build();
    }
}
