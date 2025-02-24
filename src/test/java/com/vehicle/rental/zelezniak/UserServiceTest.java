package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.UserCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.service.UserMapper;
import com.vehicle.rental.zelezniak.user.service.UserService;
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

import static com.vehicle.rental.zelezniak.config.TestConstants.USER_3_ID;
import static com.vehicle.rental.zelezniak.config.TestConstants.NUMBER_OF_USERS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class UserServiceTest {

    private static User userWithId2;
    private static UserDto user2Dto;
    private static final Pageable PAGEABLE = PageRequest.of(0, NUMBER_OF_USERS);

    @Autowired
    private User user;
    @Autowired
    private UserService userService;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private UserCreator userCreator;
    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        databaseSetup.setupAllTables();
        userWithId2 = userCreator.createUserWithId2();
        user2Dto = UserMapper.toDto(userWithId2);
    }

    @AfterEach
    void cleanupDatabase() {
        user = new User();
    }

    @Test
    void shouldReturnAllUsers() {
        List<UserDto> users = findAllUsersAndAssertSize(NUMBER_OF_USERS);

        assertTrue(users.contains(user2Dto));
        assertEquals(NUMBER_OF_USERS, users.size());

        for (UserDto user : users) {
            assertNotNull(user.getId());
            assertNotNull(user.getName());
            assertNotNull(user.getEmail());
            assertNotNull(user.getAddress());
        }
    }

    @Test
    void shouldFindUserDtoById() {
        UserDto userDto = userService.findById((userWithId2.getId()));

        assertEquals(user2Dto, userDto);
    }

    @Test
    void shouldFindUserById() {
        User user = userService.findUserById((userWithId2.getId()));

        assertEquals(userWithId2, user);
    }

    @Test
    void shouldNotFindUserByIdWhenDoesNotExist() {
        long nonExistentId = 20L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.findUserById((nonExistentId)));
        assertEquals("User with id: " + nonExistentId + " does not exist.", exception.getMessage());
    }

    @Test
    void shouldUpdateUserWhenEmailUnique() {
        Long user5Id = userWithId2.getId();
        userWithId2.setName(new UserName("Uncle", "Bob"));
        userWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        User updated = userService.update(user5Id, userWithId2);

        assertEquals(userWithId2.getEmail(), updated.getEmail());
        assertEquals(userWithId2.getUsername(), updated.getUsername());
        assertTrue(encoder.matches(userWithId2.getPassword(), updated.getPassword()));
    }

    @Test
    void shouldNotUpdateUserWhenEmailNotUnique() {
        UserDto existingUser = userService.findById(USER_3_ID);
        String existingEmail = existingUser.getEmail();
        var credentials = new UserCredentials(existingEmail, "somepassword");
        user.setCredentials(credentials);

        Long existingUserId = userWithId2.getId();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.update(existingUserId, user));
        assertEquals("User with email : " + existingEmail + " already exist", exception.getMessage());
    }

    @Test
    void shouldDeleteUserWhenDataCorrect() {
        findAllUsersAndAssertSize(NUMBER_OF_USERS);

        userService.delete(userWithId2.getId());

        List<UserDto> users = findAllUsersAndAssertSize(NUMBER_OF_USERS - 1);
        assertFalse(users.contains(user2Dto));
    }

    @Test
    void shouldFindUserByEmail() {
        String user5Email = userWithId2.getEmail();

        User byEmail = userService.findByEmail(user5Email);

        assertEquals(userWithId2, byEmail);
    }

    @Test
    void shouldNotFindUserByEmailWhenDoesNotExist() {
        String email = "nonexistingemail@gmail.com";

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.findByEmail(email));
        assertEquals("User with email: " + email + " does not exist.", exception.getMessage());
    }

    private List<UserDto> findAllUsersAndAssertSize(int expectedSize) {
        Page<UserDto> page = userService.findAll(PAGEABLE);
        List<UserDto> users = page.getContent();
        assertEquals(expectedSize, users.size());
        return users;
    }
}
