package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.UserCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.security.authentication.AuthenticationService;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.model.login.LoginResponse;
import com.vehicle.rental.zelezniak.user.repository.UserRepository;
import com.vehicle.rental.zelezniak.user.service.UserMapper;
import com.vehicle.rental.zelezniak.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static com.vehicle.rental.zelezniak.config.TestConstants.NUMBER_OF_USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class AuthenticationServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private User user;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private UserCreator userCreator;

    @BeforeEach
    void createUsers() throws IOException {
        databaseSetup.setupAllTables();
        user = UserCreator.createTestUser();
    }

    @AfterEach
    void cleanupDatabase() {
        user = new User();
    }

    @Test
    void shouldRegisterUserWhenRequestContainsRequiredData() {
        assertEquals(NUMBER_OF_USERS, userRepository.count());

        authenticationService.register(user);

        assertEquals(NUMBER_OF_USERS + 1, userRepository.count());
        assertEquals(user, userService.findUserById(user.getId()));
    }

    @Test
    void shouldLoginUserWithCorrectCredentials() {
        authenticationService.register(user);

        LoginRequest loginRequest = new LoginRequest(user.getEmail(), "somepassword");
        LoginResponse login = authenticationService.login(loginRequest);

        UserDto dto = UserMapper.toDto(user);
        assertEquals(dto, login.getUser());
        assertNotNull(login.getJwt());
    }
}
