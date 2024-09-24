package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ClientServiceTest {

    private static Client clientWithId5;
    private static final Pageable PAGEABLE = PageRequest.of(0, 5);

    @Autowired
    private Client client;
    @Autowired
    private ClientService clientService;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ClientCreator clientCreator;
    @Autowired
    private ReservationService reservationService;

    @BeforeEach
    void createUsers() throws IOException {
        databaseSetup.setupAllTables();
        clientWithId5 = clientCreator.createClientWithId5();
    }

    @AfterEach
    void cleanupDatabase() {
        databaseSetup.dropAllTables();
        client = new Client();
    }

    @Test
    void shouldReturnAllClients() {
        Page<Client> page = clientService.findAll(PAGEABLE);
        List<Client> clients = page.getContent();

        assertTrue(clients.contains(clientWithId5));
        assertEquals(3, clients.size());

        for (Client client : clients) {
            assertNotNull(client.getId());
            assertNotNull(client.getUsername());
            assertNotNull(client.getCredentials());
            assertNotNull(client.getAddress());
        }
    }

    @Test
    void shouldFindClientById() {
        Client client = clientService.findById(clientWithId5.getId());

        assertEquals(clientWithId5, client);
    }

    @Test
    void shouldNotFindClientById() {
        Long nonExistentId = 20L;

        assertThrows(NoSuchElementException.class,
                () -> clientService.findById(nonExistentId));
    }

    @Test
    void shouldUpdateClient() {
        Long client5Id = clientWithId5.getId();
        client.setId(client5Id);
        client.setName(new UserName("Uncle", "Bob"));
        client.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        Client updated = clientService.update(client5Id, client);

        assertEquals(client, updated);
    }

    @Test
    void shouldNotUpdateClient() {
        Client byId = clientService.findById(6L);
        String existingEmail = byId.getEmail();
        var credentials = new UserCredentials(existingEmail, "somepassword");
        client.setCredentials(credentials);

        Long client5Id = clientWithId5.getId();
        assertThrows(IllegalArgumentException.class,
                () -> clientService.update(client5Id, client));
    }

    @Test
    void shouldDeleteClient() {
        Page<Client> page = clientService.findAll(PAGEABLE);
        List<Client> clients = page.getContent();

        assertEquals(3, clients.size());

        clientService.delete(clientWithId5.getId());

        page = clientService.findAll(PAGEABLE);
        clients = page.getContent();

        assertEquals(2, clients.size());
        assertFalse(clients.contains(clientWithId5));
    }

    @Test
    void shouldFindClientByEmail() {
        String client5Email = clientWithId5.getEmail();

        Client byEmail = clientService.findByEmail(client5Email);

        assertEquals(clientWithId5, byEmail);
    }

    @Test
    void shouldNotFindClientByEmail() {
        String email = "nonexistingemail@gmail.com";

        assertThrows(NoSuchElementException.class,
                () -> clientService.findByEmail(email));
    }
}
