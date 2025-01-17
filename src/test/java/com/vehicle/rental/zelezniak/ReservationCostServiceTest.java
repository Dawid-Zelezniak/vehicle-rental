package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.ReservationCreator;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.calculations.ReservationCostService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
class ReservationCostServiceTest {

    private static Reservation reservationWithId2;

    @Autowired
    private DatabaseSetup setup;
    @Autowired
    private ReservationCreator reservationCreator;
    @Autowired
    private ReservationCostService costService;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void initializeTestData() {
        setup.setupAllTables();
        reservationWithId2 = reservationCreator.createReservationWithId2();
    }

    @ParameterizedTest(name = "{index} => start={0},end={1},totalCost={2},deposit={3}")
    @MethodSource("testCasesForSingleVehicle")
    void shouldCalculateTotalCostForReservationsWithSingleVehicle(
            LocalDateTime start, LocalDateTime end, Money totalCost, Money deposit) {
        updateDuration(new RentDuration(start, end));
        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId2.getId());

        Reservation reservation = costService.calculateAndApplyCosts(reservationWithId2, new HashSet<>(vehicles));

        assertEquals(totalCost, reservation.getTotalCost());
        assertEquals(deposit, reservation.getDepositAmount());
    }

    @ParameterizedTest(name = "{index}=>start={0},end={1},totalCost={2},deposit={3}")
    @MethodSource("testCasesForTwoVehicles")
    void shouldCalculateTotalCostForReservationsWithTwoVehicles(
            LocalDateTime start, LocalDateTime end, Money totalCost, Money deposit) {
        setVehiclesForReservation();
        updateDuration(new RentDuration(start, end));
        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId2.getId());

        Reservation reservation = costService.calculateAndApplyCosts(reservationWithId2, new HashSet<>(vehicles));

        assertEquals(totalCost, reservation.getTotalCost());
        assertEquals(deposit, reservation.getDepositAmount());
    }

    private static Stream<Arguments> testCasesForSingleVehicle() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 10, 10, 0, 0),
                        new Money(BigDecimal.valueOf(1200.00)),
                        new Money(BigDecimal.valueOf(1000.00))
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 7, 18, 0, 0),
                        new Money(BigDecimal.valueOf(1050.00)),
                        new Money(BigDecimal.valueOf(1000.00))
                )
                , Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 15, 10, 0, 0),
                        new Money(BigDecimal.valueOf(1427.50)),
                        new Money(BigDecimal.valueOf(1000.00))
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 18, 10, 0, 0),
                        new Money(BigDecimal.valueOf(1540.00)),
                        new Money(BigDecimal.valueOf(1000.00))
                )
        );
    }

    private static Stream<Arguments> testCasesForTwoVehicles() {
        return Stream.of(
                Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 10, 10, 0, 0),
                        new Money(BigDecimal.valueOf(3100.00)),
                        new Money(BigDecimal.valueOf(2500.00))
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 7, 18, 0, 0),
                        new Money(BigDecimal.valueOf(2650.00)),
                        new Money(BigDecimal.valueOf(2500.00))
                )
                , Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 15, 10, 0, 0),
                        new Money(BigDecimal.valueOf(3782.50)),
                        new Money(BigDecimal.valueOf(2500.00))
                ),
                Arguments.of(
                        LocalDateTime.of(2024, 7, 7, 10, 0, 0),
                        LocalDateTime.of(2024, 7, 18, 10, 0, 0),
                        new Money(BigDecimal.valueOf(4120.00)),
                        new Money(BigDecimal.valueOf(2500.00))
                )
        );
    }

    private void setVehiclesForReservation() {
        reservationWithId2.setVehicles(vehicleCreator.createSetWithVehicle5And6());
        reservationRepository.save(reservationWithId2);
    }


    private void updateDuration(RentDuration duration) {
        RentInformation rentInformation = reservationWithId2.getRentInformation();
        RentInformation updated = rentInformation.toBuilder()
                .rentDuration(duration)
                .build();
        reservationWithId2.setRentInformation(updated);
    }
}
