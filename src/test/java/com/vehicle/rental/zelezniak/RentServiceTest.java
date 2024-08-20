package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.rent_domain.model.Rent;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.rent_domain.repository.RentRepository;
import com.vehicle.rental.zelezniak.rent_domain.service.RentService;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class RentServiceTest {

    private static Rent rentWithId5;
    private static final Pageable pageable = PageRequest.of(0, 5);

    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private RentCreator rentCreator;
    @Autowired
    private RentService rentService;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private RentRepository rentRepository;
    @Autowired
    private RentDurationCreator durationCreator;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        rentWithId5 = rentCreator.createRentWithId5();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldReturnAllRents() {
        Page<Rent> page =  rentService.findAll(pageable);
        List<Rent> rents = page.getContent();

        assertEquals(3, rents.size());
        rents.forEach(Assertions::assertNotNull);
        assertTrue(rents.contains(rentWithId5));
    }

    @Test
    void shouldFindRentById() {
        Rent byId = rentService.findById(rentWithId5.getId());

        assertEquals(rentWithId5, byId);
    }

    @Test
    void shouldFindAllClientRentsByClientId() {
        Long client5Id = 5L;

        Page<Rent> page = rentService.findAllByClientId(client5Id,pageable);
        List<Rent> allByClient5Id = page.getContent();

        assertTrue(allByClient5Id.contains(rentWithId5));
    }

    @Test
    void shouldFindVehiclesByRentId(){
        Long rent5Id = 5L;
        Long rent6Id = 6L;

        Page<Vehicle> p1 = rentService.findVehiclesByRentId(rent5Id,pageable);
        List<Vehicle> vehiclesByRent5Id = p1.getContent();

        Page<Vehicle> p2 = rentService.findVehiclesByRentId(rent6Id,pageable);
        List<Vehicle> vehiclesByRent6Id = p2.getContent();

        assertEquals(vehicleCreator.createCarWithId5(), vehiclesByRent5Id.get(0));

        assertEquals(vehicleCreator.createMotorcycleWithId6(), vehiclesByRent6Id.get(0));
        assertEquals(2,vehiclesByRent6Id.size());
    }

    @Test
    void shouldFindUnavailableVehicleIdsForRentInPeriod(){
        RentDuration duration = durationCreator.createDuration1();

        Collection<Long> unavailableIds = rentRepository.findUnavailableVehicleIdsForRentInPeriod(
                duration.getRentalStart(), duration.getRentalEnd());

        assertEquals(4,unavailableIds.size());
        assertTrue(unavailableIds.contains(6L));
        assertTrue(unavailableIds.contains(7L));
        assertTrue(unavailableIds.contains(8L));
        assertTrue(unavailableIds.contains(9L));
    }
}
