package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import com.vehicle.rental.zelezniak.vehicle.service.criteria_search.VehicleCriteriaSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class VehicleStatusCriteriaSearchTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 5);

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleCriteriaSearchService criteriaSearch;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private CriteriaSearchRequests searchRequests;

    @BeforeEach
    void setUp() {
        databaseSetup.setupAllTables();
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusAvailable() {
        Vehicle unavailableVehicle = vehicleService.findById(VEHICLE_2_ID);
        unavailableVehicle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleRepository.save(unavailableVehicle);

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());
        var searchRequest = searchRequests.getStatusSearchRequest("available");

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertFalse(vehicles.contains(unavailableVehicle));
        assertEquals(NUMBER_OF_AVAILABLE_VEHICLES - 1, vehicles.size());
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusUnavailable() {
        Vehicle unavailableVehicle = vehicleService.findById(VEHICLE_2_ID);
        unavailableVehicle.setStatus(Vehicle.VehicleStatus.UNAVAILABLE);
        vehicleRepository.save(unavailableVehicle);
        var searchRequest = searchRequests.getStatusSearchRequest("unavailable");

        assertEquals(NUMBER_OF_VEHICLES, vehicleRepository.count());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertTrue(vehicles.contains(unavailableVehicle));
        assertEquals(NUMBER_OF_UNAVAILABLE_VEHICLES + 1, vehicles.size());
    }
}
