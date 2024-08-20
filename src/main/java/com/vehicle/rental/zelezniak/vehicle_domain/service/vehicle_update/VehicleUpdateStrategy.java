package com.vehicle.rental.zelezniak.vehicle_domain.service.vehicle_update;

import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;

public interface VehicleUpdateStrategy {

    Vehicle update(Vehicle existing,Vehicle newData);
}
