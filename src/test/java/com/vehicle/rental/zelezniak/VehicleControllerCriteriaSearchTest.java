package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.UserLogin;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Engine;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Year;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
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

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class VehicleControllerCriteriaSearchTest {

    private static Vehicle vehicleWithId1;
    private static String adminToken;
    private static String userToken;

    private static final Pageable PAGEABLE = PageRequest.of(0, 5);
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

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
    private ObjectMapper mapper;
    @Autowired
    private UserLogin login;
    @Autowired
    private CriteriaSearchRequests searchRequests;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        vehicleWithId1 = vehicleCreator.createCarWithId1();
        if (adminToken == null && userToken == null) {
            adminToken = generateToken("admin@gmail.com", "admin1234");
            userToken = generateToken("usertwo@gmail.com", "somepass");
        }
    }

    @Test
    void shouldFindVehiclesByCriteriaModel() throws Exception {
        int resultSize = 1;
        String value = vehicleWithId1.getVehicleInformation().getModel();
        performCriteriaRequest(searchRequests.getModelSearchRequest(value),
                resultSize, vehicleWithId1, userToken);
    }

    @Test
    void shouldFindVehiclesByCriteriaBrand() throws Exception {
        Vehicle vehicle = vehicleService.findById(VEHICLE_3_ID);
        var info = vehicle.getVehicleInformation();
        int resultSize = 1;
        String value = info.getBrand();
        performCriteriaRequest(searchRequests.getBrandSearchRequest(value),
                resultSize, vehicle, userToken);
    }

    @Test
    @DisplayName("Find vehicles by registration number when role is ADMIN")
    void shouldFindVehiclesByCriteriaRegistrationNumber() throws Exception {
        Vehicle vehicle = vehicleService.findById(VEHICLE_4_ID);
        int resultSize = 1;
        RegistrationNumber registrationNumber = vehicle.getRegistrationNumber();
        String value = registrationNumber.getRegistration();
        performCriteriaRequest(searchRequests.getRegistrationSearchRequest(value),
                resultSize, vehicle, adminToken);
    }

    @Test
    @DisplayName("Finding vehicles by registration number with role USER should throw exception")
    void shouldNotFindVehiclesByCriteriaRegistrationNumber() throws Exception {
        Vehicle vehicle = vehicleService.findById(VEHICLE_4_ID);
        RegistrationNumber registrationNumber = vehicle.getRegistrationNumber();
        String value = registrationNumber.getRegistration();
        performCriteriaRegistrationNumber(searchRequests.getRegistrationSearchRequest(value));
    }

    @Test
    void shouldFindVehiclesByCriteriaProductionYear() throws Exception {
        Vehicle vehicle = vehicleService.findById(VEHICLE_4_ID);
        var info = vehicle.getVehicleInformation();
        String value = String.valueOf(info.getProductionYear().getYear());

        performCriteriaRequest(searchRequests.getProductionYearSearchRequest(Integer.valueOf(value))
                , NUMBER_OF_VEHICLES_PRODUCED_IN_2020, vehicle, userToken);
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusAvailable() throws Exception {
        setVehicleStatusUNAVAILABLE(VEHICLE_4_ID);

        performCriteriaRequest(searchRequests.getStatusSearchRequest("available"),
                NUMBER_OF_AVAILABLE_VEHICLES - 1, vehicleWithId1, userToken);
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusUnavailable() throws Exception {
        setVehicleStatusUNAVAILABLE(VEHICLE_4_ID);
        Vehicle vehicle = vehicleService.findById(VEHICLE_4_ID);

        int resultSize = 1;
        performCriteriaRequest(searchRequests.getStatusSearchRequest("unavailable"),
                resultSize, vehicle, userToken);
    }

    private <T> void performCriteriaRequest(CriteriaSearchRequest searchRequest,
                                            int resultSize, Vehicle result, String token) throws Exception {
        var info = result.getVehicleInformation();
        Year productionYear = info.getProductionYear();
        Engine engine = info.getEngine();
        String fuelType = engine.getFuelType().toString();
        String gearType = info.getGearType().toString();
        double pricePerDay = result.getPricePerDay()
                .value().setScale(2, RoundingMode.HALF_UP).doubleValue();
        String status = result.getStatus().toString();

        mockMvc.perform(post("/vehicles/criteria/search")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(searchRequest))
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + token))
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

    private String generateToken(String email, String password) {
        return login.loginUser(email, password);
    }

    private void setVehicleStatusUNAVAILABLE(Long vehicleId) {
        Vehicle vehicle = vehicleService.findById(vehicleId);
        vehicle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleRepository.save(vehicle);
    }

    private void performCriteriaRegistrationNumber(CriteriaSearchRequest searchRequest) throws Exception {
        mockMvc.perform(post("/vehicles/criteria/search")
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
