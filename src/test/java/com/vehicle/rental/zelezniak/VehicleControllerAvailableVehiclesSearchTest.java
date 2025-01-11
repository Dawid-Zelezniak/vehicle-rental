package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.vehicle.model.dto.AvailableVehiclesCriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleControllerAvailableVehiclesSearchTest {

    private final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

    private Map<Long, Vehicle> vehicleMap;
    private String userToken;


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private RentDurationCreator durationCreator;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UserLogin login;

    @BeforeAll
    void setupData() {
        vehicleMap = new HashMap<>();
        databaseSetup.setupAllTables();
        vehicleMap.put(VEHICLE_1_ID, vehicleService.findById(VEHICLE_1_ID));
        vehicleMap.put(VEHICLE_3_ID, vehicleService.findById(VEHICLE_3_ID));
        vehicleMap.put(VEHICLE_4_ID, vehicleService.findById(VEHICLE_4_ID));
        vehicleMap.put(VEHICLE_5_ID, vehicleService.findById(VEHICLE_5_ID));
        if (userToken == null) {
            userToken = generateToken("usertwo@gmail.com", "somepass");
        }
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod1() throws Exception {
        RentDuration duration = durationCreator.createDuration1();

        mockMvc.perform(post("/vehicles/available/in_period")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getSearchRequest(duration)))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_1)));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod2() throws Exception {
        Vehicle vehicle = vehicleCreator.createMotorcycleWithId2();
        vehicle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleService.update(vehicle.getId(), vehicle);
        RentDuration duration = durationCreator.createDuration2();

        mockMvc.perform(post("/vehicles/available/in_period")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getSearchRequest(duration)))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_2 - 1)))
                .andExpect(jsonPath("$.content[*].id").value(containsInAnyOrder(
                        (int) VEHICLE_3_ID,
                        (int) VEHICLE_4_ID,
                        (int) VEHICLE_5_ID,
                        (int) VEHICLE_6_ID)));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod3() throws Exception {
        RentDuration duration = durationCreator.createDuration3();

        mockMvc.perform(post("/vehicles/available/in_period")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(getSearchRequest(duration)))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_3)))
                .andExpect(jsonPath("$.content[0].id").value(vehicleMap.get(VEHICLE_1_ID).getId()));
    }

    private String generateToken(String email, String password) {
        return login.loginUser(email, password);
    }

    private AvailableVehiclesCriteriaSearchRequest getSearchRequest(RentDuration duration) {
        CriteriaSearchRequests searchRequests = new CriteriaSearchRequests();
        return new AvailableVehiclesCriteriaSearchRequest(duration, searchRequests.getEmptySearchRequest());
    }
}
