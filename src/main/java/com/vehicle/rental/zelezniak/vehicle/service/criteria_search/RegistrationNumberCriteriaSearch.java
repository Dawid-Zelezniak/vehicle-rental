package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class RegistrationNumberCriteriaSearch implements VehicleSearchStrategy {

    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findByCriteria(Object value, Pageable pageable) {
        try {
            RegistrationNumber number = getRegistrationValue(value);
            return findByRegistrationNumber(number, pageable);
        } catch (Exception e) {
            log.error("Exception while searching by registration number: {}", e.getMessage());
            throw new IllegalArgumentException("Searching vehicles by registration number failed.");
        }
    }

    private <T> RegistrationNumber getRegistrationValue(T value) {
        if (value instanceof RegistrationNumber number) {
            return number;
        } else if (value instanceof String number) {
            return new RegistrationNumber(number);
        }
        String message = "Can not convert registration number:" + value;
        throw new IllegalArgumentException(message);
    }

    public Page<Vehicle> findByRegistrationNumber(RegistrationNumber registration, Pageable pageable) {
        return vehicleRepository.findByVehicleInformationRegistrationNumber(registration, pageable);
    }


}
