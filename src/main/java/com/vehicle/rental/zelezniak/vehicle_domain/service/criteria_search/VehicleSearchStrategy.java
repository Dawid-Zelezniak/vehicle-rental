package com.vehicle.rental.zelezniak.vehicle_domain.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleSearchStrategy {

    Page<Vehicle> findByCriteria(Object value, Pageable pageable);
}
