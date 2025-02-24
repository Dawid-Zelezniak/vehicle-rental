package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.CriteriaSearchRequests;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.user.Role;
import com.vehicle.rental.zelezniak.vehicle.exception.CriteriaAccessException;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import com.vehicle.rental.zelezniak.vehicle.service.criteria_search.VehicleCriteriaSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.vehicle.rental.zelezniak.config.TestConstants.VEHICLE_4_ID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class RegistrationNumberCriteriaSearchTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 5);

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
    @DisplayName("Admin can search vehicles by registration")
    void shouldFindVehiclesByCriteriaRegistrationNumber() {
        setSecurityContextHolder("ROLE_ADMIN");
        Vehicle vehicle = vehicleService.findById(VEHICLE_4_ID);
        RegistrationNumber registrationNumber = vehicle.getRegistrationNumber();
        var searchRequest = searchRequests.getRegistrationSearchRequest(registrationNumber.getRegistration());

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        int numberOfVehiclesWithSuchRegistration = 1;
        assertEquals(numberOfVehiclesWithSuchRegistration, vehicles.size());
        assertTrue(vehicles.contains(vehicle));
    }

    @Test
    @DisplayName("User can't search vehicles by registration")
    void shouldNotFindVehiclesByCriteriaRegistrationNumber() {
        setSecurityContextHolder("ROLE_USER");
        Vehicle vehicle = vehicleService.findById(VEHICLE_4_ID);
        RegistrationNumber registrationNumber = vehicle.getRegistrationNumber();
        var searchRequest = searchRequests.getRegistrationSearchRequest(registrationNumber.getRegistration());

        assertThrows(CriteriaAccessException.class,
                () -> criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE));
    }

    private void setSecurityContextHolder(String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Set<Role> authorities = new HashSet<>();
        authorities.add(new Role(role));
        Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password", authorities);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
