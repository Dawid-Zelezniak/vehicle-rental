package com.vehicle.rental.zelezniak.reservation.service.calculations;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Calculates the total reservation cost, including any applicable discounts.
 * Discounts are applied based on the total duration of the rental period.
 * - No discount for rentals up to 5 days.
 * - 5% discount for rentals between 6 and 10 days.
 * - 10% discount for rentals longer than 10 days.
 */
@Component
@RequiredArgsConstructor
public class ReservationCostService {

    private final RentDurationCalculator durationCalculator;
    private final DiscountStrategy discountStrategy;
    private final BasicCostCalculator basicCostCalculator;
    private final DepositCalculator depositCalculator;

    public Reservation calculateAndApplyCosts(Reservation reservation, Set<Vehicle> vehicles) {
        Integer duration = durationCalculator.calculateDuration(reservation);
        BigDecimal discountPercentage = discountStrategy.determineDiscount(duration);
        BigDecimal basicRentalCost = basicCostCalculator.calculateBasicCost(duration, vehicles);
        BigDecimal afterDiscount = applyDiscount(basicRentalCost, discountPercentage);
        BigDecimal deposit = depositCalculator.calculateTotalDeposit(vehicles);
        reservation.setTotalCost(new Money(afterDiscount.add(deposit)));
        reservation.setDepositAmount(new Money(deposit));
        return reservation;
    }

    private BigDecimal applyDiscount(BigDecimal basicCost, BigDecimal discountPercentage) {
        BigDecimal discountValue = basicCost.multiply(discountPercentage);
        return basicCost.subtract(discountValue);
    }
}
