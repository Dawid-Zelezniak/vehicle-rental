package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.service.VehicleService;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
//import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.vehicle_domain.service.AvailableVehiclesRetriever;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class AvailableVehiclesRetrieverTest {

    private static Map<Long, Vehicle> vehicleMap;
    private static final Pageable PAGEABLE = PageRequest.of(0, 5);

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

    @BeforeEach
    void setupData() throws IOException {
        vehicleMap = new HashMap<>();
        databaseSetup.setupAllTables();
        vehicleMap.put(5L, vehicleService.findById(5L));
        vehicleMap.put(6L, vehicleService.findById(6L));
        vehicleMap.put(7L, vehicleService.findById(7L));
        vehicleMap.put(8L, vehicleService.findById(8L));
        vehicleMap.put(9L, vehicleService.findById(9L));
    }

    @AfterEach
    void dropData() {
        databaseSetup.dropAllTables();
        vehicleMap = null;
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod1() {
        RentDuration duration = durationCreator.createDuration1();

        Page<Vehicle> page = vehiclesRetriever.findVehiclesAvailableInPeriod(duration, PAGEABLE);
        List<Vehicle> availableVehicles = page.getContent();

        assertEquals(0, availableVehicles.size());
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod2() {
        Vehicle motorcycle = vehicleCreator.createMotorcycleWithId6();
        motorcycle.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleService.update(6L, motorcycle);
        RentDuration duration = durationCreator.createDuration2();

        Page<Vehicle> page = vehiclesRetriever.findVehiclesAvailableInPeriod(duration, PAGEABLE);
        List<Vehicle> availableVehicles = page.getContent();

        assertEquals(3, availableVehicles.size());
        assertFalse(availableVehicles.contains(vehicleMap.get(5L)));
        assertFalse(availableVehicles.contains(vehicleMap.get(6L)));
        assertTrue(availableVehicles.contains(vehicleMap.get(7L)));
        assertTrue(availableVehicles.contains(vehicleMap.get(8L)));
        assertTrue(availableVehicles.contains(vehicleMap.get(9L)));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod3() {
        RentDuration duration = durationCreator.createDuration3();

        Page<Vehicle> page = vehiclesRetriever.findVehiclesAvailableInPeriod(duration, PAGEABLE);
        List<Vehicle> availableVehicles = page.getContent();

        assertEquals(1, availableVehicles.size());
        assertTrue(availableVehicles.contains(vehicleMap.get(5L)));
        assertFalse(availableVehicles.contains(vehicleMap.get(6L)));
        assertFalse(availableVehicles.contains(vehicleMap.get(7L)));
        assertFalse(availableVehicles.contains(vehicleMap.get(8L)));
        assertFalse(availableVehicles.contains(vehicleMap.get(9L)));
    }
}
