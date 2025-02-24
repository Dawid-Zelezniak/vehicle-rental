package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.user.model.user.User;
import com.vehicle.rental.zelezniak.user.service.AdminInitializationService;
import com.vehicle.rental.zelezniak.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class AdminInitializationServiceTest {

    @Autowired
    private AdminInitializationService initializationService;
    @Autowired
    private UserService clientService;

    @Value("${admin.email}")
    private String email;

    @Test
    void shouldFindAdminAfterLoadOfContext() {
        initializationService.createAdmin();
        User byId = clientService.findByEmail(email);
        assertNotNull(byId);
        assertEquals(email, byId.getEmail());
    }
}
