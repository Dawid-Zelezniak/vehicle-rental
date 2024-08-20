package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.TokenGenerator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects.Engine;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects.Year;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle_domain.service.VehicleService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class VehicleControllerTest {

    private static Vehicle vehicleWithId5;
    private static Vehicle vehicleWithId6;

    private static final Pageable pageable = PageRequest.of(0, 5);
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private TokenGenerator tokenGenerator;
    private String adminToken;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        adminToken = tokenGenerator.generateToken(ROLE_ADMIN);
        vehicleWithId5 = vehicleCreator.createCarWithId5();
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldReturnAllVehicles() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        var info = vehicleWithId5.getVehicleInformation();
        Year productionYear = info.getProductionYear();
        Engine engine = info.getEngine();
        String fuelType = engine.getFuelType().toString();
        String gearType = info.getGearType().toString();
        String status = vehicleWithId5.getStatus().toString();

        mockMvc.perform(get("/vehicles/")
                        .header("Authorization", "Bearer " + token)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[0].id").value(vehicleWithId5.getId()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.model").value(info.getModel()))
                .andExpect(jsonPath("$.content[0].vehicleInformation.brand").value(info.getBrand()))
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
                .andExpect(jsonPath("$.content[0].pricePerDay.value").value(getValueFromMoney(vehicleWithId5.getPricePerDay())))
                .andExpect(jsonPath("$.content[0].deposit.value").value(getValueFromMoney(vehicleWithId5.getDeposit())))
                .andExpect(jsonPath("$.content[0].status").value(status));
    }

    @Test
    void shouldFindVehicleById() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        var info = vehicleWithId5.getVehicleInformation();
        Year productionYear = info.getProductionYear();
        Engine engine = info.getEngine();
        String fuelType = engine.getFuelType().toString();
        String gearType = info.getGearType().toString();
        String status = vehicleWithId5.getStatus().toString();

        mockMvc.perform(get("/vehicles/{id}", vehicleWithId5.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleWithId5.getId()))
                .andExpect(jsonPath("$.vehicleInformation.brand").value(info.getBrand()))
                .andExpect(jsonPath("$.vehicleInformation.model").value(info.getModel()))
                .andExpect(jsonPath("$.vehicleInformation.registrationNumber.registration").value(info.getRegistrationNumber().getRegistration()))
                .andExpect(jsonPath("$.vehicleInformation.productionYear.year").value(productionYear.getYear()))
                .andExpect(jsonPath("$.vehicleInformation.description").value(info.getDescription()))
                .andExpect(jsonPath("$.vehicleInformation.engine.cylinders").value(engine.getCylinders()))
                .andExpect(jsonPath("$.vehicleInformation.engine.engineType").value(engine.getEngineType()))
                .andExpect(jsonPath("$.vehicleInformation.engine.fuelType").value(fuelType))
                .andExpect(jsonPath("$.vehicleInformation.engine.displacement").value(engine.getDisplacement()))
                .andExpect(jsonPath("$.vehicleInformation.engine.horsepower").value(engine.getHorsepower()))
                .andExpect(jsonPath("$.vehicleInformation.gearType").value(gearType))
                .andExpect(jsonPath("$.vehicleInformation.seatsNumber").value(info.getSeatsNumber()))
                .andExpect(jsonPath("$.pricePerDay.value").value(getValueFromMoney(vehicleWithId5.getPricePerDay())))
                .andExpect(jsonPath("$.deposit.value").value(getValueFromMoney(vehicleWithId5.getDeposit())))
                .andExpect(jsonPath("$.status").value(status));
    }

    @Test
    void shouldNotFindVehicleById() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long nonExistentId = 20L;
        mockMvc.perform(get("/vehicles/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle with id: " + nonExistentId + " does not exists."));
    }

    @Test
    void shouldAddVehicleForRoleADMIN() throws Exception {
        Vehicle testCar = vehicleCreator.createTestCar();
        mockMvc.perform(post("/vehicles/add")
                        .content(objectMapper.writeValueAsString(testCar))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated());

        assertEquals(6, vehicleRepository.count());
        assertTrue(vehicleRepository.existsByVehicleInformationRegistrationNumber(testCar.getRegistrationNumber()));
    }

    @Test
    void shouldNotAddVehicleWithExistingRegistrationNumber() throws Exception {
        RegistrationNumber n = vehicleWithId5.getRegistrationNumber();
        mockMvc.perform(post("/vehicles/add")
                        .content(objectMapper.writeValueAsString(vehicleWithId5))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle with registration number : " + n.getRegistration() + " already exists"));
    }

    @Test
    void shouldNotAddVehicleForRoleUSER() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Vehicle testCar = vehicleCreator.createTestCar();
        mockMvc.perform(post("/vehicles/add")
                        .content(objectMapper.writeValueAsString(testCar))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateVehicle() throws Exception {
        Long vehicle5Id = vehicleWithId5.getId();
        Vehicle newData = vehicleCreator.buildVehicle5WithDifferentData();

        mockMvc.perform(put("/vehicles/update/{id}", vehicle5Id)
                .content(objectMapper.writeValueAsString(newData))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
        ).andExpect(status().isOk());

        Vehicle updated = vehicleService.findById(vehicle5Id);
        assertEquals(newData, updated);
    }

    @Test
    @DisplayName("Should not update vehicle when new data contains an existing registration number")
    void shouldNotUpdateVehicle() throws Exception {
        RegistrationNumber n = vehicleWithId6.getRegistrationNumber();
        Vehicle newData = vehicleCreator.buildVehicle5WithDifferentData();
        var vehicleInformation = newData.getVehicleInformation();
        var infoWithExistentRegistration = vehicleInformation.toBuilder()
                .registrationNumber(n)
                .build();
        newData.setVehicleInformation(infoWithExistentRegistration);
        Long vehicle5Id = vehicleWithId5.getId();

        mockMvc.perform(put("/vehicles/update/{id}", vehicle5Id)
                        .content(objectMapper.writeValueAsString(newData))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle with registration number : " + n.getRegistration() + " already exists"));
    }

    @Test
    void shouldUpdateVehicleForRoleUSER() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long vehicle5Id = vehicleWithId5.getId();
        Vehicle newData = vehicleCreator.buildVehicle5WithDifferentData();

        mockMvc.perform(put("/vehicles/update/{id}", vehicle5Id)
                .content(objectMapper.writeValueAsString(newData))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteVehicle() throws Exception {
        vehicleWithId5.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleRepository.save(vehicleWithId5);

        assertEquals(5, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/delete/{id}", vehicleWithId5.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertEquals(4, vehicleRepository.count());

        Pageable pageable = PageRequest.of(0, 5);
        Page<Vehicle> page = vehicleService.findAll(pageable);
        List<Vehicle> list = page.get().toList();
        assertFalse(list.contains(vehicleWithId5));
    }

    @Test
    void shouldNotDeleteVehicleForNotExistingId() throws Exception {
        Long notExistingId = 20L;

        assertEquals(5, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/delete/{id}", notExistingId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle with id: " + notExistingId + " does not exists."));

        assertEquals(5, vehicleRepository.count());
    }

    @Test
    void shouldNotDeleteVehicleForRoleUSER() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        vehicleWithId5.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleRepository.save(vehicleWithId5);

        assertEquals(5, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/delete/{id}", vehicleWithId5.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldNotDeleteVehicleWithStatusAvailable() throws Exception {
        Long id = vehicleWithId5.getId();

        assertEquals(5, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/delete/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle must be in status UNAVAILABLE before it can be deleted."));

        assertEquals(5, vehicleRepository.count());
    }

    private double getValueFromMoney(Money totalCost) {
        BigDecimal value = totalCost.getValue();
        return value.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
