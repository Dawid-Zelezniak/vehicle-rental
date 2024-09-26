package com.vehicle.rental.zelezniak.reservation.service;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.util.NewReservationBuilder;
import com.vehicle.rental.zelezniak.reservation.model.util.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.reservation_update.ReservationUpdateStrategy;
import com.vehicle.rental.zelezniak.reservation.service.reservation_update.ReservationUpdateStrategyFactory;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.model.client.dto.ClientDto;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;

/**
 * Service class responsible for managing reservations that are in the NEW status.
 * Reservation has status NEW before payment , after payment reservation
 * changes its status to ACTIVE.
 */
@Service
@RequiredArgsConstructor
public class NewReservationService {

    private final ClientService clientService;
    private final ReservationCostCalculator calculator;
    private final ReservationUpdateStrategyFactory strategyFactory;
    private final ReservationRepository reservationRepository;
    private final NewReservationBuilder reservationBuilder;

    @Transactional
    public Reservation addNewReservation(ReservationCreationRequest request) {
        return buildAndSaveReservation(request);
    }

    @Transactional
    public Reservation updateLocationForReservation(Reservation existing, Reservation newData) {
        checkIfStatusIsEqualNEW(existing, "Can not update reservation with status: "
                + existing.getReservationStatus());
        ReservationUpdateStrategy<Reservation> strategy = (ReservationUpdateStrategy<Reservation>)
                strategyFactory.getStrategy(newData.getClass());
        Reservation updated = strategy.update(existing, newData);
        return reservationRepository.save(updated);
    }

    /**
     * Updates the duration of an existing reservation and removes any vehicles associated with it.
     */
    @Transactional
    public Reservation updateDurationForReservation(Reservation existing, RentDuration duration) {
        checkIfStatusIsEqualNEW(existing, "Can not update duration for reservation with status: "
                + existing.getReservationStatus());
        ReservationUpdateStrategy<RentDuration> strategy = (ReservationUpdateStrategy<RentDuration>)
                strategyFactory.getStrategy(duration.getClass());
        Reservation updated = strategy.update(existing, duration);
        updated.setVehicles(null);
        return reservationRepository.save(updated);
    }

    @Transactional
    public void deleteReservation(Reservation reservation) {
        checkIfStatusIsEqualNEW(reservation, "Can not remove reservation with status: "
                + reservation.getReservationStatus());
        handleRemove(reservation);
    }

    @Transactional
    public void addVehicleToReservation(Reservation reservation, Long vehicleId) {
        checkIfStatusIsEqualNEW(reservation, "Can not add vehicle to reservation with status: " +
                reservation.getReservationStatus());
        reservationRepository.addVehicleToReservation(vehicleId, reservation.getId());
    }

    @Transactional
    public void deleteVehicleFromReservation(Reservation r, Long vehicleId) {
        checkIfStatusIsEqualNEW(r, "Can not remove vehicle from reservation with status: "
                + r.getReservationStatus());
        reservationRepository.deleteVehicleFromReservation(r.getId(), vehicleId);
    }

    /**
     * Calculates the total cost and deposit amount for a reservation.
     */
    @Transactional
    public Money calculateCost(Reservation reservation) {
        checkIfStatusIsEqualNEW(reservation, "When calculating the total cost, the reservation status should be NEW");
        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservation.getId());
        Reservation updated = calculator.calculateAndApplyCosts(reservation, new HashSet<>(vehicles));
        reservationRepository.save(updated);
        return updated.getTotalCost();
    }

    private Reservation buildAndSaveReservation(ReservationCreationRequest request) {
        Client client = clientService.findClientById(request.getClientId());
        Reservation reservation = reservationBuilder.build(client, request.getDuration());
        return reservationRepository.save(reservation);
    }

    private void checkIfStatusIsEqualNEW(Reservation reservation, String message) {
        if (reservation.getReservationStatus() != Reservation.ReservationStatus.NEW) {
            throw new IllegalArgumentException(message);
        }
    }

    private void handleRemove(Reservation reservation) {
        reservation.setClient(null);
        reservation.setVehicles(null);
        reservationRepository.deleteById(reservation.getId());
    }
}
