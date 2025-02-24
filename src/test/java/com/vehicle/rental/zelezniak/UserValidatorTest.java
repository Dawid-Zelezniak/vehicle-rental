package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.UserCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.service.UserService;
import com.vehicle.rental.zelezniak.user.service.validation.UserValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static com.vehicle.rental.zelezniak.config.TestConstants.USER_3_ID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserValidatorTest {

    private static User userWithId2;

    @Autowired
    private UserValidator validator;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private UserCreator userCreator;
    @Autowired
    private UserService userService;

    @BeforeAll
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
    }

    @BeforeEach
    void initializeTestData() {
        userWithId2 = userCreator.createUserWithId2();
    }

    @Test
    void shouldThrowExceptionWhenUserExist() {
        String existingEmail = userWithId2.getEmail();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateUserDoesNotExists(existingEmail));
        assertEquals("User with email : " + existingEmail + " already exist", exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionWhenUserDoesNotExist() {
        User c = new User();
        c.setCredentials(new UserCredentials("someuser@gmail.com", "somepass"));

        assertDoesNotThrow(() -> validator.validateUserDoesNotExists(c.getEmail()));
    }

    @Test
    void shouldTestUserCanBeUpdatedWhenUniqueEmail() {
        String userFromDbEmail = userWithId2.getEmail();
        userWithId2.setCredentials(new UserCredentials("newemail@gmail.com", "somepass"));

        assertDoesNotThrow(() -> validator.validateUserCanBeUpdated(userFromDbEmail, userWithId2));
    }

    @Test
    void shouldTestUserCanNotBeUpdatedWhenEmailNotUnique() {
        User byId = userService.findUserById(USER_3_ID);
        String existingEmail = byId.getEmail();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validateUserCanBeUpdated(existingEmail, userWithId2));
        assertEquals("User with email : " + userWithId2.getEmail() + " already exist", exception.getMessage());
    }
}
