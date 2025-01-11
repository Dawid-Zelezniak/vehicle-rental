package com.vehicle.rental.zelezniak.vehicle.service.validation;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.AvailableVehiclesRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AvailableVehiclesValidator {

    private final AvailableVehiclesRetriever vehiclesRetriever;

    public void checkIfVehicleIsStillAvailable(RentDuration duration, Long vehicleId) {
        List<Long> vehicleIds = vehiclesRetriever.findAvailableVehiclesByRentDuration(duration)
                .stream()
                .map(Vehicle::getId)
                .toList();

        if (!vehicleIds.contains(vehicleId)) {
            throw new IllegalArgumentException("Vehicle with id: " + vehicleId + " is already rented or reserved.");
        }
    }
}
