package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.config.ClientCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.UserLogin;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.Role;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.PhoneNumber;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.service.ClientService;
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
class ClientControllerTest {

    private static Client clientWithId2;
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
    private ClientService clientService;
    @Autowired
    private ClientCreator clientCreator;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private UserLogin login;
    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        databaseSetup.setupAllTables();
        clientWithId2 = clientCreator.createClientWithId2();
        if (adminToken == null && userToken == null) {
            adminToken = generateToken("admin@gmail.com", "admin1234");
            userToken = generateToken("userthree@gmail.com", "somepass");
        }
    }

    @Test
    void shouldReturnAllClients() throws Exception {
        var credentials = clientWithId2.getCredentials();
        var name = clientWithId2.getName();
        PhoneNumber phoneNumber = clientWithId2.getPhoneNumber();

        mockMvc.perform(get("/clients")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(NUMBER_OF_CLIENTS)))
                .andExpect(jsonPath("$.content[1].id").value(clientWithId2.getId()))
                .andExpect(jsonPath("$.content[1].email").value(credentials.getEmail()))
                .andExpect(jsonPath("$.content[1].name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.content[1].name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.content[1].address.street.streetName").value(clientWithId2.getAddress().getStreet().streetName()))
                .andExpect(jsonPath("$.content[1].roles", hasSize(NUMBER_OF_CLIENT_ROLES)))
                .andExpect(jsonPath("$.content[1].phoneNumber.number").value(phoneNumber.getNumber()))
                .andExpect(jsonPath("$.content[1].roles[0].roleName").value(ROLE_USER));
    }

    @Test
    void shouldNotReturnAllClientsForRoleUser() throws Exception {
        mockMvc.perform(get("/clients")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindClientByIdForRoleAdmin() throws Exception {
        Long existingClientId = clientWithId2.getId();
        var credentials = clientWithId2.getCredentials();
        var name = clientWithId2.getName();
        PhoneNumber phoneNumber = clientWithId2.getPhoneNumber();

        mockMvc.perform(get("/clients/{id}", existingClientId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(clientWithId2.getId()))
                .andExpect(jsonPath("$.email").value(credentials.getEmail()))
                .andExpect(jsonPath("$.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.address.street.streetName").value(clientWithId2.getAddress().getStreet().streetName()))
                .andExpect(jsonPath("$.roles", hasSize(NUMBER_OF_CLIENT_ROLES)))
                .andExpect(jsonPath("$.phoneNumber.number").value(phoneNumber.getNumber()))
                .andExpect(jsonPath("$.roles[0].roleName").value(ROLE_USER));
    }

    @Test
    void shouldDenyAccessToOtherClientsDataForRoleUser() throws Exception {
        Long existingClientId = clientWithId2.getId();
        mockMvc.perform(get("/clients/{id}", existingClientId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to search for other users."));
    }

    @Test
    void shouldAllowUserToFindHisOwnDataById() throws Exception {
        mockMvc.perform(get("/clients/{id}", CLIENT_3_ID)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CLIENT_3_ID));
    }

    @Test
    void shouldNotFindClientByNotExistingId() throws Exception {
        Long notExistentId = 20L;
        int notFound = 404;
        mockMvc.perform(get("/clients/{id}", notExistentId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Client with id: " + notExistentId + " does not exist."))
                .andExpect(jsonPath("$.code").value(notFound));
    }

    @Test
    @DisplayName("Run update method with token generated for role USER")
    void shouldTestUserCanUpdateHimself() throws Exception {
        String tokenForCurrentUser = login.loginUser("usertwo@gmail.com", "somepass");

        clientWithId2.setName(new UserName("Uncle", "Bob"));
        clientWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        performUpdateClient(clientWithId2, tokenForCurrentUser);

        Client updated = clientService.findClientById(clientWithId2.getId());

        assertEquals(clientWithId2.getUsername(), updated.getUsername());
        assertEquals(clientWithId2.getEmail(), updated.getEmail());
        assertTrue(encoder.matches(clientWithId2.getPassword(), updated.getPassword()));
    }

    @Test
    @DisplayName("Run update method with token generated for role ADMIN")
    void shouldUpdateClientWhenRoleADMIN() throws Exception {
        clientWithId2.setName(new UserName("Uncle", "Bob"));
        clientWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        performUpdateClient(clientWithId2, adminToken);

        Client updated = clientService.findClientById(clientWithId2.getId());

        assertEquals(clientWithId2.getUsername(), updated.getUsername());
        assertEquals(clientWithId2.getEmail(), updated.getEmail());
        assertTrue(encoder.matches(clientWithId2.getPassword(), updated.getPassword()));
    }

    @Test
    void shouldTestClientCanNotUpdateAnotherClient() throws Exception {
        Long otherClientId = clientWithId2.getId();

        clientWithId2.setName(new UserName("Uncle", "Bob"));
        clientWithId2.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));

        mockMvc.perform(put("/clients/{id}", otherClientId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientWithId2))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not authorized to update another client data."));
    }

    @Test
    void shouldDeleteClientForRoleADMIN() throws Exception {
        Long existingClientId = clientWithId2.getId();
        findAllClientsAndAssertSize(NUMBER_OF_CLIENTS);

        mockMvc.perform(delete("/clients/{id}", existingClientId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        findAllClientsAndAssertSize(NUMBER_OF_CLIENTS - 1);
    }

    @Test
    void shouldNotDeleteClientForRoleUSER() throws Exception {
        Long existingClientId = clientWithId2.getId();

        findAllClientsAndAssertSize(NUMBER_OF_CLIENTS);

        mockMvc.perform(delete("/clients/{id}", existingClientId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindClientByEmailWhenRoleADMIN() throws Exception {
        var credentials = clientWithId2.getCredentials();
        var name = clientWithId2.getName();
        PhoneNumber phoneNumber = clientWithId2.getPhoneNumber();

        mockMvc.perform(get("/clients/email/{email}", credentials.getEmail())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(clientWithId2.getId()))
                .andExpect(jsonPath("$.credentials.email").value(credentials.getEmail()))
                .andExpect(jsonPath("$.credentials.password").value(credentials.getPassword()))
                .andExpect(jsonPath("$.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.address.street.streetName").value(clientWithId2.getAddress().getStreet().streetName()))
                .andExpect(jsonPath("$.roles", hasSize(NUMBER_OF_CLIENT_ROLES)))
                .andExpect(jsonPath("$.phoneNumber.number").value(phoneNumber.getNumber()))
                .andExpect(jsonPath("$.roles[0].roleName").value(ROLE_USER));
    }

    @Test
    void shouldNotFindClientByEmailWhenDoesNotExist() throws Exception {
        String email = "nonexistingemail@gmail.com";
        mockMvc.perform(get("/clients/email/{email}", email)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Client with email: " + email + " does not exist."));
    }

    @Test
    void shouldNotFindClientByEmailForRoleUSER() throws Exception {
        String email = "nonexistingemail@gmail.com";
        mockMvc.perform(get("/clients/email/{email}", email)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private String generateToken(String email, String password) {
        return login.loginUser(email, password);
    }

    private void performUpdateClient(Client newData, String token) throws Exception {
        Long existingClientId = clientWithId2.getId();
        mockMvc.perform(put("/clients/{id}", existingClientId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newData))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private void findAllClientsAndAssertSize(int expectedSize) {
        Page<ClientDto> page = clientService.findAll(PAGEABLE);
        List<ClientDto> clients = page.getContent();
        assertEquals(expectedSize, clients.size());
    }
}
