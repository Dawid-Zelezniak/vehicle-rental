package com.vehicle.rental.zelezniak.user.service;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;

public class ClientMapper {

    public static ClientDto toDto(Client client) {
        ClientDto c = new ClientDto();
        c.setId(client.getId());
        c.setEmail(client.getEmail());
        c.setName(client.getName());
        c.setPhoneNumber(client.getPhoneNumber());
        c.setCreatedAt(client.getCreatedAt());
        c.setAddress(client.getAddress());
        c.setRoles(client.getRoles());
        return c;
    }
}
