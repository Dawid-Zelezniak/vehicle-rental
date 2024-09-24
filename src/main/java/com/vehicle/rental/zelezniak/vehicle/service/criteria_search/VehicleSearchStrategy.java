package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleSearchStrategy {

    Page<Vehicle> findByCriteria(Object value, Pageable pageable);
}
