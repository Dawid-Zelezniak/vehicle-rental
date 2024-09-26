package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.TokenGenerator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Engine;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Year;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.model.util.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.RoundingMode;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class VehicleControllerCriteriaSearchTest {

    private static Vehicle vehicleWithId5;
    private static final Pageable PAGEABLE = PageRequest.of(0, 5);
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;
    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private TokenGenerator tokenGenerator;
    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        vehicleWithId5 = vehicleCreator.createCarWithId5();
    }

    @Test
    void shouldFindVehiclesByCriteriaModel() throws Exception {
        String criteria = "model";
        int resultSize = 1;
        String value = vehicleWithId5.getVehicleInformation().getModel();
        performCriteriaRequest(new CriteriaSearchRequest<>(criteria, value),
                resultSize, vehicleWithId5, USER);
    }

    @Test
    void shouldFindVehiclesByCriteriaBrand() throws Exception {
        Vehicle vehicle7 = vehicleService.findById(7L);
        var info = vehicle7.getVehicleInformation();
        String criteria = "brand";
        int resultSize = 1;
        String value = info.getBrand();
        performCriteriaRequest(new CriteriaSearchRequest<>(criteria, value),
                resultSize, vehicle7, USER);
    }

    @Test
    @DisplayName("Find vehicles by registration number when role is ADMIN")
    void shouldFindVehiclesByCriteriaRegistrationNumber() throws Exception {
        Vehicle vehicle8 = vehicleService.findById(8L);
        String criteria = "registration number";
        int resultSize = 1;
        RegistrationNumber registrationNumber = vehicle8.getRegistrationNumber();
        String value = registrationNumber.getRegistration();
        performCriteriaRequest(new CriteriaSearchRequest<>(criteria, value),
                resultSize, vehicle8, ADMIN);
    }

    @Test
    @DisplayName("Finding vehicles by registration number with role USER should throw exception")
    void shouldNotFindVehiclesByCriteriaRegistrationNumber() throws Exception {
        Vehicle vehicle8 = vehicleService.findById(8L);
        String criteria = "registration number";
        RegistrationNumber registrationNumber = vehicle8.getRegistrationNumber();
        String value = registrationNumber.getRegistration();
        performCriteriaRegistrationNumber(new CriteriaSearchRequest<>(criteria, value));
    }

    @Test
    void shouldFindVehiclesByCriteriaProductionYear() throws Exception {
        Vehicle vehicle8 = vehicleService.findById(8L);
        var info = vehicle8.getVehicleInformation();
        String criteria = "production year";
        int resultSize = 2;
        String value = String.valueOf(info.getProductionYear().getYear());
        performCriteriaRequest(new CriteriaSearchRequest<>(criteria, value)
                , resultSize, vehicle8, USER);
    }

    @Test
    void shouldNotFindVehiclesByNonExistentCriteria() throws Exception {
        String userToken = tokenGenerator.generateToken(USER);
        String criteria = "wheels number";
        String value = "4";
        var request = new CriteriaSearchRequest<>(criteria, value);
        mockMvc.perform(post("/vehicles/criteria")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Unknown criteria type " + criteria));
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusAvailable() throws Exception {
        Vehicle vehicle8 = vehicleService.findById(8L);
        vehicle8.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleRepository.save(vehicle8);

        String criteria = "status";
        int resultSize = 4;
        String value = "available";
        performCriteriaRequest(new CriteriaSearchRequest<>(criteria, value),
                resultSize, vehicleWithId5, USER);
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusUnavailable() throws Exception {
        Vehicle vehicle8 = vehicleService.findById(8L);
        vehicle8.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleRepository.save(vehicle8);

        String criteria = "status";
        int resultSize = 1;
        String value = "unavailable";
        performCriteriaRequest(new CriteriaSearchRequest<>(criteria, value),
                resultSize, vehicle8, USER);
    }

    private <T> void performCriteriaRequest(CriteriaSearchRequest<T> searchRequest,
                                            int resultSize, Vehicle result, String role) throws Exception {
        String userToken = tokenGenerator.generateToken(role);
        var info = result.getVehicleInformation();
        Year productionYear = info.getProductionYear();
        Engine engine = info.getEngine();
        String fuelType = engine.getFuelType().toString();
        String gearType = info.getGearType().toString();
        double pricePerDay = result.getPricePerDay()
                .value().setScale(2, RoundingMode.HALF_UP).doubleValue();
        String status = result.getStatus().toString();

        mockMvc.perform(post("/vehicles/criteria")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(searchRequest))
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(resultSize)))
                .andExpect(jsonPath("$.content[0].id").value(result.getId()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.brand").value(info.getBrand()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.model").value(info.getModel()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.registrationNumber.registration").value(info.getRegistrationNumber().getRegistration()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.productionYear.year").value(productionYear.getYear()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.description").value(info.getDescription()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.engine.cylinders").value(engine.getCylinders()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.engine.engineType").value(engine.getEngineType()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.engine.fuelType").value(fuelType))
                .andExpect(jsonPath("$.content[0].vehicleInformation.engine.displacement").value(engine.getDisplacement()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.engine.horsepower").value(engine.getHorsepower()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.gearType").value(gearType))
                .andExpect(jsonPath("$.content[0].vehicleInformation.seatsNumber").value(info.getSeatsNumber()))
                .andExpect(jsonPath("$.content[0].pricePerDay.value").value(pricePerDay))
                .andExpect(jsonPath("$.content[0].status").value(status));
    }

    private <T> void performCriteriaRegistrationNumber(CriteriaSearchRequest<T> searchRequest) throws Exception {
        String userToken = tokenGenerator.generateToken(USER);
        mockMvc.perform(post("/vehicles/criteria")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(searchRequest))
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(
                        "Access denied: Only admins can search by registration number"
                ));
    }
}
