package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.service.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientMapperTest {

    private static Client clientWithId2;

    @BeforeEach
    void setupDatabase() {
        ClientCreator clientCreator = new ClientCreator();
        clientWithId2 = clientCreator.createClientWithId2();
    }

    @Test
    void shouldMapClientToDto() {
        ClientDto client5Dto = ClientMapper.toDto(clientWithId2);

        assertEquals(client5Dto.getId(), clientWithId2.getId());
        assertEquals(client5Dto.getName(), clientWithId2.getName());
        assertEquals(client5Dto.getEmail(), clientWithId2.getEmail());
        assertEquals(client5Dto.getCreatedAt(), clientWithId2.getCreatedAt());
        assertEquals(client5Dto.getPhoneNumber(), clientWithId2.getPhoneNumber());
        assertEquals(client5Dto.getAddress(), clientWithId2.getAddress());
        assertEquals(client5Dto.getRoles(), clientWithId2.getRoles());
    }
}
