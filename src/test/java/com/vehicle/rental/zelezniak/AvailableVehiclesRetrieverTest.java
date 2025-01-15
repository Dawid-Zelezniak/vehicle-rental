package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.dto.AvailableVehiclesCriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
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

import java.util.*;

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
        Arrays.asList(VEHICLE_1_ID, VEHICLE_2_ID, VEHICLE_3_ID, VEHICLE_4_ID, VEHICLE_5_ID, VEHICLE_6_ID)
                .forEach(id -> vehicleMap.put(id, vehicleService.findById(id)));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod1() {
        RentDuration duration = durationCreator.createDuration1();

        AvailableVehiclesCriteriaSearchRequest searchRequest = getEmptySearchRequest(duration);
        Page<Vehicle> page = vehiclesRetriever.findAvailableVehiclesByCriteria(searchRequest, PAGEABLE);

        assertAvailableVehiclesCount(page.getNumberOfElements(), EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_1);
        assertAvailableVehicles(page.getContent(), List.of(vehicleMap.get(VEHICLE_6_ID)), List.of());
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod2() {
        Vehicle motorcycle = vehicleCreator.createMotorcycleWithId2();
        motorcycle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleService.update(motorcycle.getId(), motorcycle);
        RentDuration duration = durationCreator.createDuration2();

        Page<Vehicle> page = vehiclesRetriever.findAvailableVehiclesByCriteria(getEmptySearchRequest(duration), PAGEABLE);

        assertAvailableVehiclesCount(page.getNumberOfElements(), EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_2 - 1);
        assertAvailableVehicles(
                page.getContent(),
                List.of(vehicleMap.get(VEHICLE_3_ID), vehicleMap.get(VEHICLE_4_ID), vehicleMap.get(VEHICLE_5_ID), vehicleMap.get(VEHICLE_6_ID)),
                List.of(vehicleMap.get(VEHICLE_1_ID), vehicleMap.get(VEHICLE_2_ID)));
    }

    @Test
    void shouldFindSortedAvailableVehiclesInPeriod2() {
        Vehicle motorcycle = vehicleCreator.createMotorcycleWithId2();
        motorcycle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleService.update(motorcycle.getId(), motorcycle);
        RentDuration duration = durationCreator.createDuration2();

        AvailableVehiclesCriteriaSearchRequest searchRequest = brandAndProductionYearRequest(duration, "Honda", 2020);
        Page<Vehicle> page = vehiclesRetriever.findAvailableVehiclesByCriteria(searchRequest, PAGEABLE);

        assertAvailableVehiclesCount(page.getNumberOfElements(), NUMBER_OF_HONDA_CARS_PRODUCED_IN_2020);
        assertAvailableVehicles(page.getContent(),
                List.of(vehicleMap.get(VEHICLE_4_ID), vehicleMap.get(VEHICLE_6_ID)),
                List.of(vehicleMap.get(VEHICLE_1_ID), vehicleMap.get(VEHICLE_2_ID), vehicleMap.get(VEHICLE_3_ID), vehicleMap.get(VEHICLE_5_ID)));
    }

    @Test
    void shouldFindAvailableVehiclesInPeriod3() {
        RentDuration duration = durationCreator.createDuration3();

        Page<Vehicle> page = vehiclesRetriever.findAvailableVehiclesByCriteria(getEmptySearchRequest(duration), PAGEABLE);

        assertAvailableVehiclesCount(page.getNumberOfElements(), EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_3);
        assertAvailableVehicles(page.getContent(),
                List.of(vehicleMap.get(VEHICLE_1_ID), vehicleMap.get(VEHICLE_6_ID)),
                List.of(vehicleMap.get(VEHICLE_2_ID), vehicleMap.get(VEHICLE_3_ID), vehicleMap.get(VEHICLE_4_ID), vehicleMap.get(VEHICLE_5_ID)));
    }

    @Test
    void shouldFindSortedAvailableVehiclesInPeriod3() {
        RentDuration duration = durationCreator.createDuration3();

        AvailableVehiclesCriteriaSearchRequest searchRequest = brandAndProductionYearRequest(duration, "Seat", 2001);
        Page<Vehicle> page = vehiclesRetriever.findAvailableVehiclesByCriteria(searchRequest, PAGEABLE);

        assertAvailableVehiclesCount(page.getNumberOfElements(), NUMBER_OF_SEAT_CARS_PRODUCED_IN_2001);
        assertAvailableVehicles(page.getContent(),
                List.of(vehicleMap.get(VEHICLE_1_ID)),
                List.of(vehicleMap.get(VEHICLE_2_ID), vehicleMap.get(VEHICLE_3_ID), vehicleMap.get(VEHICLE_4_ID),
                        vehicleMap.get(VEHICLE_5_ID), vehicleMap.get(VEHICLE_6_ID)));
    }

    @Test
    void helperMethodShouldFindAvailableVehiclesInPeriod1() {
        RentDuration duration = durationCreator.createDuration1();

        Collection<Vehicle> vehicles = vehiclesRetriever.findAvailableVehiclesByRentDuration(duration);

        assertAvailableVehiclesCount(vehicles.size(), EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_1);
        assertAvailableVehicles(vehicles, List.of(vehicleMap.get(VEHICLE_6_ID)), List.of());
    }

    @Test
    void helperMethodShouldFindAvailableVehiclesInPeriod2() {
        Vehicle motorcycle = vehicleCreator.createMotorcycleWithId2();
        motorcycle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleService.update(motorcycle.getId(), motorcycle);
        RentDuration duration = durationCreator.createDuration2();

        Collection<Vehicle> vehicles = vehiclesRetriever.findAvailableVehiclesByRentDuration(duration);

        assertAvailableVehiclesCount(vehicles.size(), EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_2 - 1);
        assertAvailableVehicles(vehicles,
                List.of(vehicleMap.get(VEHICLE_3_ID), vehicleMap.get(VEHICLE_4_ID), vehicleMap.get(VEHICLE_5_ID), vehicleMap.get(VEHICLE_6_ID)),
                List.of(vehicleMap.get(VEHICLE_1_ID), vehicleMap.get(VEHICLE_2_ID)));
    }

    @Test
    void helperMethodShouldFindAvailableVehiclesInPeriod3() {
        RentDuration duration = durationCreator.createDuration3();

        Collection<Vehicle> vehicles = vehiclesRetriever.findAvailableVehiclesByRentDuration(duration);
        
        assertAvailableVehiclesCount(vehicles.size(), EXPECTED_NUMBER_OF_AVAILABLE_VEHICLES_FOR_DURATION_3);
        assertAvailableVehicles(vehicles,
                List.of(vehicleMap.get(VEHICLE_1_ID), vehicleMap.get(VEHICLE_6_ID)),
                List.of(vehicleMap.get(VEHICLE_2_ID), vehicleMap.get(VEHICLE_3_ID), vehicleMap.get(VEHICLE_4_ID), vehicleMap.get(VEHICLE_5_ID)));
    }

    private void assertAvailableVehiclesCount(int actualSize, int expectedCount) {
        assertEquals(expectedCount, actualSize);
    }

    private void assertAvailableVehicles(Collection<Vehicle> actualVehicles, List<Vehicle> expectedVehicles, List<Vehicle> unexpectedVehicles) {
        expectedVehicles.forEach(vehicle -> assertTrue(actualVehicles.contains(vehicle)));
        unexpectedVehicles.forEach(vehicle -> assertFalse(actualVehicles.contains(vehicle)));
    }

    private AvailableVehiclesCriteriaSearchRequest getEmptySearchRequest(RentDuration duration) {
        return new AvailableVehiclesCriteriaSearchRequest(duration, searchRequests.getEmptySearchRequest());
    }

    private AvailableVehiclesCriteriaSearchRequest brandAndProductionYearRequest(RentDuration duration, String brand, Integer year) {
        CriteriaSearchRequest request = searchRequests.getBrandAndProductionYearSearchRequest(brand, year);
        return new AvailableVehiclesCriteriaSearchRequest(duration, request);
    }
}
