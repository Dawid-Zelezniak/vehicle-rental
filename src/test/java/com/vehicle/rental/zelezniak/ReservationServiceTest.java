package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.util.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ReservationServiceTest {

    private static Reservation reservationWithId5;
    private static final Pageable PAGEABLE = PageRequest.of(0, 5);

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
    private Vehicle vehicleWithId6;

    @BeforeEach
    void setupDatabase() throws IOException {
        creationRequest = new ReservationCreationRequest(5L, durationCreator.createDuration2());
        databaseSetup.setupAllTables();
        reservationWithId5 = reservationCreator.createReservationWithId5();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldFindAllReservations() {
        Page<Reservation> all = reservationService.findAll(PAGEABLE);
        List<Reservation> reservations = all.getContent();
        assertTrue(reservations.contains(reservationWithId5));
        assertEquals(5, reservations.size());
    }

    @Test
    void shouldFindAllReservationsByClientId() {
        Long clientId = 5L;

        Page<Reservation> page = reservationService.findAllByClientId(clientId, PAGEABLE);
        List<Reservation> reservations = page.getContent();

        assertEquals(2, reservations.size());
        assertTrue(reservations.contains(reservationWithId5));
    }

    @Test
    void shouldAddNewReservationForClient() {
        Long client5Id = 5L;

        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        List<Reservation> allByClientId = page.getContent();
        assertEquals(2, allByClientId.size());

        Reservation reservation = reservationService.addReservation(creationRequest);

        page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        allByClientId = page.getContent();
        assertEquals(3, allByClientId.size());
        assertTrue(allByClientId.contains(reservation));
    }

    @Test
    void shouldUpdateNewReservationLocation() {
        Reservation reservation = reservationService.addReservation(creationRequest);
        RentInformation information = reservation.getRentInformation();
        reservation.setRentInformation(information.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build());
        Long id = reservation.getId();

        Reservation updated = reservationService.updateLocation(id, reservation);

        assertEquals(reservation, updated);
    }

    @Test
    void shouldUpdateLocation() {
        setReservationStatusToNew(reservationWithId5);
        Long reservation5Id = reservationWithId5.getId();
        RentInformation information = reservationWithId5.getRentInformation();
        reservationWithId5.setRentInformation(information.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build());

        Reservation updated = reservationService.updateLocation(reservation5Id, reservationWithId5);

        assertEquals(reservationWithId5, updated);
    }

    @Test
    void shouldNotUpdateLocation() {
        Reservation newData = reservationWithId5;
        RentInformation information = newData.getRentInformation();
        newData.setRentInformation(information.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build());
        Long reservation5Id = reservationWithId5.getId();

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.updateLocation(reservation5Id, newData));
    }

    @Test
    void shouldUpdateDuration() {
        Long reservationId = reservationWithId5.getId();
        setReservationStatusToNew(reservationWithId5);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(1, vehicles.size());

        Reservation updated = reservationService.updateDuration(reservationId, durationCreator.createDuration3());

        vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(0, vehicles.size());
        assertEquals(updated, findReservationById(reservationId));
    }

    @Test
    void shouldNotUpdateDuration() {
        Long reservationId = reservationWithId5.getId();
        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationId);

        assertEquals(1, vehicles.size());
        RentDuration duration = durationCreator.createDuration3();

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.updateDuration(reservationId, duration));
    }

    @Test
    void shouldDeleteReservation() {
        Long client5Id = 5L;
        setReservationStatusToNew(reservationWithId5);

        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        List<Reservation> allByClientId = page.getContent();
        assertEquals(2, allByClientId.size());

        reservationService.deleteReservation(reservationWithId5.getId());

        page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        allByClientId = page.getContent();
        assertEquals(1, allByClientId.size());
    }

    @Test
    void shouldNotDeleteReservation() {
        Long id = reservationWithId5.getId();
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.deleteReservation(id));
    }

    @Test
    void shouldAddVehicleToReservation() {
        Long reservationId = reservationWithId5.getId();
        setReservationStatusToNew(reservationWithId5);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(1, vehicles.size());

        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
        reservationService.addVehicleToReservation(reservationId, vehicleWithId6.getId());

        vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(2, vehicles.size());
    }

    @Test
    void shouldNotAddVehicleToReservation() {
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
        Long vehicle6Id = vehicleWithId6.getId();
        Long reservation5Id = reservationWithId5.getId();

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.addVehicleToReservation(reservation5Id, vehicle6Id));
    }

    @Test
    void shouldRemoveVehicleFromReservation() {
        Long vehicle5Id = 5L;
        setReservationStatusToNew(reservationWithId5);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(1, vehicles.size());

        reservationService.deleteVehicleFromReservation(reservationWithId5.getId(), vehicle5Id);

        vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(0, vehicles.size());
    }

    @Test
    void shouldNotRemoveVehicleFromReservation() {
        Long vehicle5Id = 5L;
        Long reservation5Id = reservationWithId5.getId();

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(1, vehicles.size());

        assertThrows(IllegalArgumentException.class,
                () -> reservationService.deleteVehicleFromReservation(reservation5Id, vehicle5Id));
    }

    @Test
    void shouldCalculateTotalCost() {
        Money totalCost = reservationWithId5.getTotalCost();
        reservationWithId5.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationWithId5.setTotalCost(null);
        reservationWithId5.setDepositAmount(null);
        reservationRepository.save(reservationWithId5);

        Money cost = reservationService.calculateCost(reservationWithId5.getId());

        assertEquals(totalCost, cost);
    }

    @Test
    void shouldNotCalculateTotalCost() {
        Long id = reservationWithId5.getId();
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.calculateCost(id));
    }

    private void setReservationStatusToNew(Reservation reservation) {
        reservation.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationRepository.save(reservation);
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
