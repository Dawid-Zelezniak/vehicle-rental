package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.config.TokenGenerator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.AvailableVehiclesRetriever;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class VehicleControllerAvailableVehiclesSearchTest {

    private static Map<Long, Vehicle> vehicleMap;
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;
    private static final String ROLE_USER = "USER";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private AvailableVehiclesRetriever vehiclesRetriever;
    @Autowired
    private RentDurationCreator durationCreator;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private TokenGenerator tokenGenerator;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setupData() {
        vehicleMap = new HashMap<>();
        databaseSetup.setupAllTables();
        vehicleMap.put(5L, vehicleService.findById(5L));
        vehicleMap.put(7L, vehicleService.findById(7L));
        vehicleMap.put(8L, vehicleService.findById(8L));
        vehicleMap.put(9L, vehicleService.findById(9L));
    }

    @AfterEach
    void dropData() {
        vehicleMap = null;
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod1() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        RentDuration duration = durationCreator.createDuration1();

        mockMvc.perform(post("/vehicles/available/in_period")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(duration))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod2() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Vehicle motorcycle = vehicleCreator.createMotorcycleWithId6();
        motorcycle.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleService.update(6L, motorcycle);
        RentDuration duration = durationCreator.createDuration2();

        mockMvc.perform(post("/vehicles/available/in_period")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(duration))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].id").value(vehicleMap.get(7L).getId()))
                .andExpect(jsonPath("$.content[1].id").value(vehicleMap.get(8L).getId()))
                .andExpect(jsonPath("$.content[2].id").value(vehicleMap.get(9L).getId()));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod3() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        RentDuration duration = durationCreator.createDuration3();

        mockMvc.perform(post("/vehicles/available/in_period")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(duration))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(vehicleMap.get(5L).getId()));
    }
}
