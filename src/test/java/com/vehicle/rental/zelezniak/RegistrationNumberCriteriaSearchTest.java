package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user.model.client.Role;
import com.vehicle.rental.zelezniak.vehicle.exception.CriteriaAccessException;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleCriteriaSearch;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class RegistrationNumberCriteriaSearchTest {

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

    @Test
    @DisplayName("Admin can search vehicles by registration")
    void shouldFindVehiclesByCriteriaRegistrationNumber() {
        setSecurityContextHolder("ROLE_ADMIN");
        Vehicle vehicle4 = vehicleService.findById(4L);
        RegistrationNumber vehicle8RegistrationNumber = vehicle4.getRegistrationNumber();
        var searchRequest = new CriteriaSearchRequest<>("registration number", vehicle8RegistrationNumber);

        Page<Vehicle> page = criteriaSearch.findVehiclesByCriteria(searchRequest, PAGEABLE);
        List<Vehicle> vehicles = page.getContent();

        assertEquals(1, vehicles.size());
        assertTrue(vehicles.contains(vehicle4));
    }

    @Test
    @DisplayName("Client can't search vehicles by registration")
    void shouldNotFindVehiclesByCriteriaRegistrationNumber() {
        setSecurityContextHolder("ROLE_USER");
        Vehicle vehicle4 = vehicleService.findById(4L);
        RegistrationNumber vehicle8RegistrationNumber = vehicle4.getRegistrationNumber();
        var searchRequest = new CriteriaSearchRequest<>("registration number", vehicle8RegistrationNumber);

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
