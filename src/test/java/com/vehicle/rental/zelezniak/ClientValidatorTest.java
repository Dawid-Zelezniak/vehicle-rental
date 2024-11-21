package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import com.vehicle.rental.zelezniak.user.service.validation.ClientValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static com.vehicle.rental.zelezniak.config.TestConstants.CLIENT_3_ID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientValidatorTest {

    private static Client clientWithId2;

    @Autowired
    private ClientValidator validator;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ClientCreator clientCreator;
    @Autowired
    private ClientService clientService;

    @BeforeAll
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
    }

    @BeforeEach
    void initializeTestData() {
        clientWithId2 = clientCreator.createClientWithId2();
    }

    @Test
    void shouldThrowExceptionWhenClientExist() {
        String existingEmail = clientWithId2.getEmail();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateUserDoesNotExists(existingEmail));
        assertEquals("Client with email : " + existingEmail + " already exist", exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionWhenClientDoesNotExist() {
        Client c = new Client();
        c.setCredentials(new UserCredentials("someuser@gmail.com", "somepass"));

        assertDoesNotThrow(() -> validator.validateUserDoesNotExists(c.getEmail()));
    }

    @Test
    void shouldTestClientCanBeUpdatedWhenUniqueEmail() {
        String userFromDbEmail = clientWithId2.getEmail();
        clientWithId2.setCredentials(new UserCredentials("newemail@gmail.com", "somepass"));

        assertDoesNotThrow(() -> validator.validateUserCanBeUpdated(userFromDbEmail, clientWithId2));
    }

    @Test
    void shouldTestClientCanNotBeUpdatedWhenEmailNotUnique() {
        Client byId = clientService.findClientById(CLIENT_3_ID);
        String existingEmail = byId.getEmail();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateUserCanBeUpdated(existingEmail, clientWithId2));
        assertEquals("Client with email : " + clientWithId2.getEmail() + " already exist", exception.getMessage());
    }
}
