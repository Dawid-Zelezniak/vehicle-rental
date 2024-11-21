package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleCriteriaSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static com.vehicle.rental.zelezniak.config.TestConstants.EXPECTED_NUMBER_OF_VEHICLES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ModelCriteriaSearchTest {

    public static final int NUMBER_OF_SEAT_CARS = 1;
    private static final Pageable PAGEABLE = PageRequest.of(0, EXPECTED_NUMBER_OF_VEHICLES);

    private static Vehicle vehicleWithId1;

    @Autowired
    private VehicleCriteriaSearch criteriaSearch;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private VehicleCreator vehicleCreator;

    @BeforeEach
    void setUp() {
        databaseSetup.setupAllTables();
        vehicleWithId1 = vehicleCreator.createCarWithId1();
    }

    @Test
    void shouldFindVehiclesByCriteriaModel() {
        var info = vehicleWithId1.getVehicleInformation();
        var searchRequest = new CriteriaSearchRequest<>("model", info.getModel());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertEquals(NUMBER_OF_SEAT_CARS, vehicles.size());
        assertTrue(vehicles.contains(vehicleWithId1));
    }
}
