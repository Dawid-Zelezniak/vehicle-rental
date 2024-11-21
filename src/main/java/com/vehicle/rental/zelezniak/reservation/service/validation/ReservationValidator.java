package com.vehicle.rental.zelezniak.reservation.service;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.AvailableVehiclesRetriever;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final AvailableVehiclesRetriever vehiclesRetriever;
    private final ReservationRepository reservationRepository;

    @Transactional()
    public void validateReservationDataBeforePayment(Long id) {
        Reservation reservationToPay = findReservation(id);
        validateReservationStatus(reservationToPay.getReservationStatus());
        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationToPay.getId());
        checkIfReservationContainsVehicles(vehicles.size());
        checkIfNobodyReservedAnyPickedVehicle(reservationToPay, vehicles);
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist."));
    }

    private void validateReservationStatus(Reservation.ReservationStatus status) {
        if (status != Reservation.ReservationStatus.NEW) {
            throw new IllegalArgumentException("Payments are unavailable for reservations with status other than NEW.");
        }
    }

    private void checkIfReservationContainsVehicles(int size) {
        if (size == 0) {
            throw new IllegalArgumentException("Reservation must contains at least one vehicle.");
        }
    }

    private void checkIfNobodyReservedAnyPickedVehicle(Reservation reservation, Collection<Vehicle> pickedVehicles) {
        RentInformation information = reservation.getRentInformation();
        RentDuration rentDuration = information.getRentDuration();
        Collection<Vehicle> availableVehiclesInPeriod = vehiclesRetriever.findVehiclesAvailableInPeriod(rentDuration);
        if (!availableVehiclesInPeriod.containsAll(pickedVehicles)) {
            removeVehiclesAndThrowException(reservation);
        }
    }

    private void removeVehiclesAndThrowException(Reservation reservation) {
        reservation.setVehicles(null);
        reservationRepository.save(reservation);
        throw new IllegalArgumentException(
                "Someone already reserved vehicle that you tried to reserve.Pick vehicles one more time.");
    }
}
