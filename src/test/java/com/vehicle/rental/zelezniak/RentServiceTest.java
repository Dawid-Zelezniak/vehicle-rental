package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.RentCreator;
import com.vehicle.rental.zelezniak.config.RentDurationCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.rent.model.Rent;
import com.vehicle.rental.zelezniak.rent.repository.RentRepository;
import com.vehicle.rental.zelezniak.rent.service.RentService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
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

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class RentServiceTest {

    public static final int EXPECTED_NUMBER_OF_UNAVAILABLE_VEHICLES_FOR_DURATION_1 = 4;
    private static final Pageable PAGEABLE = PageRequest.of(0, NUMBER_OF_RENTS);

    private static Rent rentWithId1;

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
        rentWithId1 = rentCreator.createRentWithId1();
    }

    @Test
    void shouldReturnAllRents() {
        Page<Rent> page = rentService.findAll(PAGEABLE);
        List<Rent> rents = page.getContent();

        assertEquals(NUMBER_OF_RENTS, rents.size());
        rents.forEach(Assertions::assertNotNull);
        assertTrue(rents.contains(rentWithId1));
    }

    @Test
    void shouldFindRentById() {
        Rent byId = rentService.findById(rentWithId1.getId());

        assertEquals(rentWithId1, byId);
    }

    @Test
    void shouldFindAllClientRentsByClientId() {
        Long clientId = CLIENT_2_ID;

        Page<Rent> page = rentService.findAllByClientId(clientId, PAGEABLE);
        List<Rent> allByClient5Id = page.getContent();

        assertTrue(allByClient5Id.contains(rentWithId1));
    }

    @Test
    void shouldFindVehiclesByIdForRent1() {
        Long rentId = RENT_1_ID;

        List<Vehicle> vehicles = findVehiclesByRentIdAndAssertSize(rentId, VEHICLES_IN_RENT_1);

        assertEquals(VEHICLES_IN_RENT_1, vehicles.size());
        assertTrue(vehicles.contains(vehicleCreator.createCarWithId1()));
    }

    @Test
    void shouldFindVehiclesByIdForRent2() {
        Long rentId = RENT_2_ID;

        List<Vehicle> vehicles = findVehiclesByRentIdAndAssertSize(rentId, VEHICLES_IN_RENT_2);

        assertEquals(VEHICLES_IN_RENT_2, vehicles.size());
        assertTrue(vehicles.contains(vehicleCreator.createMotorcycleWithId2()));
    }

    /**
     * testing method from repository,
     * this method is not used in service
     */
    @Test
    @Transactional
    void shouldFindUnavailableVehicleIdsForRentInPeriod() {
        RentDuration duration = durationCreator.createDuration1();

        Collection<Long> unavailableIds = rentRepository.findUnavailableVehicleIdsForRentInPeriod(
                duration.getRentalStart(), duration.getRentalEnd());

        assertEquals(EXPECTED_NUMBER_OF_UNAVAILABLE_VEHICLES_FOR_DURATION_1, unavailableIds.size());
        assertTrue(unavailableIds.contains(VEHICLE_2_ID));
        assertTrue(unavailableIds.contains(VEHICLE_3_ID));
        assertTrue(unavailableIds.contains(VEHICLE_4_ID));
        assertTrue(unavailableIds.contains(VEHICLE_5_ID));
    }

    private List<Vehicle> findVehiclesByRentIdAndAssertSize(Long rentId, int expectedSize) {
        Page<Vehicle> p1 = rentService.findVehiclesByRentId(rentId, PAGEABLE);
        List<Vehicle> vehicles = p1.getContent();
        assertEquals(expectedSize, vehicles.size());
        return vehicles;
    }
}
