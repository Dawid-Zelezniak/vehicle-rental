package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.dto.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.NewReservationService;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class NewReservationServiceTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, NUMBER_OF_RESERVATIONS);
    private static Reservation reservationWithId2;

    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ReservationCreator reservationCreator;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private NewReservationService newReservationService;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private RentDurationCreator durationCreator;
    @Autowired
    private LocationCreator locationCreator;

    private ReservationCreationRequest creationRequest;

    @BeforeEach
    void setupDatabase() throws IOException {
        databaseSetup.setupAllTables();
        creationRequest = new ReservationCreationRequest(CLIENT_2_ID, durationCreator.createDuration2());
        reservationWithId2 = reservationCreator.createReservationWithId2();
    }

    @Test
    void shouldAddReservationForClientWhenDataCorrect() {
        Long clientId = CLIENT_2_ID;

        findReservationsByClientIdAndAssertSize(clientId, NUMBER_OF_CLIENT_2_RESERVATIONS);

        Reservation reservation = newReservationService.addNewReservation(creationRequest);
        Long reservationId = reservation.getId();

        findReservationsByClientIdAndAssertSize(clientId, NUMBER_OF_CLIENT_2_RESERVATIONS + 1);
        assertEquals(reservation, findReservationById(reservationId));
    }

    @Test
    void shouldUpdateNewReservationLocationWhenStatusCorrect() {
        Reservation reservation = newReservationService.addNewReservation(creationRequest);
        RentInformation rentInformation = reservation.getRentInformation();
        RentInformation updatedLocation = rentInformation.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build();
        reservation.setRentInformation(updatedLocation);

        Reservation updated = newReservationService.updateLocationForReservation(reservation, updatedLocation);
        assertEquals(reservation, updated);
    }

    @Test
    void shouldNotUpdateLocationWhenStatusIncorrect() {
        Reservation reservation = reservationWithId2;
        RentInformation information = reservation.getRentInformation();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> newReservationService.updateLocationForReservation(reservation, information));
        assertEquals("Can not update reservation with status: " + reservation.getReservationStatus(),
                exception.getMessage());
    }

    @Test
    void shouldUpdateDurationWhenStatusCorrect() {
        Long reservationId = reservationWithId2.getId();
        setReservationStatusToNew(reservationWithId2);

        findVehiclesByReservationIdAssertSize(reservationId, VEHICLES_IN_RESERVATION_2);

        Reservation updated = newReservationService.updateDurationForReservation(reservationWithId2, durationCreator.createDuration3());

        assertEquals(updated, findReservationById(reservationId));
    }

    @Test
    void shouldNotUpdateDurationWhenStatusIncorrect() {
        Long reservationId = reservationWithId2.getId();
        findVehiclesByReservationIdAssertSize(reservationId, VEHICLES_IN_RESERVATION_2);

        RentDuration duration = durationCreator.createDuration3();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> newReservationService.updateDurationForReservation(reservationWithId2, duration));
        assertEquals("Can not update duration for reservation with status: " + reservationWithId2.getReservationStatus(),
                exception.getMessage());
    }

    @Test
    void shouldDeleteReservationWhenStatusCorrect() {
        Long clientId = CLIENT_2_ID;
        setReservationStatusToNew(reservationWithId2);

        findReservationsByClientIdAndAssertSize(clientId, NUMBER_OF_CLIENT_2_RESERVATIONS);

        newReservationService.deleteReservation(reservationWithId2);

        findReservationsByClientIdAndAssertSize(clientId, NUMBER_OF_CLIENT_2_RESERVATIONS - 1);
    }

    @Test
    void shouldNotDeleteReservationWhenStatusIncorrect() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> newReservationService.deleteReservation(reservationWithId2));
        assertEquals("Can not remove reservation with status: " + reservationWithId2.getReservationStatus(),
                exception.getMessage());
    }

    @Test
    void shouldAddVehicleToNewReservation() {
        Long reservationId = reservationWithId2.getId();
        setReservationStatusToNew(reservationWithId2);

        findVehiclesByReservationIdAssertSize(reservationId, VEHICLES_IN_RESERVATION_2);

        Vehicle vehicle = vehicleCreator.createMotorcycleWithId2();
        newReservationService.addVehicleToReservation(reservationWithId2, vehicle.getId());

        findVehiclesByReservationIdAssertSize(reservationId, VEHICLES_IN_RESERVATION_2 + 1);
    }

    @Test
    void shouldNotAddVehicleToReservationWithIncorrectStatus() {
        Vehicle vehicle = vehicleCreator.createMotorcycleWithId2();
        Long vehicleId = vehicle.getId();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> newReservationService.addVehicleToReservation(reservationWithId2, vehicleId));
        assertEquals("Can not add vehicle to reservation with status: " + reservationWithId2.getReservationStatus(),
                exception.getMessage());
    }

    @Test
    void shouldRemoveVehicleFromReservationWhenReservationStatusCorrect() {
        setReservationStatusToNew(reservationWithId2);

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        newReservationService.deleteVehicleFromReservation(reservationWithId2, VEHICLE_1_ID);

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2 - 1);
    }

    @Test
    void shouldNotRemoveVehicleFromReservationWhenReservationStatusIncorrect() {
        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> newReservationService.deleteVehicleFromReservation(reservationWithId2, VEHICLE_1_ID));
        assertEquals("Can not remove vehicle from reservation with status: " + reservationWithId2.getReservationStatus(),
                exception.getMessage());
    }

    @Test
    void shouldCalculateTotalCostWhenReservationStatusIsCorrect() {
        Money totalCost = reservationWithId2.getTotalCost();
        reservationWithId2.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationWithId2.setTotalCost(null);
        reservationWithId2.setDepositAmount(null);
        reservationRepository.save(reservationWithId2);

        Money cost = newReservationService.calculateCost(reservationWithId2);

        assertEquals(totalCost, cost);
    }

    @Test
    void shouldNotCalculateTotalCostWhenReservationStatusIncorrect() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> newReservationService.calculateCost(reservationWithId2));
        assertEquals("When calculating the total cost, the reservation status should be NEW",
                exception.getMessage());
    }

    private void findReservationsByClientIdAndAssertSize(Long clientId, int expectedSize) {
        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(clientId, PAGEABLE);
        List<Reservation> content = page.getContent();
        assertEquals(expectedSize, content.size());
    }

    private void findVehiclesByReservationIdAssertSize(Long reservationId, int expectedSize) {
        Page<Vehicle> page = reservationRepository.findVehiclesByReservationId(reservationId, PAGEABLE);
        List<Vehicle> content = page.getContent();
        assertEquals(expectedSize, content.size());
    }

    private void setReservationStatusToNew(Reservation reservation) {
        reservation.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationRepository.save(reservation);
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
