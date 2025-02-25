package com.vehicle.rental.zelezniak.reservation.service.validation;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.AvailableVehiclesRetriever;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationPaymentValidator {

    private static final int UNACCEPTABLE_VEHICLES_NUMBER = 0;
    private final AvailableVehiclesRetriever vehiclesRetriever;
    private final ReservationService reservationService;

    /**
     * This method validates the reservation data before proceeding with the payment:
     * - The reservation must have status 'NEW'.
     * - The reservation must contain at least one vehicle.
     * - It must be ensured that none of the selected vehicles have been already reserved or rented.
     */
    public void validateReservationDataBeforePayment(Long id) {
        log.debug("Starting validation process before payment. Reservation id: {}", id);
        Reservation reservationToPay = reservationService.findById(id);
        validateReservationStatus(reservationToPay.getReservationStatus());
        Collection<Vehicle> vehicles = reservationService.findVehiclesByReservationId(reservationToPay.getId());
        checkIfReservationContainsVehicles(vehicles.size());
        checkIfNobodyReservedAnyPickedVehicle(reservationToPay, vehicles);
    }

    private void validateReservationStatus(Reservation.ReservationStatus status) {
        if (status != Reservation.ReservationStatus.NEW) {
            throw new IllegalArgumentException("Payments are unavailable for reservations with status other than NEW.");
        }
    }

    private void checkIfReservationContainsVehicles(int size) {
        if (size == UNACCEPTABLE_VEHICLES_NUMBER) {
            throw new IllegalArgumentException("Reservation must contains at least one vehicle.");
        }
    }

    private void checkIfNobodyReservedAnyPickedVehicle(Reservation reservation, Collection<Vehicle> pickedVehicles) {
        RentInformation information = reservation.getRentInformation();
        RentDuration rentDuration = information.getRentDuration();
        Collection<Vehicle> availableVehiclesInPeriod = vehiclesRetriever.findAvailableVehiclesByRentDuration(rentDuration);
        if (!availableVehiclesInPeriod.containsAll(pickedVehicles)) {
            removeVehiclesAndThrowException(reservation.getId());
        }
    }

    private void removeVehiclesAndThrowException(Long reservationId) {
        reservationService.deleteVehiclesFromReservation(reservationId);
        throw new IllegalArgumentException(
                "One or more vehicles in your reservation have already been taken. Please select new vehicles.");
    }
}
