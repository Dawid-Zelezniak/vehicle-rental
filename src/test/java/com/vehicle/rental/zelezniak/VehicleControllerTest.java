package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.UserLogin;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Engine;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.VehicleInformation;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.vehicle.rental.zelezniak.config.TestConstants.NUMBER_OF_VEHICLES;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class VehicleControllerTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, NUMBER_OF_VEHICLES);
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

    private static Vehicle vehicleWithId1;
    private static Vehicle vehicleWithId2;
    private static String adminToken;
    private static String userToken;


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
    private UserLogin login;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        vehicleWithId1 = vehicleCreator.createCarWithId1();
        vehicleWithId2 = vehicleCreator.createMotorcycleWithId2();
        if (adminToken == null && userToken == null) {
            adminToken = generateToken("admin@gmail.com", "admin1234");
            userToken = generateToken("usertwo@gmail.com", "somepass");
        }
    }

    @Test
    void shouldReturnAllVehicles() throws Exception {
        var info = vehicleWithId1.getVehicleInformation();
        Year productionYear = info.getProductionYear();
        Engine engine = info.getEngine();
        String fuelType = engine.getFuelType().toString();
        String gearType = info.getGearType().toString();
        String status = vehicleWithId1.getStatus().toString();

        mockMvc.perform(get("/vehicles")
                        .header("Authorization", "Bearer " + userToken)
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize())))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(NUMBER_OF_VEHICLES)))
                .andExpect(jsonPath("$.content[0].id").value(vehicleWithId1.getId()))
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
                .andExpect(jsonPath("$.content[0].pricePerDay.value").value(getValueFromMoney(vehicleWithId1.getPricePerDay())))
                .andExpect(jsonPath("$.content[0].deposit.value").value(getValueFromMoney(vehicleWithId1.getDeposit())))
                .andExpect(jsonPath("$.content[0].status").value(status));
    }

    @Test
    void shouldFindVehicleById() throws Exception {
        var info = vehicleWithId1.getVehicleInformation();
        Year productionYear = info.getProductionYear();
        Engine engine = info.getEngine();
        String fuelType = engine.getFuelType().toString();
        String gearType = info.getGearType().toString();
        String status = vehicleWithId1.getStatus().toString();

        mockMvc.perform(get("/vehicles/{id}", vehicleWithId1.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleWithId1.getId()))
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
                .andExpect(jsonPath("$.pricePerDay.value").value(getValueFromMoney(vehicleWithId1.getPricePerDay())))
                .andExpect(jsonPath("$.deposit.value").value(getValueFromMoney(vehicleWithId1.getDeposit())))
                .andExpect(jsonPath("$.status").value(status));
    }

    @Test
    void shouldNotFindVehicleById() throws Exception {
        Long nonExistentId = 20L;
        mockMvc.perform(get("/vehicles/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle with id: " + nonExistentId + " does not exist."));
    }

    @Test
    void shouldAddVehicleWhenUserHasRoleADMIN() throws Exception {
        Vehicle testCar = vehicleCreator.createTestCar();
        mockMvc.perform(post("/vehicles")
                        .content(objectMapper.writeValueAsString(testCar))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated());

        assertEquals(NUMBER_OF_VEHICLES + 1, vehicleRepository.count());
        assertTrue(vehicleRepository.existsByVehicleInformationRegistrationNumber(testCar.getRegistrationNumber()));
    }

    @Test
    void shouldNotAddVehicleWhenDataInvalid() throws Exception {
        Vehicle testCar = vehicleCreator.createTestCar();
        VehicleInformation vehicleInformation = testCar.getVehicleInformation();
        VehicleInformation invalid = vehicleInformation.toBuilder()
                .brand("")
                .model("")
                .seatsNumber(0)
                .build();
        testCar.setVehicleInformation(invalid);

        mockMvc.perform(post("/vehicles")
                        .content(objectMapper.writeValueAsString(testCar))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("Brand can not be blank.",
                                "Model can not be blank.",
                                "Seats number can not be lower than 1")));
    }

    @Test
    void shouldNotAddVehicleWithExistingRegistrationNumber() throws Exception {
        mockMvc.perform(post("/vehicles")
                        .content(objectMapper.writeValueAsString(vehicleWithId1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle with registration number : " + vehicleWithId1.getRegistrationValue() + " already exist."));
    }

    @Test
    void shouldNotAddVehicleForRoleUSER() throws Exception {
        Vehicle testCar = vehicleCreator.createTestCar();
        mockMvc.perform(post("/vehicles")
                        .content(objectMapper.writeValueAsString(testCar))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateVehicleWhenDataIsCorrect() throws Exception {
        Long vehicleToUpdateId = vehicleWithId1.getId();
        Vehicle newData = vehicleCreator.buildVehicle1WithDifferentData();

        mockMvc.perform(put("/vehicles/{id}", vehicleToUpdateId)
                .content(objectMapper.writeValueAsString(newData))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
        ).andExpect(status().isOk());

        Vehicle updated = vehicleService.findById(vehicleToUpdateId);
        assertEquals(newData, updated);
    }

    @Test
    @DisplayName("Should not update vehicle when new data contains an existing registration number")
    void shouldNotUpdateVehicle() throws Exception {
        RegistrationNumber n = vehicleWithId2.getRegistrationNumber();
        Vehicle newData = vehicleCreator.buildVehicle1WithDifferentData();
        var vehicleInformation = newData.getVehicleInformation();
        var infoWithExistentRegistration = vehicleInformation.toBuilder()
                .registrationNumber(n)
                .build();
        newData.setVehicleInformation(infoWithExistentRegistration);
        Long vehicleToUpdateId = vehicleWithId1.getId();

        mockMvc.perform(put("/vehicles/{id}", vehicleToUpdateId)
                        .content(objectMapper.writeValueAsString(newData))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle with registration number : " + n.getRegistration() + " already exist."));
    }

    @Test
    void shouldNotUpdateVehicleForRoleUSER() throws Exception {
        Long vehicleToUpdateId = vehicleWithId1.getId();
        Vehicle newData = vehicleCreator.buildVehicle1WithDifferentData();

        mockMvc.perform(put("/vehicles/{id}", vehicleToUpdateId)
                .content(objectMapper.writeValueAsString(newData))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken)
        ).andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteWhenVehicleStatusIsUNAVAILABLE() throws Exception {
        vehicleWithId1.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleRepository.save(vehicleWithId1);

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/{id}", vehicleWithId1.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertEquals(NUMBER_OF_VEHICLES - 1, vehicleRepository.count());

        List<Vehicle> list = vehicleRepository.findAll();
        assertFalse(list.contains(vehicleWithId1));
    }

    @Test
    void shouldNotDeleteVehicleWithStatusAVAILABLE() throws Exception {
        Long vehicleToDeleteId = vehicleWithId1.getId();

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/{id}", vehicleToDeleteId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Vehicle must be in status UNAVAILABLE before it can be deleted."));

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
    }

    @Test
    void shouldNotDeleteVehicleWhenDoesNotExists() throws Exception {
        Long notExistingId = 20L;

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/{id}", notExistingId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Vehicle with id: " + notExistingId + " does not exist."));

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
    }

    @Test
    void shouldNotDeleteVehicleForRoleUSER() throws Exception {
        vehicleWithId1.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleRepository.save(vehicleWithId1);

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
        mockMvc.perform(delete("/vehicles/{id}", vehicleWithId1.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private String generateToken(String email, String password) {
        return login.loginUser(email, password);
    }

    private double getValueFromMoney(Money totalCost) {
        BigDecimal value = totalCost.value();
        return value.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
