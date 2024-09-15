package com.vehicle.rental.zelezniak.vehicle_domain.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects.Year;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class ProductionYearCriteriaSearch implements VehicleSearchStrategy {

    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findByCriteria(Object value, Pageable pageable) {
        try {
            Year year = new Year(getYearValue(value));
            return findByProductionYear(year, pageable);
        } catch (Exception e) {
            log.error("Exception while searching by production year: {}", e.getMessage());
            throw new IllegalArgumentException("Searching vehicles by production year failed.");
        }
    }

    private <T> int getYearValue(T value) {
        if (value instanceof String s) {
            return Integer.parseInt((s));
        } else if (value instanceof Number) {
            return (int) value;
        }
        String message = "Can not convert year:" + value;
        throw new IllegalArgumentException(message);
    }

    public Page<Vehicle> findByProductionYear(Year productionYear, Pageable pageable) {
        return vehicleRepository.findByVehicleInformationProductionYear(productionYear, pageable);
    }
}
