package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Location;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import com.vehicle.rental.zelezniak.rent.model.Rent;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RentCreator {

    private final ClientCreator clientCreator;
    private final VehicleCreator vehicleCreator;

    public Rent createRentWithId1() {
        return Rent.builder()
                .id(1L)
                .rentStatus(Rent.RentStatus.COMPLETED)
                .totalCost(new Money(BigDecimal.valueOf(1200.00)))
                .depositAmount(new Money(BigDecimal.valueOf(1000.00)))
                .rentInformation(buildRentInformation())
                .vehicles(addVehicleWithId5())
                .client(clientCreator.createClientWithId2())
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

    private Set<Vehicle> addVehicleWithId5() {
        Set<Vehicle> vehicles = new HashSet<>();
        vehicles.add(vehicleCreator.createCarWithId1());
        return vehicles;
    }
}
