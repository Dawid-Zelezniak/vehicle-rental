package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.security.authentication.AuthenticationService;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.user.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static com.vehicle.rental.zelezniak.config.TestConstants.EXPECTED_NUMBER_OF_CLIENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class AuthenticationServiceTest {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private Client client;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ClientCreator clientCreator;

    @BeforeEach
    void createUsers() throws IOException {
        databaseSetup.setupAllTables();
        client = ClientCreator.createTestClient();
    }

    @AfterEach
    void cleanupDatabase() {
        client = new Client();
    }

    @Test
    void shouldRegisterUserWhenRequestContainsRequiredData() {
        assertEquals(EXPECTED_NUMBER_OF_CLIENTS, clientRepository.count());

        authenticationService.register(client);

        assertEquals(EXPECTED_NUMBER_OF_CLIENTS + 1, clientRepository.count());
        assertEquals(client, clientService.findClientById(client.getId()));
    }

    @Test
    void shouldLoginUserWithCorrectCredentials() {
        authenticationService.register(client);

        LoginRequest loginRequest = new LoginRequest(client.getEmail(), "somepassword");
        LoginResponse login = authenticationService.login(loginRequest);

        assertEquals(client, login.getClient());
        assertNotNull(login.getJwt());
    }
}
