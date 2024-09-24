package com.vehicle.rental.zelezniak.vehicle.service.vehicle_update;

import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;

public interface VehicleUpdateStrategy {

    Vehicle update(Vehicle existing,Vehicle newData);
}
