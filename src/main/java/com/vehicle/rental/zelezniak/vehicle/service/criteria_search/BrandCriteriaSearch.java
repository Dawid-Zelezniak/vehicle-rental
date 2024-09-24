package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class BrandCriteriaSearch implements VehicleSearchStrategy {

    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findByCriteria(Object value, Pageable pageable) {
        try {
            String brand = (String) value;
            return findByBrand(brand, pageable);
        } catch (Exception e) {
            log.error("Exception while searching by brand: {}", e.getMessage());
            throw new IllegalArgumentException("Searching vehicles by brand failed.");
        }
    }

    public Page<Vehicle> findByBrand(String brand, Pageable pageable) {
        return vehicleRepository.findByVehicleInformationBrand(brand, pageable);
    }
}
