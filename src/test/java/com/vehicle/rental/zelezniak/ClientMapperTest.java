package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.service.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientMapperTest {

    private static Client clientWithId5;

    private ClientCreator clientCreator;

    @BeforeEach
    void setupDatabase() {
        clientCreator = new ClientCreator();
        clientWithId5 = clientCreator.createClientWithId5();
    }

    @Test
    void shouldMapClientToDto() {
        ClientDto client5Dto = ClientMapper.toDto(clientWithId5);

        assertEquals(client5Dto.getId(), clientWithId5.getId());
        assertEquals(client5Dto.getName(), clientWithId5.getName());
        assertEquals(client5Dto.getEmail(), clientWithId5.getEmail());
        assertEquals(client5Dto.getCreatedAt(), clientWithId5.getCreatedAt());
        assertEquals(client5Dto.getPhoneNumber(), clientWithId5.getPhoneNumber());
        assertEquals(client5Dto.getAddress(), clientWithId5.getAddress());
        assertEquals(client5Dto.getRoles(), clientWithId5.getRoles());
    }
}
