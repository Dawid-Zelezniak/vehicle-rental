package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.common_value_objects.location.Location;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReservationCreator {

    private final ClientCreator clientCreator;
    private final VehicleCreator vehicleCreator;

    public Reservation createReservationWithId2() {
        return Reservation.builder()
                .id(2L)
                .reservationStatus(Reservation.ReservationStatus.COMPLETED)
                .totalCost(new Money(BigDecimal.valueOf(1200.00)))
                .depositAmount(new Money(BigDecimal.valueOf(1000.00)))
                .rentInformation(buildRentInformation())
                .vehicles(addVehicleWithId1())
                .client(clientCreator.createClientWithId2())
                .build();
    }

    public Reservation buildNewReservation() {
        return Reservation.builder()
                .reservationStatus(Reservation.ReservationStatus.NEW)
                .client(clientCreator.createClientWithId2())
                .rentInformation(buildNewRentInfo())
                .build();
    }

    private RentInformation buildRentInformation() {
        return RentInformation.builder()
                .rentDuration(RentDuration.builder()
                        .rentalStart(LocalDateTime.of(2024, 7, 7, 10, 0, 0))
                        .rentalEnd(LocalDateTime.of(2024, 7, 10, 10, 0, 0))
                        .build())
                .pickUpLocation(buildLocation())
                .dropOffLocation(buildLocation())
                .build();
    }

    private Location buildLocation() {
        return Location.builder()
                .city(new City("Lublin"))
                .street(new Street("Turystyczna"))
                .additionalInformation("Next to the Leclerc mall")
                .build();
    }

    private Set<Vehicle> addVehicleWithId1() {
        Set<Vehicle> vehicles = new HashSet<>();
        vehicles.add(vehicleCreator.createCarWithId1());
        return vehicles;
    }

    private RentInformation buildNewRentInfo() {
        return RentInformation.builder()
                .rentDuration(RentDuration.builder()
                        .rentalStart(LocalDateTime.of(2024, 7, 24, 10, 0, 0))
                        .rentalEnd(LocalDateTime.of(2024, 7, 28, 10, 0, 0))
                        .build())
                .build();
    }
}
