package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.user_domain.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user_domain.service.ClientService;
import com.vehicle.rental.zelezniak.user_domain.service.ClientValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ClientValidatorTest {

    private static Client clientWithId5;

    @Autowired
    private ClientValidator validator;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ClientCreator clientCreator;
    @Autowired
    private ClientService clientService;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        clientWithId5 = clientCreator.createClientWithId5();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldThrowExceptionIfClientExists() {
        String existingEmail = clientWithId5.getEmail();

        assertThrows(IllegalArgumentException.class,
                () -> validator.ifUserExistsThrowException(existingEmail));
    }

    @Test
    void shouldNotThrowExceptionIfClientNotExists() {
        Client c = new Client();
        c.setCredentials(new UserCredentials("someuser@gmail.com", "somepass"));

        assertDoesNotThrow(() -> validator.ifUserExistsThrowException(c.getEmail()));
    }

    @Test
    void shouldTestClientCanBeUpdated() {
        String userFromDbEmail = clientWithId5.getEmail();
        clientWithId5.setCredentials(new UserCredentials("newemail@gmail.com", "somepass"));

        assertDoesNotThrow(() -> validator.checkIfUserCanBeUpdated(userFromDbEmail, clientWithId5));
    }

    @Test
    void shouldTestClientCanNotBeUpdated() {
        Client byId = clientService.findById(6L);
        String existingEmail = byId.getEmail();

        assertThrows(IllegalArgumentException.class,
                () -> validator.checkIfUserCanBeUpdated(existingEmail, clientWithId5));
    }
}
