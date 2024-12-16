package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.criteria_search.VehicleCriteriaSearchService;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
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
class ProductionYearCriteriaSearchTest {

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
    void shouldFindVehiclesByCriteriaProductionYear() {
        Vehicle vehicle4 = vehicleService.findById(VEHICLE_4_ID);
        Vehicle vehicle5 = vehicleService.findById(VEHICLE_5_ID);
        var info = vehicle4.getVehicleInformation();
        var searchRequest = searchRequests.getProductionYearSearchRequest(info.getProductionYear().getYear());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertEquals(NUMBER_OF_VEHICLES_PRODUCED_IN_2020, vehicles.size());
        assertTrue(vehicles.contains(vehicle4));
        assertTrue(vehicles.contains(vehicle5));
    }
}
