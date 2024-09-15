package com.vehicle.rental.zelezniak.vehicle_domain.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class ModelCriteriaSearch implements VehicleSearchStrategy {

    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findByCriteria(Object value, Pageable pageable) {
        try {
            String model = (String) value;
            return findByModel(model, pageable);
        } catch (Exception e) {
            log.error("Exception while searching by model: {}", e.getMessage());
            throw new IllegalArgumentException("Searching vehicles by model failed.");
        }
    }

    public Page<Vehicle> findByModel(String model, Pageable pageable) {
        return vehicleRepository.findByVehicleInformationModel(model, pageable);
    }
}
