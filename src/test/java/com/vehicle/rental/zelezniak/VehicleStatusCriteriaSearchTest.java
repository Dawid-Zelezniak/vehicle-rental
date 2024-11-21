package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleCriteriaSearch;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class VehicleStatusCriteriaSearchTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 5);

    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleCriteriaSearch criteriaSearch;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DatabaseSetup databaseSetup;

    @BeforeEach
    void setUp() {
        databaseSetup.setupAllTables();
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusAvailable() {
        Vehicle unavailableVehicle = vehicleService.findById(VEHICLE_2_ID);
        unavailableVehicle.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleRepository.save(unavailableVehicle);

        assertEquals(EXPECTED_NUMBER_OF_VEHICLES, vehicleRepository.count());
        var searchRequest = new CriteriaSearchRequest<>("status", "available");

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertFalse(vehicles.contains(unavailableVehicle));
        assertEquals(NUMBER_OF_AVAILABLE_VEHICLES - 1, vehicles.size());
    }

    @Test
    void shouldFindVehiclesByCriteriaStatusUnavailable() {
        Vehicle unavailableVehicle = vehicleService.findById(VEHICLE_2_ID);
        unavailableVehicle.setStatus(Vehicle.Status.UNAVAILABLE);
        vehicleRepository.save(unavailableVehicle);
        var searchRequest = new CriteriaSearchRequest<>("status", "unavailable");

        assertEquals(EXPECTED_NUMBER_OF_VEHICLES, vehicleRepository.count());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertTrue(vehicles.contains(unavailableVehicle));
        assertEquals(NUMBER_OF_UNAVAILABLE_VEHICLES + 1, vehicles.size());
    }
}
