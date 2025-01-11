package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.dto.AvailableVehiclesCriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.AvailableVehiclesRetriever;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvailableVehiclesRetrieverTest {

    private Map<Long, Vehicle> vehicleMap;
    private final Pageable PAGEABLE = PageRequest.of(0, NUMBER_OF_VEHICLES);

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
    private CriteriaSearchRequests searchRequests;

    @BeforeAll
    void setupData() {
        vehicleMap = new HashMap<>();
        databaseSetup.setupAllTables();
        vehicleMap.put(VEHICLE_1_ID, vehicleService.findById(VEHICLE_1_ID));
        vehicleMap.put(VEHICLE_2_ID, vehicleService.findById(VEHICLE_2_ID));
        vehicleMap.put(VEHICLE_3_ID, vehicleService.findById(VEHICLE_3_ID));
        vehicleMap.put(VEHICLE_4_ID, vehicleService.findById(VEHICLE_4_ID));
        vehicleMap.put(VEHICLE_5_ID, vehicleService.findById(VEHICLE_5_ID));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod1() {
        RentDuration duration = durationCreator.createDuration1();

        Page<Vehicle> page = vehiclesRetriever.findAvailableVehiclesByCriteria(getSearchRequest(duration), PAGEABLE);
        List<Vehicle> availableVehicles = page.getContent();

        assertEquals(EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_1, availableVehicles.size());
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod2() {
        Vehicle motorcycle = vehicleCreator.createMotorcycleWithId2();
        motorcycle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleService.update(motorcycle.getId(), motorcycle);
        RentDuration duration = durationCreator.createDuration2();

        Collection<Vehicle> vehicles = vehiclesRetriever.findAvailableVehiclesByRentDuration(duration);
        List<Vehicle> availableVehicles = (List<Vehicle>) vehicles;

        assertEquals(EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_2 - 1, availableVehicles.size());
        assertFalse(availableVehicles.contains(vehicleMap.get(VEHICLE_1_ID)));
        assertFalse(availableVehicles.contains(vehicleMap.get(VEHICLE_2_ID)));
        assertTrue(availableVehicles.contains(vehicleMap.get(VEHICLE_3_ID)));
        assertTrue(availableVehicles.contains(vehicleMap.get(VEHICLE_4_ID)));
        assertTrue(availableVehicles.contains(vehicleMap.get(VEHICLE_5_ID)));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod3() {
        RentDuration duration = durationCreator.createDuration3();

        Page<Vehicle> page = vehiclesRetriever.findAvailableVehiclesByCriteria(getSearchRequest(duration), PAGEABLE);
        List<Vehicle> availableVehicles = page.getContent();

        assertEquals(EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_3, availableVehicles.size());
        assertTrue(availableVehicles.contains(vehicleMap.get(VEHICLE_1_ID)));
        assertFalse(availableVehicles.contains(vehicleMap.get(VEHICLE_2_ID)));
        assertFalse(availableVehicles.contains(vehicleMap.get(VEHICLE_3_ID)));
        assertFalse(availableVehicles.contains(vehicleMap.get(VEHICLE_4_ID)));
        assertFalse(availableVehicles.contains(vehicleMap.get(VEHICLE_5_ID)));
    }

    private AvailableVehiclesCriteriaSearchRequest getSearchRequest(RentDuration duration) {
        return new AvailableVehiclesCriteriaSearchRequest(duration, searchRequests.getEmptySearchRequest());
    }
}
