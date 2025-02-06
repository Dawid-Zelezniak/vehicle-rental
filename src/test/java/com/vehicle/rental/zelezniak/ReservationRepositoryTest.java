package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.ReservationCreator;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private DatabaseSetup setup;
    @Autowired
    private ReservationCreator reservationCreator;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setupData() {
        setup.setupAllTables();
    }

    @Test
    @Transactional
    void shouldUpdateReservationStatusToActive() {
        Reservation reservation = reservationCreator.buildNewReservation();
        Reservation saved = reservationRepository.save(reservation);
        Long id = saved.getId();

        assertTrue(saved.hasStatus(Reservation.ReservationStatus.NEW));

        reservationRepository.changeReservationStatusToActive(id);
        em.flush();
        em.clear();

        Optional<Reservation> byId = reservationRepository.findById(id);
        assertTrue(byId.isPresent());
        Reservation afterUpdate = byId.get();
        assertTrue(afterUpdate.hasStatus(Reservation.ReservationStatus.ACTIVE));
    }
}
