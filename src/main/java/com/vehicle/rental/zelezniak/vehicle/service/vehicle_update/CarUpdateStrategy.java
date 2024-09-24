package com.vehicle.rental.zelezniak.vehicle.service.vehicle_update;

import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Car;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;

public class CarUpdateStrategy implements VehicleUpdateStrategy {

    public Car update(Vehicle existing, Vehicle newData) {
        Car existingCar = (Car) existing;
        Car newCarData = (Car) newData;

        return existingCar.toBuilder()
                .vehicleInformation(newCarData.getVehicleInformation())
                .doorsNumber(newCarData.getDoorsNumber())
                .bodyType(newCarData.getBodyType())
                .driveType(newCarData.getDriveType())
                .pricePerDay(newCarData.getPricePerDay())
                .status(newCarData.getStatus())
                .deposit(newCarData.getDeposit())
                .build();
    }
}
