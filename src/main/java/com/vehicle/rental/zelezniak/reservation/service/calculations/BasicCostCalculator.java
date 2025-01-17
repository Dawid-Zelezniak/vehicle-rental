package com.vehicle.rental.zelezniak.reservation.service.calculations;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
class BasicCostCalculator {

    BigDecimal calculateBasicCost(int duration, Set<Vehicle> vehicles) {
        BigDecimal cost = BigDecimal.ZERO;
        for (Vehicle vehicle : vehicles) {
            BigDecimal vehicleRentalCost = calculateRentalCostForVehicle(vehicle, duration);
            cost = cost.add(vehicleRentalCost);
        }
        return cost;
    }

    private BigDecimal calculateRentalCostForVehicle(Vehicle vehicle, int duration) {
        Money pricePerDay = vehicle.getPricePerDay();
        BigDecimal value = pricePerDay.value();
        return value.multiply(BigDecimal.valueOf(duration));
    }
}
