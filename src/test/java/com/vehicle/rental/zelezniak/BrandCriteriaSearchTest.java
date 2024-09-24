package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.model.util.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleCriteriaSearch;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class BrandCriteriaSearchTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 5);

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleCriteriaSearch criteriaSearch;
    @Autowired
    private DatabaseSetup databaseSetup;

    @BeforeEach
    void setUp() {
        databaseSetup.setupAllTables();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldFindVehiclesByCriteriaBrand() {
        Vehicle vehicle7 = vehicleService.findById(7L);
        var info = vehicle7.getVehicleInformation();
        var searchRequest = new CriteriaSearchRequest<>("brand", info.getBrand());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertTrue(vehicles.contains(vehicle7));
        assertEquals(1, vehicles.size());
    }
}
