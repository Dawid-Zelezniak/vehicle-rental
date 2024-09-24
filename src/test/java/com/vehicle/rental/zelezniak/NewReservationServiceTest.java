package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;
import com.vehicle.rental.zelezniak.reservation_domain.model.util.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation_domain.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation_domain.service.NewReservationService;
import com.vehicle.rental.zelezniak.reservation_domain.service.ReservationService;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class NewReservationServiceTest {

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
    private NewReservationService newReservationService;
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
        databaseSetup.setupAllTables();
        creationRequest = new ReservationCreationRequest(5L, durationCreator.createDuration2());
        reservationWithId5 = reservationCreator.createReservationWithId5();
    }

    @AfterEach
    void cleanupDatabase(){
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldAddReservationForClient() {
        Long client5Id = 5L;

        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        Collection<Reservation> allByClientId = page.getContent();
        assertEquals(2, allByClientId.size());

        Reservation reservation = newReservationService.addNewReservation(creationRequest);
        Long reservationId = reservation.getId();

        page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        allByClientId = page.getContent();
        assertEquals(3, allByClientId.size());
        assertEquals(reservation, findReservationById(reservationId));
    }

    @Test
    void shouldUpdateNewReservationLocation() {
        Reservation reservation = newReservationService.addNewReservation(creationRequest);
        RentInformation rentInformation = reservation.getRentInformation();
        reservation.setRentInformation(rentInformation.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build());
        Reservation fromDb = findReservationById(reservation.getId());

        Reservation updated = newReservationService.updateLocationForReservation(fromDb, reservation);

        assertEquals(reservation, updated);
    }

    @Test
    void shouldUpdateLocation() {
        setReservationStatusToNew(reservationWithId5);
        Reservation fromDb = findReservationById(reservationWithId5.getId());
        RentInformation information = reservationWithId5.getRentInformation();
        reservationWithId5.setRentInformation(information.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build());

        Reservation updated = newReservationService.updateLocationForReservation(fromDb, reservationWithId5);

        assertEquals(reservationWithId5, updated);
    }

    @Test
    void shouldNotUpdateLocation() {
        Reservation newData = reservationWithId5;
        RentInformation information = newData.getRentInformation();
        newData.setRentInformation(information.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build());

        assertThrows(IllegalArgumentException.class,
                () -> newReservationService.updateLocationForReservation(reservationWithId5, newData));
    }

    @Test
    void shouldUpdateDuration() {
        Long reservationId = reservationWithId5.getId();
        setReservationStatusToNew(reservationWithId5);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(1, vehicles.size());

        Reservation updated = newReservationService.updateDurationForReservation(reservationWithId5, durationCreator.createDuration3());

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
                () -> newReservationService.updateDurationForReservation(reservationWithId5, duration));
    }

    @Test
    void shouldDeleteReservation() {
        Long client5Id = 5L;
        setReservationStatusToNew(reservationWithId5);

        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        List<Reservation> reservations = page.getContent();
        assertEquals(2, reservations.size());

        newReservationService.deleteReservation(reservationWithId5);

        page = reservationRepository.findAllReservationsByClientId(client5Id, PAGEABLE);
        reservations = page.getContent();
        assertEquals(1, reservations.size());
    }

    @Test
    void shouldNotDeleteReservation() {
        assertThrows(IllegalArgumentException.class,
                () -> newReservationService.deleteReservation(reservationWithId5));
    }

    @Test
    void shouldAddVehicleToReservation() {
        Long reservationId = reservationWithId5.getId();
        setReservationStatusToNew(reservationWithId5);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(1, vehicles.size());

        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
        newReservationService.addVehicleToReservation(reservationWithId5, vehicleWithId6.getId());

        vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(2, vehicles.size());
    }

    @Test
    void shouldNotAddVehicleToReservation() {
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
        Long vehicle6Id = vehicleWithId6.getId();

        assertThrows(IllegalArgumentException.class,
                () -> newReservationService.addVehicleToReservation(reservationWithId5, vehicle6Id));
    }

    @Test
    void shouldRemoveVehicleFromReservation() {
        Long vehicle5Id = 5L;
        setReservationStatusToNew(reservationWithId5);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(1, vehicles.size());

        newReservationService.deleteVehicleFromReservation(reservationWithId5, vehicle5Id);

        vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(0, vehicles.size());
    }

    @Test
    void shouldNotRemoveVehicleFromReservation() {
        Long vehicle5Id = 5L;

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(1, vehicles.size());

        assertThrows(IllegalArgumentException.class,
                () -> newReservationService.deleteVehicleFromReservation(reservationWithId5, vehicle5Id));
    }

    @Test
    void shouldCalculateTotalCost() {
        Money totalCost = reservationWithId5.getTotalCost();
        reservationWithId5.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationWithId5.setTotalCost(null);
        reservationWithId5.setDepositAmount(null);
        reservationRepository.save(reservationWithId5);

        Money cost = newReservationService.calculateCost(reservationWithId5);

        assertEquals(totalCost, cost);
    }

    @Test
    void shouldNotCalculateTotalCost() {
        assertThrows(IllegalArgumentException.class,
                () -> newReservationService.calculateCost(reservationWithId5));
    }

    private void setReservationStatusToNew(Reservation reservation) {
        reservation.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationRepository.save(reservation);
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
