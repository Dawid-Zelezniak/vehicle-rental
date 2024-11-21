package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.service.ClientMapper;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.config.TestConstants.CLIENT_3_ID;
import static com.vehicle.rental.zelezniak.config.TestConstants.EXPECTED_NUMBER_OF_CLIENTS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ClientServiceTest {

    private static Client clientWithId2;
    private static ClientDto client2Dto;
    private static final Pageable PAGEABLE = PageRequest.of(0, EXPECTED_NUMBER_OF_CLIENTS);

    @Autowired
    private Client client;
    @Autowired
    private ClientService clientService;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ClientCreator clientCreator;
    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        databaseSetup.setupAllTables();
        clientWithId2 = clientCreator.createClientWithId2();
        client2Dto = ClientMapper.toDto(clientWithId2);
    }

    @AfterEach
    void cleanupDatabase() {
        client = new Client();
    }

    @Test
    void shouldReturnAllClients() {
        List<ClientDto> clients = findAllClientsAndAssertSize(EXPECTED_NUMBER_OF_CLIENTS);

        assertTrue(clients.contains(client2Dto));
        assertEquals(EXPECTED_NUMBER_OF_CLIENTS, clients.size());

        for (ClientDto client : clients) {
            assertNotNull(client.getId());
            assertNotNull(client.getName());
            assertNotNull(client.getEmail());
            assertNotNull(client.getAddress());
        }
    }

    @Test
    void shouldFindClientDtoById() {
        ClientDto clientDto = clientService.findById((clientWithId2.getId()));

        assertEquals(client2Dto, clientDto);
    }

    @Test
    void shouldFindClientById() {
        Client client = clientService.findClientById((clientWithId2.getId()));

        assertEquals(clientWithId2, client);
    }

    @Test
    void shouldNotFindClientByIdWhenDoesNotExist() {
        long nonExistentId = 20L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> clientService.findClientById((nonExistentId)));
        assertEquals("Client with id: " + nonExistentId + " does not exist.", exception.getMessage());
    }

    @Test
    void shouldUpdateClientWhenEmailUnique() {
        Long client5Id = clientWithId2.getId();
        clientWithId2.setName(new UserName("Uncle", "Bob"));
        clientWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        Client updated = clientService.update(client5Id, clientWithId2);

        assertEquals(clientWithId2.getEmail(), updated.getEmail());
        assertEquals(clientWithId2.getUsername(), updated.getUsername());
        assertTrue(encoder.matches(clientWithId2.getPassword(), updated.getPassword()));
    }

    @Test
    void shouldNotUpdateClientWhenEmailNotUnique() {
        ClientDto existingClient = clientService.findById(CLIENT_3_ID);
        String existingEmail = existingClient.getEmail();
        var credentials = new UserCredentials(existingEmail, "somepassword");
        client.setCredentials(credentials);

        Long existingClientId = clientWithId2.getId();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> clientService.update(existingClientId, client));
        assertEquals("Client with email : " + existingEmail + " already exist", exception.getMessage());
    }

    @Test
    void shouldDeleteClientWhenDataCorrect() {
        findAllClientsAndAssertSize(EXPECTED_NUMBER_OF_CLIENTS);

        clientService.delete(clientWithId2.getId());

        List<ClientDto> clients = findAllClientsAndAssertSize(EXPECTED_NUMBER_OF_CLIENTS - 1);
        assertFalse(clients.contains(client2Dto));
    }

    @Test
    void shouldFindClientByEmail() {
        String client5Email = clientWithId2.getEmail();

        Client byEmail = clientService.findByEmail(client5Email);

        assertEquals(clientWithId2, byEmail);
    }

    @Test
    void shouldNotFindClientByEmailWhenDoesNotExist() {
        String email = "nonexistingemail@gmail.com";

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> clientService.findByEmail(email));
        assertEquals("Client with email: " + email + " does not exist.", exception.getMessage());
    }

    private List<ClientDto> findAllClientsAndAssertSize(int expectedSize) {
        Page<ClientDto> page = clientService.findAll(PAGEABLE);
        List<ClientDto> clients = page.getContent();
        assertEquals(expectedSize, clients.size());
        return clients;
    }
}
