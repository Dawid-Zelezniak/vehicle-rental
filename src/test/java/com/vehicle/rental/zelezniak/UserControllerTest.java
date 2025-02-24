package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.config.UserCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.UserLogin;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.Role;
import com.vehicle.rental.zelezniak.user.model.user.dto.UserDto;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.PhoneNumber;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class UserControllerTest {

    private static User userWithId2;
    private static String adminToken;
    private static String userToken;
    private static final Pageable PAGEABLE = PageRequest.of(0, 5);
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;
    private static final String ROLE_USER = Role.USER;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserCreator userCreator;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private UserLogin login;
    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        databaseSetup.setupAllTables();
        userWithId2 = userCreator.createUserWithId2();
        if (adminToken == null && userToken == null) {
            adminToken = generateToken("admin@gmail.com", "admin1234");
            userToken = generateToken("userthree@gmail.com", "somepass");
        }
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        var credentials = userWithId2.getCredentials();
        var name = userWithId2.getName();
        PhoneNumber phoneNumber = userWithId2.getPhoneNumber();

        mockMvc.perform(get("/users")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(NUMBER_OF_USERS)))
                .andExpect(jsonPath("$.content[1].id").value(userWithId2.getId()))
                .andExpect(jsonPath("$.content[1].email").value(credentials.getEmail()))
                .andExpect(jsonPath("$.content[1].name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.content[1].name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.content[1].address.street.streetName").value(userWithId2.getAddress().getStreet().streetName()))
                .andExpect(jsonPath("$.content[1].roles", hasSize(NUMBER_OF_USER_ROLES)))
                .andExpect(jsonPath("$.content[1].phoneNumber.number").value(phoneNumber.getNumber()))
                .andExpect(jsonPath("$.content[1].roles[0].roleName").value(ROLE_USER));
    }

    @Test
    void shouldNotReturnAllUsersForRoleUser() throws Exception {
        mockMvc.perform(get("/users")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindUserByIdForRoleAdmin() throws Exception {
        Long existingUserId = userWithId2.getId();
        var credentials = userWithId2.getCredentials();
        var name = userWithId2.getName();
        PhoneNumber phoneNumber = userWithId2.getPhoneNumber();

        mockMvc.perform(get("/users/{id}", existingUserId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userWithId2.getId()))
                .andExpect(jsonPath("$.email").value(credentials.getEmail()))
                .andExpect(jsonPath("$.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.address.street.streetName").value(userWithId2.getAddress().getStreet().streetName()))
                .andExpect(jsonPath("$.roles", hasSize(NUMBER_OF_USER_ROLES)))
                .andExpect(jsonPath("$.phoneNumber.number").value(phoneNumber.getNumber()))
                .andExpect(jsonPath("$.roles[0].roleName").value(ROLE_USER));
    }

    @Test
    void shouldDenyAccessToOtherUsersDataForRoleUser() throws Exception {
        Long existingUserId = userWithId2.getId();
        mockMvc.perform(get("/users/{id}", existingUserId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to search for other users."));
    }

    @Test
    void shouldAllowUserToFindHisOwnDataById() throws Exception {
        mockMvc.perform(get("/users/{id}", USER_3_ID)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_3_ID));
    }

    @Test
    void shouldNotFindUserByNotExistingId() throws Exception {
        Long notExistentId = 20L;
        int notFound = 404;
        mockMvc.perform(get("/users/{id}", notExistentId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "User with id: " + notExistentId + " does not exist."))
                .andExpect(jsonPath("$.code").value(notFound));
    }

    @Test
    @DisplayName("Run update method with token generated for role USER")
    void shouldTestUserCanUpdateHimself() throws Exception {
        String tokenForCurrentUser = login.loginUser("usertwo@gmail.com", "somepass");

        userWithId2.setName(new UserName("Uncle", "Bob"));
        userWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        performUpdateUser(userWithId2, tokenForCurrentUser);

        User updated = userService.findUserById(userWithId2.getId());

        assertEquals(userWithId2.getUsername(), updated.getUsername());
        assertEquals(userWithId2.getEmail(), updated.getEmail());
        assertTrue(encoder.matches(userWithId2.getPassword(), updated.getPassword()));
    }

    @Test
    @DisplayName("Run update method with token generated for role ADMIN")
    void shouldUpdateUserWhenRoleADMIN() throws Exception {
        userWithId2.setName(new UserName("Uncle", "Bob"));
        userWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        performUpdateUser(userWithId2, adminToken);

        User updated = userService.findUserById(userWithId2.getId());

        assertEquals(userWithId2.getUsername(), updated.getUsername());
        assertEquals(userWithId2.getEmail(), updated.getEmail());
        assertTrue(encoder.matches(userWithId2.getPassword(), updated.getPassword()));
    }

    @Test
    void shouldTestUserCanNotUpdateAnotherUser() throws Exception {
        Long otherUserId = userWithId2.getId();

        userWithId2.setName(new UserName("Uncle", "Bob"));
        userWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        mockMvc.perform(put("/users/{id}", otherUserId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWithId2))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to update another user data."));
    }

    @Test
    void shouldDeleteUserForRoleADMIN() throws Exception {
        Long existingUserId = userWithId2.getId();
        findAllUsersAndAssertSize(NUMBER_OF_USERS);

        mockMvc.perform(delete("/users/{id}", existingUserId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        findAllUsersAndAssertSize(NUMBER_OF_USERS - 1);
    }

    @Test
    void shouldNotDeleteUserForRoleUSER() throws Exception {
        Long existingUserId = userWithId2.getId();

        findAllUsersAndAssertSize(NUMBER_OF_USERS);

        mockMvc.perform(delete("/users/{id}", existingUserId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindUserByEmailWhenRoleADMIN() throws Exception {
        var credentials = userWithId2.getCredentials();
        var name = userWithId2.getName();
        PhoneNumber phoneNumber = userWithId2.getPhoneNumber();

        mockMvc.perform(get("/users/email/{email}", credentials.getEmail())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userWithId2.getId()))
                .andExpect(jsonPath("$.credentials.email").value(credentials.getEmail()))
                .andExpect(jsonPath("$.credentials.password").value(credentials.getPassword()))
                .andExpect(jsonPath("$.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.address.street.streetName").value(userWithId2.getAddress().getStreet().streetName()))
                .andExpect(jsonPath("$.roles", hasSize(NUMBER_OF_USER_ROLES)))
                .andExpect(jsonPath("$.phoneNumber.number").value(phoneNumber.getNumber()))
                .andExpect(jsonPath("$.roles[0].roleName").value(ROLE_USER));
    }

    @Test
    void shouldNotFindUserByEmailWhenDoesNotExist() throws Exception {
        String email = "nonexistingemail@gmail.com";
        mockMvc.perform(get("/users/email/{email}", email)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "User with email: " + email + " does not exist."));
    }

    @Test
    void shouldNotFindUserByEmailForRoleUSER() throws Exception {
        String email = "nonexistingemail@gmail.com";
        mockMvc.perform(get("/users/email/{email}", email)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private String generateToken(String email, String password) {
        return login.loginUser(email, password);
    }

    private void performUpdateUser(User newData, String token) throws Exception {
        Long existingUserId = userWithId2.getId();
        mockMvc.perform(put("/users/{id}", existingUserId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newData))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private void findAllUsersAndAssertSize(int expectedSize) {
        Page<UserDto> page = userService.findAll(PAGEABLE);
        List<UserDto> users = page.getContent();
        assertEquals(expectedSize, users.size());
    }
}
