package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.location.Country;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import com.vehicle.rental.zelezniak.config.UserCreator;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.security.authentication.AuthenticationService;
import com.vehicle.rental.zelezniak.user.model.user.Address;
import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserCredentials;
import com.vehicle.rental.zelezniak.user.model.user.user_value_objects.UserName;
import com.vehicle.rental.zelezniak.user.model.login.LoginRequest;
import com.vehicle.rental.zelezniak.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static com.vehicle.rental.zelezniak.config.TestConstants.NUMBER_OF_USERS;
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
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private User user;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        user = UserCreator.createTestUser();
    }

    @Test
    void shouldRegisterUserWhenRequestContainsRequiredData() throws Exception {
        UserName name = user.getName();
        Address address = user.getAddress();

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.address.street.streetName").value(address.getStreet().streetName()))
                .andExpect(jsonPath("$.address.houseNumber").value(address.getHouseNumber()))
                .andExpect(jsonPath("$.address.flatNumber").value(address.getFlatNumber()))
                .andExpect(jsonPath("$.address.city.cityName").value(address.getCity().cityName()))
                .andExpect(jsonPath("$.address.postalCode").value(address.getPostalCode()))
                .andExpect(jsonPath("$.address.country.countryName").value(address.getCountry().countryName()));

        assertEquals(NUMBER_OF_USERS + 1, userRepository.count());
    }

    @Test
    void shouldNotRegisterUserWhenNameIsInvalid() throws Exception {
        user.setName(new UserName("aa", "a"));
        int expectedErrors = 2;

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors", hasSize(expectedErrors)))
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("First name must contains at least 3 characters",
                                "Last name must contains at least 2 characters"
                        )));
    }

    @Test
    void shouldNotRegisterUserWhenCredentialsInvalid() throws Exception {
        user.setCredentials(new UserCredentials(user.getEmail(), "a"));

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("Password must contains at least 5 characters."
                        )));
    }

    @Test
    void shouldNotRegisterUserWhenAddressInvalid() throws Exception {
        Address address = user.getAddress();
        address.setStreet(new Street(""));
        address.setCountry(new Country(""));

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("Street name cannot be blank. Please provide a valid street name.",
                                "Country name cannot be blank. Please provide a valid country name."
                        )));
    }

    @Test
    void shouldLoginUserWithCorrectCredentials() throws Exception {
        authService.register(user);
        String email = user.getEmail();
        LoginRequest loginRequest = new LoginRequest(email, "somepassword");

        User registeredUser = userRepository.findByCredentialsEmail(email).get();
        UserName name = registeredUser.getName();
        Address address = registeredUser.getAddress();

        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.user.name.firstName").value(name.getFirstName()))
                .andExpect(jsonPath("$.user.name.lastName").value(name.getLastName()))
                .andExpect(jsonPath("$.user.email").value(user.getEmail()))
                .andExpect(jsonPath("$.user.address.street.streetName").value(address.getStreet().streetName()))
                .andExpect(jsonPath("$.user.address.houseNumber").value(address.getHouseNumber()))
                .andExpect(jsonPath("$.user.address.flatNumber").value(address.getFlatNumber()))
                .andExpect(jsonPath("$.user.address.city.cityName").value(address.getCity().cityName()))
                .andExpect(jsonPath("$.user.address.postalCode").value(address.getPostalCode()))
                .andExpect(jsonPath("$.user.address.country.countryName").value(address.getCountry().countryName()))
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
        user.setCredentials(new UserCredentials("wrongemail@com", "somepassword"));
        String email = user.getEmail();

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(jsonPath("$.message").value(
                        "Email " + email + " has invalid pattern."));
    }
}
