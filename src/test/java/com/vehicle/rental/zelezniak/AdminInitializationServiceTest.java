package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.user_domain.model.client.Client;
import com.vehicle.rental.zelezniak.user_domain.service.AdminInitializationService;
import com.vehicle.rental.zelezniak.user_domain.service.ClientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    private ClientService clientService;
    @Autowired
    private DatabaseSetup setup;

    @Value("${admin.email}")
    private String email;

    @BeforeEach
    void setupDatabase() {
        setup.setupAllTables();
    }

    @AfterEach
    void cleanupDatabase() {
        setup.dropAllTables();
    }

    @Test
    void shouldFindAdminAfterLoadOfContext() {
        initializationService.createAdmin();
        Long adminId = 1L;
        Client byId = clientService.findById(adminId);
        assertNotNull(byId);
        assertEquals(email, byId.getEmail());
    }
}