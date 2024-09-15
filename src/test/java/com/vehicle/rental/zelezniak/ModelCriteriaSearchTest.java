package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.util.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle_domain.service.VehicleCriteriaSearch;
import com.vehicle.rental.zelezniak.vehicle_domain.service.VehicleService;
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
class ModelCriteriaSearchTest {

    private static Vehicle vehicleWithId5;
    private static final Pageable pageable = PageRequest.of(0, 5);

    @Autowired
    private VehicleCriteriaSearch criteriaSearch;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private VehicleCreator vehicleCreator;

    @BeforeEach
    void setUp() {
        databaseSetup.setupAllTables();
        vehicleWithId5 = vehicleCreator.createCarWithId5();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldFindVehiclesByCriteriaModel() {
        var info = vehicleWithId5.getVehicleInformation();
        var searchRequest = new CriteriaSearchRequest<>("model", info.getModel());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, pageable);
        List<Vehicle> vehicles = page.getContent();

        assertEquals(1, vehicles.size());
        assertTrue(vehicles.contains(vehicleWithId5));
    }
}
