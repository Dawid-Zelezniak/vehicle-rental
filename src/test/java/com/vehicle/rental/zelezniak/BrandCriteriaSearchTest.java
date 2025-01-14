package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class BrandCriteriaSearchTest {

    public static final int NUMBER_OF_TOYOTA_CARS = 1;
    public static final int NUMBER_OF_HONDA_CARS = 2;
    private static final Pageable PAGEABLE = PageRequest.of(0, NUMBER_OF_VEHICLES);

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleCriteriaSearchService criteriaSearch;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private CriteriaSearchRequests searchRequests;

    @BeforeEach
    void setUp() {
        databaseSetup.setupAllTables();
    }

    @Test
    void shouldFindVehiclesByToyotaBrand() {
        Vehicle vehicle = vehicleService.findById(VEHICLE_3_ID);
        var info = vehicle.getVehicleInformation();
        var searchRequest = searchRequests.getBrandSearchRequest(info.getBrand());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertTrue(vehicles.contains(vehicle));
        assertEquals(NUMBER_OF_TOYOTA_CARS, vehicles.size());
    }

    @Test
    void shouldFindVehiclesByHondaBrand() {
        Vehicle vehicle = vehicleService.findById(VEHICLE_6_ID);
        var info = vehicle.getVehicleInformation();
        var searchRequest = searchRequests.getBrandSearchRequest(info.getBrand());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertTrue(vehicles.contains(vehicle));
        assertEquals(NUMBER_OF_HONDA_CARS, vehicles.size());
    }
}
