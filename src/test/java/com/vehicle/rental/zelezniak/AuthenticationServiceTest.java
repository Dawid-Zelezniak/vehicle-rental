package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.address.City;
import com.vehicle.rental.zelezniak.common_value_objects.address.Country;
import com.vehicle.rental.zelezniak.common_value_objects.address.Street;
import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.client.Address;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.user.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import com.vehicle.rental.zelezniak.user.service.authentication.AuthenticationService;
import com.vehicle.rental.zelezniak.util.TimeFormatter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @AfterEach
    void cleanupDatabase() {
        databaseSetup.dropAllTables();
        client = new Client();
    }

    @Test
    void shouldRegisterNewUser() {
        setDataForClient();
        assertEquals(3, clientRepository.count());

        authenticationService.register(client);

        assertEquals(4, clientRepository.count());
        assertEquals(client, clientService.findById(client.getId()));

    }

    @Test
    void shouldLoginUser() {
        setDataForClient();
        authenticationService.register(client);

        LoginRequest loginRequest = new LoginRequest(client.getEmail(), "somepassword");
        LoginResponse login = authenticationService.login(loginRequest);

        Assertions.assertEquals(client, login.getClient());
        String token = login.getJwt();
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
    }

    private void setDataForClient() {
        client.setName(new UserName("Uncle", "Bob"));
        client.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));
        client.setCreatedAt(TimeFormatter.getFormattedActualDateTime());
        Address address = new Address(null, new Street("teststreet"),
                "5", "150", new City("Warsaw"),
                "00-001", new Country("Poland"));
        client.setAddress(address);
    }
}
