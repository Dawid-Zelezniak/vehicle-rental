package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.VehicleInformation;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class VehicleServiceTest {

    private static Vehicle vehicleWithId5;
    private static Vehicle vehicleWithId6;

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DatabaseSetup databaseSetup;

    @BeforeEach
    void setupDatabase() throws Exception {
        databaseSetup.setupAllTables();
        vehicleWithId5 = vehicleCreator.createCarWithId5();
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldReturnPageOf2Vehicles() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Vehicle> page = vehicleService.findAll(pageable);
        List<Vehicle> vehicles = page.getContent();

        assertEquals(2, vehicles.size());
    }

    @Test
    void shouldReturnAllVehicles() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Vehicle> page = vehicleService.findAll(pageable);
        List<Vehicle> vehicles = page.getContent();

        assertTrue(vehicles.contains(vehicleWithId5));
        assertTrue(vehicles.contains(vehicleWithId6));
        assertEquals(5, vehicles.size());
    }

    @Test
    void shouldFindVehicleById() {
        Vehicle vehicle5 = vehicleService.findById(vehicleWithId5.getId());
        Vehicle vehicle6 = vehicleService.findById(vehicleWithId6.getId());

        assertEquals(vehicleWithId5, vehicle5);
        assertEquals(vehicleWithId6, vehicle6);
    }

    @Test
    void shouldNotFindVehicleById() {
        Long nonExistentId = 20L;
        assertThrows(NoSuchElementException.class, () -> vehicleService.findById(nonExistentId));
    }

    @Test
    void shouldAddVehicle() {
        Vehicle testCar = vehicleCreator.createTestCar();

        Vehicle vehicle = vehicleService.addVehicle(testCar);

        assertEquals(6, vehicleRepository.count());
        assertTrue(vehicleRepository.existsByVehicleInformationRegistrationNumber(vehicle.getRegistrationNumber()));
    }

    @Test
    void shouldNotAddVehicle() {
        assertThrows(IllegalArgumentException.class, () -> vehicleService.addVehicle(vehicleWithId5));
        assertThrows(IllegalArgumentException.class, () -> vehicleService.addVehicle(vehicleWithId6));
    }

    @Test
    void shouldUpdateCar() {
        Long vehicle5Id = vehicleWithId5.getId();
        Vehicle newData = vehicleCreator.buildVehicle5WithDifferentData();

        Vehicle updated = vehicleService.update(vehicle5Id, newData);

        assertEquals(newData, updated);
    }

    @Test
    @DisplayName("Should not update vehicle when new data contains an existing registration number")
    void shouldNotUpdateVehicle() {
        RegistrationNumber existingRegistration = vehicleWithId6.getRegistrationNumber();
        Vehicle newData = vehicleCreator.buildVehicle5WithDifferentData();
        VehicleInformation vehicleInformation = newData.getVehicleInformation();
        VehicleInformation infoWithExistingRegistration = vehicleInformation.toBuilder()
                .registrationNumber(existingRegistration)
                .build();
        newData.setVehicleInformation(infoWithExistingRegistration);
        Long vehicleToUpdateId = vehicleWithId5.getId();

        assertThrows(IllegalArgumentException.class, () -> vehicleService.update(vehicleToUpdateId, newData));
    }

    @Test
    void shouldUpdateMotorcycle() {
        Long vehicle6Id = vehicleWithId6.getId();
        Vehicle newData = vehicleWithId6;
        newData.setStatus(Vehicle.Status.UNAVAILABLE);
        newData.setDeposit(new Money(BigDecimal.valueOf(1000)));

        Vehicle updated = vehicleService.update(vehicle6Id, newData);

        assertEquals(newData, updated);
    }

    @Test
    void shouldDeleteVehicle() {
        vehicleWithId5.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleRepository.save(vehicleWithId5);

        assertEquals(5, vehicleRepository.count());

        vehicleService.delete(vehicleWithId5.getId());

        assertEquals(4, vehicleRepository.count());

        List<Vehicle> all = vehicleRepository.findAll();
        assertFalse(all.contains(vehicleWithId5));
    }

    @Test
    void shouldNotDeleteVehicle() {
        Long nonExistentId = 20L;

        assertEquals(5, vehicleRepository.count());
        assertThrows(NoSuchElementException.class, () -> vehicleService.delete(nonExistentId));
        assertEquals(5, vehicleRepository.count());
    }

    @Test
    void shouldNotDeleteVehicleWithStatusAvailable() {
        Long id = vehicleWithId5.getId();
        assertThrows(IllegalStateException.class, () -> vehicleService.delete(id));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod() {
        Pageable pageable = PageRequest.of(0, 5);
        RentDurationCreator durationCreator = new RentDurationCreator();
        Page<Vehicle> availableVehicles = vehicleService.findAvailableVehicles(durationCreator.createDuration2(), pageable);
        List<Vehicle> vehicles = availableVehicles.getContent();

        assertFalse(vehicles.contains(vehicleService.findById(5L)));
        assertTrue(vehicles.contains(vehicleService.findById(6L)));
        assertTrue(vehicles.contains(vehicleService.findById(7L)));
        assertTrue(vehicles.contains(vehicleService.findById(8L)));
        assertTrue(vehicles.contains(vehicleService.findById(9L)));
    }
}
