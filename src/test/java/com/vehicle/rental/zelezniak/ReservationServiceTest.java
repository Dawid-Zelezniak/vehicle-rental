package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.dto.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ReservationServiceTest {


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
    private VehicleCreator vehicleCreator;
    @Autowired
    private RentDurationCreator durationCreator;
    @Autowired
    private LocationCreator locationCreator;

    private ReservationCreationRequest creationRequest;
    private Vehicle vehicleWithId2;

    @BeforeEach
    void setupDatabase() throws IOException {
        creationRequest = new ReservationCreationRequest(CLIENT_2_ID, durationCreator.createDuration2());
        databaseSetup.setupAllTables();
        reservationWithId2 = reservationCreator.createReservationWithId2();
    }

    @Test
    void shouldFindAllReservations() {
        Page<Reservation> all = reservationService.findAll(PAGEABLE);
        List<Reservation> reservations = all.getContent();

        assertEquals(NUMBER_OF_RESERVATIONS, reservations.size());
        assertTrue(reservations.contains(reservationWithId2));
    }

    @Test
    void shouldFindAllReservationsByClientId() {
        List<Reservation> reservations = findReservationsByClientIdAndAssertSize(CLIENT_2_ID, NUMBER_OF_CLIENT_2_RESERVATIONS);

        assertEquals(NUMBER_OF_CLIENT_2_RESERVATIONS, reservations.size());
        assertTrue(reservations.contains(reservationWithId2));
    }

    @Test
    void shouldFindVehiclesByReservationId() {
        findVehiclesByReservationIdAssertSize(RESERVATION_3_ID, VEHICLES_IN_RESERVATION_3);
    }

    @Test
    void shouldReturnEmptyPageOfVehiclesWhenReservationDoesNotExist() {
        Long reservationId = 90L;
        int expectedSize = 0;

        findVehiclesByReservationIdAssertSize(reservationId, expectedSize);
    }

    @Test
    void shouldAddNewReservationForClientWhenDataCorrect() {
        findReservationsByClientIdAndAssertSize(CLIENT_2_ID, NUMBER_OF_CLIENT_2_RESERVATIONS);

        Reservation reservation = reservationService.addReservation(creationRequest);

        List<Reservation> reservations = findReservationsByClientIdAndAssertSize(CLIENT_2_ID, NUMBER_OF_CLIENT_2_RESERVATIONS + 1);
        assertTrue(reservations.contains(reservation));
    }

    @Test
    void shouldUpdateNewReservationLocationWhenDataCorrect() {
        Reservation reservation = reservationService.addReservation(creationRequest);
        RentInformation information = reservation.getRentInformation();
        RentInformation updatedLocation = information.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build();
        reservation.setRentInformation(updatedLocation);
        Long id = reservation.getId();

        Reservation updated = reservationService.updateLocationForNewReservation(id, updatedLocation);

        assertEquals(reservation, updated);
    }

    @Test
    void shouldNotUpdateLocationWhenStatusInvalid() {
        Reservation newData = reservationWithId2;
        RentInformation information = newData.getRentInformation();
        RentInformation updatedLocation = information.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build();
        newData.setRentInformation(updatedLocation);
        Long reservationToUpdateId = reservationWithId2.getId();

        IllegalArgumentException assertion = assertThrows(IllegalArgumentException.class,
                () -> reservationService.updateLocationForNewReservation(reservationToUpdateId, updatedLocation));
        assertEquals(assertion.getMessage(), "Can not update reservation with status: " + newData.getReservationStatus());
    }

    @Test
    void shouldUpdateDurationWhenStatusCorrect() {
        Long reservationToUpdateId = reservationWithId2.getId();
        setReservationStatusToNew(reservationWithId2);

        findVehiclesByReservationIdAssertSize(reservationToUpdateId, VEHICLES_IN_RESERVATION_2);

        Reservation updated = reservationService.updateDurationForNewReservation(reservationToUpdateId, durationCreator.createDuration3());

        findVehiclesByReservationIdAssertSize(reservationToUpdateId, VEHICLES_IN_RESERVATION_2 - 1);
        assertEquals(updated, findReservationById(reservationToUpdateId));
    }

    @Test
    void shouldNotUpdateDurationWhenReservationStatusInvalid() {
        Long reservationToUpdateId = reservationWithId2.getId();
        findVehiclesByReservationIdAssertSize(reservationToUpdateId, VEHICLES_IN_RESERVATION_2);

        RentDuration duration = durationCreator.createDuration3();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.updateDurationForNewReservation(reservationToUpdateId, duration));
        assertEquals("Can not update duration for reservation with status: " + reservationWithId2.getReservationStatus(), exception.getMessage());
    }

    @Test
    void shouldDeleteReservationWhenDataCorrect() {
        setReservationStatusToNew(reservationWithId2);

        findReservationsByClientIdAndAssertSize(CLIENT_2_ID, NUMBER_OF_CLIENT_2_RESERVATIONS);

        reservationService.deleteReservation(reservationWithId2.getId());

        findReservationsByClientIdAndAssertSize(CLIENT_2_ID, NUMBER_OF_CLIENT_2_RESERVATIONS - 1);
    }

    @Test
    void shouldNotDeleteReservation() {
        Long reservationToDeleteId = reservationWithId2.getId();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.deleteReservation(reservationToDeleteId));
        assertEquals("Can not remove reservation with status: " + reservationWithId2.getReservationStatus(), exception.getMessage());
    }

    @Test
    void shouldAddVehicleToReservationWhenDataCorrect() {
        Long reservationId = reservationWithId2.getId();
        setReservationStatusToNew(reservationWithId2);

        findVehiclesByReservationIdAssertSize(reservationId, VEHICLES_IN_RESERVATION_2);

        vehicleWithId2 = vehicleCreator.createMotorcycleWithId2();
        reservationService.addVehicleToNewReservation(reservationId, vehicleWithId2.getId());

        findVehiclesByReservationIdAssertSize(reservationId, VEHICLES_IN_RESERVATION_2 + 1);
    }

    @Test
    void shouldNotAddVehicleToReservation() {
        vehicleWithId2 = vehicleCreator.createMotorcycleWithId2();
        Long vehicleId = vehicleWithId2.getId();
        Long reservationId = reservationWithId2.getId();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.addVehicleToNewReservation(reservationId, vehicleId));
        assertEquals("Can not add vehicle to reservation with status: " + reservationWithId2.getReservationStatus(), exception.getMessage());
    }

    @Test
    @DisplayName("Don't add vehicle to reservation when it is already rented or reserved")
    void shouldThrowExceptionWhenVehicleIsAlreadyReserved() {
        Reservation reservation = reservationCreator.buildNewReservation();
        Reservation saved = reservationRepository.save(reservation);
        Long id = saved.getId();
        Vehicle car = vehicleCreator.createCarWithId1();
        Long carId = car.getId();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> reservationService.addVehicleToNewReservation(id, carId));
        assertEquals("Vehicle with id: " + carId + " is already rented or reserved.", e.getMessage());
    }

    @Test
    void shouldRemoveVehicleFromReservation() {
        Long vehicleId = VEHICLE_1_ID;
        setReservationStatusToNew(reservationWithId2);

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        reservationService.deleteVehicleFromNewReservation(reservationWithId2.getId(), vehicleId);

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2 - 1);
    }

    @Test
    void shouldNotRemoveVehicleFromReservation() {
        Long vehicleId = VEHICLE_1_ID;
        Long reservationId = reservationWithId2.getId();

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.deleteVehicleFromNewReservation(reservationId, vehicleId));
        assertEquals("Can not remove vehicle from reservation with status: " + reservationWithId2.getReservationStatus(), exception.getMessage());
    }

    @Test
    void shouldCalculateReservationTotalCost() {
        Money totalCost = reservationWithId2.getTotalCost();
        reservationWithId2.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationWithId2.setTotalCost(null);
        reservationWithId2.setDepositAmount(null);
        reservationRepository.save(reservationWithId2);

        Money cost = reservationService.calculateNewReservationCost(reservationWithId2.getId());

        assertEquals(totalCost, cost);
    }

    @Test
    void shouldNotCalculateTotalCost() {
        Long reservationId = reservationWithId2.getId();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reservationService.calculateNewReservationCost(reservationId));
        assertEquals("When calculating the total cost, the reservation status should be NEW", exception.getMessage());
    }

    private void setReservationStatusToNew(Reservation reservation) {
        reservation.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationRepository.save(reservation);
    }

    private List<Reservation> findReservationsByClientIdAndAssertSize(Long clientId, int expectedSize) {
        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(clientId, PAGEABLE);
        List<Reservation> content = page.getContent();
        assertEquals(expectedSize, content.size());
        return content;
    }

    private void findVehiclesByReservationIdAssertSize(Long reservationId, int expectedSize) {
        Page<Vehicle> page = reservationRepository.findVehiclesByReservationId(reservationId, PAGEABLE);
        List<Vehicle> content = page.getContent();
        assertEquals(expectedSize, content.size());
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
