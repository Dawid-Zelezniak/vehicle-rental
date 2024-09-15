package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;
import com.vehicle.rental.zelezniak.reservation_domain.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation_domain.service.ReservationService;
import com.vehicle.rental.zelezniak.reservation_domain.service.ReservationValidator;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ReservationValidatorTest {

    private static Reservation reservationWithId5;
    private static final Pageable pageable = PageRequest.of(0, 5);

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

    Reservation reservation;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        reservationWithId5 = reservationCreator.createReservationWithId5();
    }

    @AfterEach
    void cleanupDatabase() {
        databaseSetup.dropAllTables();
        reservation = null;
    }

    @Test
    void shouldValidateSuccessfullyForReservationWithStatusNEW() {
        reservation = reservationCreator.buildNewReservation();
        Reservation saved = reservationRepository.save(reservation);
        addAvailableVehicleToReservation(reservation.getId());
        assertDoesNotThrow(() -> validator.validateReservationDataBeforePayment(saved.getId()));
    }

    @Test
    void shouldThrowExceptionWhenReservationStatusIsNotNEW() {
        Long id = reservationWithId5.getId();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> validator.validateReservationDataBeforePayment(id));
        assertEquals("Payments are unavailable for reservations with status other than NEW.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionIfVehiclesAreEmpty() {
        reservation = reservationCreator.buildNewReservation();
        Reservation saved = reservationRepository.save(reservation);
        Long id = saved.getId();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> validator.validateReservationDataBeforePayment(id));
        assertEquals("Reservation must contains at least one vehicle.", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenVehicleIsAlreadyReserved() {
        reservation = reservationCreator.buildNewReservation();
        Reservation saved = reservationRepository.save(reservation);
        Long id = saved.getId();
        addAvailableVehicleToReservation(id);
        addAlreadyReservedVehicleToReservation(id);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> validator.validateReservationDataBeforePayment(id));
        assertEquals("Someone already reserved vehicle that you tried to reserve.Pick vehicles one more time.", e.getMessage());
    }

    private void addAvailableVehicleToReservation(Long id) {
        Vehicle motorcycleWithId6 = vehicleCreator.createMotorcycleWithId6();
        reservationService.addVehicleToReservation(id, motorcycleWithId6.getId());
    }

    private void addAlreadyReservedVehicleToReservation(Long id) {
        Vehicle carWithId5 = vehicleCreator.createCarWithId5();
        reservationService.addVehicleToReservation(id, carWithId5.getId());
    }
}
