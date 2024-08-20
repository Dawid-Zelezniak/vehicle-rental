package com.vehicle.rental.zelezniak.reservation_domain.service;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
public class ReservationCostCalculator {

    private static final BigDecimal FIVE_PERCENT_DISCOUNT = BigDecimal.valueOf(0.05);
    private static final BigDecimal TEN_PERCENT_DISCOUNT = BigDecimal.valueOf(0.1);
    private static final int FIVE_DAYS = 5;
    private static final int TEN_DAYS = 10;

    public Reservation calculateAndApplyCosts(Reservation reservation, Set<Vehicle> vehicles) {
        long duration = calculateRentDuration(reservation);
        BigDecimal discountPercentage = determineDiscountPercentage(duration);
        BigDecimal basicRentalCost = calculateBasicCost(duration, vehicles);
        BigDecimal afterDiscount = applyDiscount(basicRentalCost, discountPercentage);
        BigDecimal deposit = calculateTotalDeposit(vehicles);
        reservation.setTotalCost(new Money(afterDiscount.add(deposit)));
        reservation.setDepositAmount(new Money(deposit));
        return reservation;
    }

    private long calculateRentDuration(Reservation reservation) {
        var information = reservation.getRentInformation();
        RentDuration rentDuration = information.getRentDuration();
        LocalDateTime rentalStart = rentDuration.getRentalStart();
        LocalDateTime rentalEnd = rentDuration.getRentalEnd();
        return ChronoUnit.DAYS.between(rentalStart, rentalEnd) + 1;
    }

    private BigDecimal determineDiscountPercentage(
            long duration) {
        if (duration <= FIVE_DAYS) {
            return BigDecimal.ZERO;
        } else if (duration <= TEN_DAYS) {
            return FIVE_PERCENT_DISCOUNT;
        } else return TEN_PERCENT_DISCOUNT;
    }

    private BigDecimal calculateBasicCost(long duration, Set<Vehicle> vehicles) {
        BigDecimal cost = BigDecimal.ZERO;
        for (Vehicle vehicle : vehicles) {
            Money pricePerDay = vehicle.getPricePerDay();
            BigDecimal value = pricePerDay.getValue();
            BigDecimal vehicleRentalCost = value.multiply(
                    BigDecimal.valueOf(duration));
            cost = cost.add(vehicleRentalCost);
        }
        return cost;
    }

    private BigDecimal applyDiscount(BigDecimal basicCost, BigDecimal discountPercentage) {
        BigDecimal discountValue = basicCost.multiply(
                discountPercentage);
        return basicCost.subtract(
                discountValue);
    }

    private BigDecimal calculateTotalDeposit(Set<Vehicle> vehicles) {
        BigDecimal result = BigDecimal.ZERO;
        for (Vehicle vehicle : vehicles) {
            Money deposit = vehicle.getDeposit();
            result = result.add(deposit.getValue());
        }
        return result;
    }
}
