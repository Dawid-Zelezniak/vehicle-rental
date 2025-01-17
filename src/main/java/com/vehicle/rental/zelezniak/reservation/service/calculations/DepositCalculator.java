package com.vehicle.rental.zelezniak.reservation.service.calculations;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
class DepositCalculator {

    BigDecimal calculateTotalDeposit(Set<Vehicle> vehicles) {
        BigDecimal result = BigDecimal.ZERO;
        for (Vehicle vehicle : vehicles) {
            Money deposit = vehicle.getDeposit();
            result = result.add(deposit.value());
        }
        return result;
    }
}
