package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class VehicleStatusCriteriaSearch implements VehicleSearchStrategy {

    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findByCriteria(Object value, Pageable pageable) {
        try {
            String status = (String) value;
            Vehicle.Status s = Vehicle.Status.getStatusFromString(status);
            return findByStatus(s, pageable);
        } catch (Exception e) {
            log.error("Exception while searching by vehicle status: {}", e.getMessage());
            throw new IllegalArgumentException("Searching vehicles by status failed.");
        }
    }

    public Page<Vehicle> findByStatus(Vehicle.Status status, Pageable pageable) {
        return vehicleRepository.findByStatus(status, pageable);
    }
}
