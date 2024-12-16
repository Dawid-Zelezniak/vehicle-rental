package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.VehicleInformation;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class VehicleServiceTest {

    private static Vehicle vehicleWithId1;
    private static Vehicle vehicleWithId2;

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private DatabaseSetup databaseSetup;

    @BeforeEach
    void setupDatabase() throws Exception {
        databaseSetup.setupAllTables();
        vehicleWithId1 = vehicleCreator.createCarWithId1();
        vehicleWithId2 = vehicleCreator.createMotorcycleWithId2();
    }

    @Test
    void shouldReturnPageOf2Vehicles() {
        int pageSize = 2;
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Vehicle> page = vehicleService.findAll(pageable);
        List<Vehicle> vehicles = page.getContent();

        assertEquals(pageSize, vehicles.size());
    }

    @Test
    void shouldReturnAllVehicles() {
        Pageable pageable = PageRequest.of(0, NUMBER_OF_VEHICLES);
        Page<Vehicle> page = vehicleService.findAll(pageable);
        List<Vehicle> vehicles = page.getContent();

        assertTrue(vehicles.contains(vehicleWithId1));
        assertTrue(vehicles.contains(vehicleWithId2));
        assertEquals(NUMBER_OF_VEHICLES, vehicles.size());
    }

    @Test
    void shouldFindVehicleById() {
        Vehicle vehicle5 = vehicleService.findById(vehicleWithId1.getId());
        Vehicle vehicle6 = vehicleService.findById(vehicleWithId2.getId());

        assertEquals(vehicleWithId1, vehicle5);
        assertEquals(vehicleWithId2, vehicle6);
    }

    @Test
    void shouldNotFindVehicleByIdWhenDoesNotExist() {
        Long nonExistentId = 20L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> vehicleService.findById(nonExistentId));
        assertEquals("Vehicle with id: " + nonExistentId + " does not exist.", exception.getMessage());
    }

    @Test
    void shouldAddVehicleIfDoesNotExistByRegistrationNumber() {
        Vehicle testCar = vehicleCreator.createTestCar();

        Vehicle vehicle = vehicleService.addVehicle(testCar);

        assertEquals(NUMBER_OF_VEHICLES + 1, vehicleRepository.count());
        assertTrue(vehicleRepository.existsByVehicleInformationRegistrationNumber(vehicle.getRegistrationNumber()));
    }

    @Test
    void shouldNotAddVehicleIfExistByRegistrationNumber() {
        IllegalArgumentException assertion1 = assertThrows(IllegalArgumentException.class, () -> vehicleService.addVehicle(vehicleWithId1));
        IllegalArgumentException assertion2 = assertThrows(IllegalArgumentException.class, () -> vehicleService.addVehicle(vehicleWithId2));

        assertEquals("Vehicle with registration number : " + vehicleWithId1.getRegistrationValue() + " already exist.", assertion1.getMessage());
        assertEquals("Vehicle with registration number : " + vehicleWithId2.getRegistrationValue() + " already exist.", assertion2.getMessage());
    }

    @Test
    void shouldUpdateCarWhenNewDataIsCorrect() {
        Long vehicleToUpdateId = vehicleWithId1.getId();
        Vehicle newData = vehicleCreator.buildVehicle1WithDifferentData();

        Vehicle updated = vehicleService.update(vehicleToUpdateId, newData);

        assertEquals(newData, updated);
    }

    @Test
    @DisplayName("Should not update vehicle when new data contains an existing registration number")
    void shouldNotUpdateVehicle() {
        RegistrationNumber existingRegistration = vehicleWithId2.getRegistrationNumber();
        Vehicle newData = vehicleCreator.buildVehicle1WithDifferentData();
        VehicleInformation vehicleInformation = newData.getVehicleInformation();
        VehicleInformation infoWithExistingRegistration = vehicleInformation.toBuilder()
                .registrationNumber(existingRegistration)
                .build();
        newData.setVehicleInformation(infoWithExistingRegistration);
        Long vehicleToUpdateId = vehicleWithId1.getId();

        assertThrows(IllegalArgumentException.class, () -> vehicleService.update(vehicleToUpdateId, newData));
    }

    @Test
    void shouldUpdateMotorcycleData() {
        Vehicle newData = vehicleWithId2;
        VehicleInformation vehicleInformation = newData.getVehicleInformation();
        vehicleInformation.toBuilder()
                .description("*new description*");
        int newDepositValue = 1000;
        newData.setDeposit(new Money(BigDecimal.valueOf(newDepositValue)));

        Vehicle updated = vehicleService.update(vehicleWithId2.getId(), newData);

        assertEquals(newData, updated);
    }

    @Test
    void shouldDeleteVehicle() {
        vehicleWithId1.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleRepository.save(vehicleWithId1);

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());

        vehicleService.delete(vehicleWithId1.getId());

        assertEquals(NUMBER_OF_VEHICLES - 1, vehicleRepository.count());

        List<Vehicle> all = vehicleRepository.findAll();
        assertFalse(all.contains(vehicleWithId1));
    }

    @Test
    void shouldNotDeleteVehicleWhenDoesNotExist() {
        Long nonExistentId = 20L;

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> vehicleService.delete(nonExistentId));
        assertEquals("Vehicle with id: " + nonExistentId + " does not exist.", exception.getMessage());
        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
    }

    @Test
    void shouldThrowWhenDeletingVehicleWithStatusAvailable() {
        Long id = vehicleWithId1.getId();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> vehicleService.delete(id));
        assertEquals("Vehicle must be in status UNAVAILABLE before it can be deleted.", exception.getMessage());
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod() {
        Pageable pageable = PageRequest.of(0, NUMBER_OF_VEHICLES);
        RentDurationCreator durationCreator = new RentDurationCreator();
        Page<Vehicle> availableVehicles = vehicleService.findAvailableVehicles(durationCreator.createDuration2(), pageable);
        List<Vehicle> vehicles = availableVehicles.getContent();

        assertFalse(vehicles.contains(vehicleWithId1));
        assertTrue(vehicles.contains(vehicleWithId2));
        assertTrue(vehicles.contains(vehicleService.findById(VEHICLE_3_ID)));
        assertTrue(vehicles.contains(vehicleService.findById(VEHICLE_4_ID)));
        assertTrue(vehicles.contains(vehicleService.findById(VEHICLE_5_ID)));
    }
}
