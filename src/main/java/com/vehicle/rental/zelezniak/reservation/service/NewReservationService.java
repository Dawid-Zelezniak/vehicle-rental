package com.vehicle.rental.zelezniak.reservation.service;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.dto.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.calculations.ReservationCostService;
import com.vehicle.rental.zelezniak.reservation.service.reservation_update.ReservationUpdateStrategy;
import com.vehicle.rental.zelezniak.reservation.service.reservation_update.ReservationUpdateStrategyProvider;
import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.user.service.ClientService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.validation.AvailableVehiclesValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class NewReservationService {

    private final ClientService clientService;
    private final ReservationCostService calculator;
    private final ReservationUpdateStrategyProvider strategyFactory;
    private final ReservationRepository reservationRepository;
    private final NewReservationBuilder reservationBuilder;
    private final AvailableVehiclesValidator vehiclesValidator;

    @Transactional
    public Reservation addNewReservation(ReservationCreationRequest request) {
        return buildAndSaveReservation(request);
    }

    @Transactional
    public Reservation updateLocationForReservation(Reservation existing, RentInformation newData) {
        log.debug("Updating location for reservation with ID : {}", existing.getId());
        checkIfStatusIsEqualNEW(existing, "Can not update reservation with status: " + existing.getReservationStatus());
        ReservationUpdateStrategy<RentInformation> strategy = (ReservationUpdateStrategy<RentInformation>)
                strategyFactory.getStrategy(newData.getClass());
        Reservation updated = strategy.update(existing, newData);
        return reservationRepository.save(updated);
    }

    @Transactional
    public Reservation updateDurationForReservation(Reservation existing, RentDuration duration) {
        log.debug("Updating duration for reservation with ID : {}", existing.getId());
        checkIfStatusIsEqualNEW(existing, "Can not update duration for reservation with status: "
                + existing.getReservationStatus());
        ReservationUpdateStrategy<RentDuration> strategy = (ReservationUpdateStrategy<RentDuration>)
                strategyFactory.getStrategy(duration.getClass());
        Reservation updated = strategy.update(existing, duration);
        return reservationRepository.save(updated);
    }

    @Transactional
    public void deleteReservation(Reservation reservation) {
        checkIfStatusIsEqualNEW(reservation, "Can not remove reservation with status: " + reservation.getReservationStatus());
        handleRemove(reservation);
    }

    @Transactional
    public void addVehicleToReservation(Reservation reservation, Long vehicleId) {
        checkIfStatusIsEqualNEW(reservation, "Can not add vehicle to reservation with status: " + reservation.getReservationStatus());
        log.warn("Adding vehicle with ID : {} to reservation with ID : {}", vehicleId, reservation.getId());
        vehiclesValidator.checkIfVehicleIsStillAvailable(reservation.getDuration(), vehicleId);
        reservationRepository.addVehicleToReservation(vehicleId, reservation.getId());
    }

    @Transactional
    public void deleteVehicleFromReservation(Reservation reservation, Long vehicleId) {
        checkIfStatusIsEqualNEW(reservation, "Can not remove vehicle from reservation with status: " + reservation.getReservationStatus());
        log.warn("Deleting vehicle with ID : {} from reservation with ID : {}", vehicleId, reservation.getId());
        reservationRepository.deleteVehicleFromReservation(reservation.getId(), vehicleId);
    }

    /**
     * Calculates the total cost and deposit amount for a reservation.
     */
    @Transactional
    public Money calculateCost(Reservation reservation) {
        checkIfStatusIsEqualNEW(reservation, "When calculating the total cost, the reservation status should be NEW");
        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservation.getId());
        log.info("Calculating cost for reservation with ID : {}", reservation.getId());
        Reservation updated = calculator.calculateAndApplyCosts(reservation, new HashSet<>(vehicles));
        reservationRepository.save(updated);
        log.info("Reservation with calculated cost has been saved.");
        return updated.getTotalCost();
    }

    private Reservation buildAndSaveReservation(ReservationCreationRequest request) {
        Client client = clientService.findClientById(request.clientId());
        Reservation reservation = reservationBuilder.build(client, request.duration());
        Reservation saved = reservationRepository.save(reservation);
        log.info("New reservation with ID : {} has been saved", saved.getId());
        return saved;
    }

    private void checkIfStatusIsEqualNEW(Reservation r, String message) {
        if (r.getReservationStatus() != Reservation.ReservationStatus.NEW) {
            throw new IllegalArgumentException(message);
        }
    }

    private void handleRemove(Reservation reservation) {
        log.warn("Deleting reservation with ID : {}", reservation.getId());
        reservation.setClient(null);
        reservation.setVehicles(null);
        reservationRepository.save(reservation);
        reservationRepository.deleteById(reservation.getId());
    }
}
