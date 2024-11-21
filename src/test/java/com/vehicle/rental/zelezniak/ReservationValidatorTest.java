package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.reservation.service.validation.ReservationValidator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationValidatorTest {

    private static Reservation reservationWithId2;
    private static final int EXPECTED_NUMBER_OF_VEHICLES = 0;

    @Autowired
    private ReservationValidator validator;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ReservationCreator reservationCreator;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private VehicleCreator vehicleCreator;

    private Reservation reservation;

    @BeforeAll
    void setupDatabase() {
        databaseSetup.setupAllTables();
        reservationWithId2 = reservationCreator.createReservationWithId2();
    }

    @BeforeEach
    void initializeNewReservation() {
        reservation = reservationCreator.buildNewReservation();
    }

    @Test
    void shouldValidateSuccessfullyForReservationWithStatusNEW() {
        Reservation saved = reservationRepository.save(reservation);
        addAvailableVehicleToReservation(reservation.getId());
        assertDoesNotThrow(() -> validator.validateReservationDataBeforePayment(saved.getId()));
    }

    @Test
    void shouldThrowExceptionWhenReservationStatusIsNotNEW() {
        Long id = reservationWithId2.getId();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> validator.validateReservationDataBeforePayment(id));
        assertEquals("Payments are unavailable for reservations with status other than NEW.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionIfVehiclesAreEmpty() {
        Reservation saved = reservationRepository.save(reservation);
        Long id = saved.getId();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> validator.validateReservationDataBeforePayment(id));
        assertEquals("Reservation must contains at least one vehicle.", e.getMessage());
    }

    @Test
    @Transactional
    void shouldThrowExceptionWhenVehicleIsAlreadyReserved() {
        Reservation saved = reservationRepository.save(reservation);
        Long id = saved.getId();

        addAvailableVehicleToReservation(id);
        simulateSomeoneReservedAvailableVehicle();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> validator.validateReservationDataBeforePayment(id));
        assertEquals("Someone already reserved vehicle that you tried to reserve.Pick vehicles one more time.", e.getMessage());

        Collection<Vehicle> vehiclesByReservationId = reservationRepository.findVehiclesByReservationId(id);
        assertEquals(EXPECTED_NUMBER_OF_VEHICLES,vehiclesByReservationId.size());
    }

    private void addAvailableVehicleToReservation(Long id) {
        Vehicle vehicle = vehicleCreator.createMotorcycleWithId2();
        reservationService.addVehicleToNewReservation(id, vehicle.getId());
    }

    private void simulateSomeoneReservedAvailableVehicle() {
        Reservation reservation = reservationCreator.buildNewReservation();
        Reservation saved = reservationRepository.save(reservation);
        Vehicle vehicle = vehicleCreator.createMotorcycleWithId2();
        reservationService.addVehicleToNewReservation(saved.getId(), vehicle.getId());
        saved.setReservationStatus(Reservation.ReservationStatus.ACTIVE);
        reservationRepository.save(saved);
    }
}
