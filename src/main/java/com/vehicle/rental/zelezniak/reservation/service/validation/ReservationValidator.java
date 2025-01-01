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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationValidator {

    private static final int UNACCEPTABLE_VEHICLES_NUMBER = 0;
    private final AvailableVehiclesRetriever vehiclesRetriever;
    private final ReservationService reservationService;

    /**
     * This method validates the reservation data before proceeding with the payment:
     * - The reservation must have status 'NEW'.
     * - The reservation must contain at least one vehicle.
     * - It must be ensured that none of the selected vehicles have been already reserved or rented.
     */
    @Transactional
    public void validateReservationDataBeforePayment(Long id) {
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
        Collection<Vehicle> availableVehiclesInPeriod = vehiclesRetriever.findVehiclesAvailableInPeriod(rentDuration);
        if (!availableVehiclesInPeriod.containsAll(pickedVehicles)) {
            removeVehiclesAndThrowException(reservation.getId());
        }
    }

    private void removeVehiclesAndThrowException(Long reservationId) {
       reservationService.deleteVehiclesFromReservation(reservationId);
        throw new IllegalArgumentException(
                "Someone already reserved vehicle that you tried to reserve.Pick vehicles one more time.");
    }
}
