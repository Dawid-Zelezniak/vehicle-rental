package com.vehicle.rental.zelezniak.vehicle_domain.service.vehicle_update;

import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Motorcycle;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;

public class MotorcycleUpdateStrategy implements VehicleUpdateStrategy{

    public Motorcycle update(
            Vehicle existing,
            Vehicle newData) {
        Motorcycle existingMotorcycle = (Motorcycle) existing;
        Motorcycle newMotorcycleData = (Motorcycle) newData;

        return existingMotorcycle.toBuilder()
                .vehicleInformation(newMotorcycleData.getVehicleInformation())
                .motorcycleType(newMotorcycleData.getMotorcycleType())
                .pricePerDay(newMotorcycleData.getPricePerDay())
                .status(newMotorcycleData.getStatus())
                .deposit(newMotorcycleData.getDeposit())
                .build();
    }
}
