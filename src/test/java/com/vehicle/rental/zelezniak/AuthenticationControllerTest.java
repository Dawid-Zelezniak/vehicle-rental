package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Country;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.client.Address;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.client.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user.service.authentication.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = VehicleRentalApplication.class)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class AuthenticationControllerTest {

    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private AuthenticationService authService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DatabaseSetup databaseSetup;

    private Client client;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        client = new Client();
        client.setName(new UserName("Uncle", "Bob"));
        client.setCredentials(new UserCredentials("bob@gmail.com", "somepassword"));
        Address address = new Address(null, new Street("teststreet"), "5",
                "150", new City("Warsaw"), "00-001", new Country("Poland"));
        client.setAddress(address);
    }

    @Test
    void shouldRegisterUser() throws Exception {
        UserName name = client.getName();
        Address address = client.getAddress();
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.credentials.email").value(client.getEmail()))
                .andExpect(jsonPath("$.address.street.streetName").value(address.getStreet().streetName()))
                .andExpect(jsonPath("$.address.houseNumber").value(address.getHouseNumber()))
                .andExpect(jsonPath("$.address.flatNumber").value(address.getFlatNumber()))
                .andExpect(jsonPath("$.address.city.cityName").value(address.getCity().cityName()))
                .andExpect(jsonPath("$.address.postalCode").value(address.getPostalCode()))
                .andExpect(jsonPath("$.address.country.countryName").value(address.getCountry().countryName()));

        assertEquals(4, clientRepository.count());
    }

    @Test
    void shouldNotRegisterUserWhenNameIsInvalid() throws Exception {
        client.setName(new UserName("Ab", "L"));
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors", hasSize(2)))
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("First name must contains at least 3 characters",
                                "Last name must contains at least 2 characters"
                        )));
    }

    @Test
    void shouldNotRegisterUserWhenCredentialsInvalid() throws Exception {
        client.setCredentials(new UserCredentials(client.getEmail(), "a"));
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("Password must contains at least 5 characters."
                        )));
    }

    @Test
    void shouldNotRegisterUserWhenAddressInvalid() throws Exception {
        Address address = client.getAddress();
        address.setStreet(new Street(""));
        address.setCountry(new Country(""));

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("Street name can not be blank.",
                                "Country name can not be blank."
                        )));
    }

    @Test
    void shouldLoginUser() throws Exception {
        authService.register(client);
        String email = client.getEmail();
        LoginRequest loginRequest = new LoginRequest(email, "somepassword");

        Client registeredUser = clientRepository.findByCredentialsEmail(email).get();
        UserName name = registeredUser.getName();
        Address address = registeredUser.getAddress();

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.client.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.client.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.client.credentials.email").value(client.getEmail()))
                .andExpect(jsonPath("$.client.address.street.streetName").value(address.getStreet().streetName()))
                .andExpect(jsonPath("$.client.address.houseNumber").value(address.getHouseNumber()))
                .andExpect(jsonPath("$.client.address.flatNumber").value(address.getFlatNumber()))
                .andExpect(jsonPath("$.client.address.city.cityName").value(address.getCity().cityName()))
                .andExpect(jsonPath("$.client.address.postalCode").value(address.getPostalCode()))
                .andExpect(jsonPath("$.client.address.country.countryName").value(address.getCountry().countryName()))
                .andExpect(jsonPath("$.jwt").isNotEmpty());
    }

    @Test
    void shouldNotLoginUserWhenRequestIsInvalid() throws Exception {
        LoginRequest loginRequest = new LoginRequest(null, null);

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("Email can not be null.",
                                "Password can not be null.")));
    }

    @Test
    void shouldTestInvalidEmailPattern() throws Exception {
        client.setCredentials(new UserCredentials("wrongemail@com", "somepassword"));
        String email = client.getEmail();
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
                .andExpect(jsonPath("$.message").value(
                        "Email " + email + " has invalid pattern."));
    }
}
