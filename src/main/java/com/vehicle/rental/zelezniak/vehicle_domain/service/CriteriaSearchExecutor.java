package com.vehicle.rental.zelezniak.vehicle_domain.service;

import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects.Year;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Setter
@RequiredArgsConstructor
public class CriteriaSearchExecutor {

    private static final String CAN_NOT_CONVERT = "Can not convert value to ";

    Pageable pageable;
    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findByBrand(Object brand) {
        return vehicleRepository.findByVehicleInformationBrand((String) brand,pageable);
    }

    public Page<Vehicle> findByModel(Object model) {
        return vehicleRepository.findByVehicleInformationModel((String) model,pageable);
    }

    public Page<Vehicle> findByRegistrationNumber(Object registration) {
        RegistrationNumber number = getRegistrationValue(registration);
        return vehicleRepository.findByVehicleInformationRegistrationNumber(number,pageable);
    }

    public Page<Vehicle> findByProductionYear(Object productionYear) {
        Year year = new Year(getYearValue(productionYear));
        return vehicleRepository.findByVehicleInformationProductionYear(year,pageable);
    }

    public Page<Vehicle> findByStatus(Object statusValue) {
        String status = (String) statusValue;
        Vehicle.Status s = Vehicle.Status.getStatusFromString(status);
        return vehicleRepository.findByStatus(s,pageable);
    }

    private <T> RegistrationNumber getRegistrationValue(T value) {
        if (value instanceof RegistrationNumber number) {
            return number;
        } else if (value instanceof String number) {
            return new RegistrationNumber(number);
        }
        String message = CAN_NOT_CONVERT + "registration number:" + value;
        throw new IllegalArgumentException(message);
    }

    private <T> int getYearValue(T value) {
        if (value instanceof String s) {
            return Integer.parseInt((s));
        } else if (value instanceof Number) {
            return (int) value;
        }
        String message = CAN_NOT_CONVERT + "year:" + value;
        throw new IllegalArgumentException(message);
    }
}
