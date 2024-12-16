package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
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

import static com.vehicle.rental.zelezniak.config.TestConstants.NUMBER_OF_VEHICLES_PRODUCED_IN_2020;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used for testing queries that contain multiple criteria.
 */
@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class VehicleCriteriaSearchTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 5);
    private static final String HONDA_CIVIC_REGISTRATION = "HON88888";
    private static final String HONDA_TYPE_R_REGISTRATION = "HDA24556";
    private static final Integer PRODUCTION_YEAR = 2020;
    private static final String SUZUKI_BRAND = "Suzuki";
    private static final String HONDA_BRAND = "Honda";
    private static final String HONDA_MODEL = "Civic Type R";
    private static final String BRAND_CRITERIA = "vehicleInformation.brand";
    private static final Integer NUMBER_OF_HONDA_CARS_PRODUCED_IN_2020 = 2;
    private static final Integer NUMBER_OF_HONDA_TYPE_R_CARS = 1;

    @Autowired
    private VehicleCriteriaSearchService criteriaSearch;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private VehicleRepository repository;
    @Autowired
    private CriteriaSearchRequests searchRequests;

    @BeforeEach
    void setUp() {
        databaseSetup.setupAllTables();
    }

    @Test
    void shouldSearchByBrandAndProductionYear() {
        CriteriaSearchRequest request = searchRequests.getBrandAndProductionYearSearchRequest(HONDA_BRAND, PRODUCTION_YEAR);

        Page<Vehicle> vehiclesByCriteria = criteriaSearch.findVehiclesByCriteria(request, PAGEABLE);

        List<Vehicle> vehicles = vehiclesByCriteria.getContent();

        assertEquals(NUMBER_OF_HONDA_CARS_PRODUCED_IN_2020, vehicles.size());
        Vehicle vehicle = repository.findByVehicleInformationRegistrationNumber(new RegistrationNumber(HONDA_CIVIC_REGISTRATION));
        assertTrue(vehicles.contains(vehicle));
    }

    @Test
    void shouldSearchByModelAndProductionYear() {
        CriteriaSearchRequest request = searchRequests.getModelAndProductionYearSearchRequest(HONDA_MODEL, PRODUCTION_YEAR);

        Page<Vehicle> vehiclesByCriteria = criteriaSearch.findVehiclesByCriteria(request, PAGEABLE);

        List<Vehicle> vehicles = vehiclesByCriteria.getContent();

        assertEquals(NUMBER_OF_HONDA_TYPE_R_CARS, vehicles.size());
        Vehicle vehicle = repository.findByVehicleInformationRegistrationNumber(new RegistrationNumber(HONDA_TYPE_R_REGISTRATION));
        assertTrue(vehicles.contains(vehicle));
    }

    @Test
    void shouldSearchByProductionYearAndSortByBrandAsc() {
        CriteriaSearchRequest request = searchRequests.getSearchByProductionYearAndSortByRequest(PRODUCTION_YEAR,BRAND_CRITERIA);

        Page<Vehicle> vehiclesByCriteria = criteriaSearch.findVehiclesByCriteria(request, PAGEABLE);

        List<Vehicle> vehicles = vehiclesByCriteria.getContent();

        assertEquals(NUMBER_OF_VEHICLES_PRODUCED_IN_2020, vehicles.size());
        assertEquals(HONDA_BRAND, vehicles.get(0).getBrand());
        assertEquals(HONDA_BRAND, vehicles.get(1).getBrand());
        assertEquals(SUZUKI_BRAND, vehicles.get(2).getBrand());
    }

    @Test
    void shouldSearchByProductionYearAndSortByBrandDesc() {
        CriteriaSearchRequest request = searchRequests.getSearchByProductionYearAndSortDescByRequest(PRODUCTION_YEAR,BRAND_CRITERIA);

        Page<Vehicle> vehiclesByCriteria = criteriaSearch.findVehiclesByCriteria(request, PAGEABLE);

        List<Vehicle> vehicles = vehiclesByCriteria.getContent();

        assertEquals(NUMBER_OF_VEHICLES_PRODUCED_IN_2020, vehicles.size());
        assertEquals(SUZUKI_BRAND, vehicles.get(0).getBrand());
        assertEquals(HONDA_BRAND, vehicles.get(1).getBrand());
        assertEquals(HONDA_BRAND, vehicles.get(2).getBrand());
    }
}
