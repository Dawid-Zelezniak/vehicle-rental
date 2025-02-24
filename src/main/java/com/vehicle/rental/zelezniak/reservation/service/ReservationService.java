package com.vehicle.rental.zelezniak.reservation.service;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.payment.provider.stripe.MetadataIdRetriever;
import com.vehicle.rental.zelezniak.payment.provider.stripe.dto.StripeWebhookEvent;
import com.vehicle.rental.zelezniak.rent.service.RentService;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.dto.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository repository;
    private final RentService rentService;// it will be used to convert reservation to rent
    private final InputValidator inputValidator;
    private final NewReservationService newReservationService;


    @Transactional(readOnly = true)
    public Page<Reservation> findAll(Pageable pageable) {
        log.info("Searching for all vehicles.");
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Reservation> findAllByUserId(Long userId, Pageable pageable) {
        log.info("Searching for reservations by user ID : {}", userId);
        validateNotNull(userId, USER_ID_NOT_NULL);
        return repository.findAllReservationsByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        return findReservation(id);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findVehiclesByReservationId(Long id, Pageable pageable) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        if (repository.existsById(id)) {
            return repository.findVehiclesByReservationId(id, pageable);
        }
        return Page.empty();
    }

    @Transactional(readOnly = true)
    public Collection<Vehicle> findVehiclesByReservationId(Long id) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        if (repository.existsById(id)) {
            return repository.findVehiclesByReservationId(id);
        }
        return new ArrayList<>();
    }

    @Transactional
    public Reservation addReservation(ReservationCreationRequest request) {
        log.debug("Received new ReservationCreationRequest : {}", request);
        inputValidator.throwExceptionIfObjectIsNull(request, "Reservation creation request can not be null.");
        return newReservationService.addNewReservation(request);
    }

    @Transactional
    public Reservation updateLocationForNewReservation(Long id, RentInformation newData) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(newData, "Rent information can not be null.");
        Reservation r = findReservation(id);
        return newReservationService.updateLocationForReservation(r, newData);
    }

    /**
     * Updates the duration of an existing reservation and removes any vehicles associated with it.
     */
    @Transactional
    public Reservation updateDurationForNewReservation(Long id, RentDuration duration) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(duration, "Rent duration can not be null.");
        Reservation reservation = findReservation(id);
        Reservation updated = newReservationService.updateDurationForReservation(reservation, duration);
        handleRemoveVehicles(id);
        return updated;
    }

    @Transactional
    public void deleteReservation(Long id) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        Reservation r = findReservation(id);
        newReservationService.deleteReservation(r);
    }

    @Transactional
    public void addVehicleToNewReservation(Long id, Long vehicleId) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(vehicleId, VEHICLE_ID_NOT_NULL);
        Reservation r = findReservation(id);
        newReservationService.addVehicleToReservation(r, vehicleId);
    }

    @Transactional
    public void deleteVehicleFromNewReservation(Long id, Long vehicleId) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(vehicleId, VEHICLE_ID_NOT_NULL);
        Reservation r = findReservation(id);
        newReservationService.deleteVehicleFromReservation(r, vehicleId);
    }

    @Transactional(readOnly = true)
    public Money calculateNewReservationCost(Long id) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        Reservation r = findReservation(id);
        return newReservationService.calculateCost(r);
    }

    /**
     * After successful payment, this method retrieves the reservation
     * ID from the metadata and updates its status.
     */
    @Transactional
    public void setReservationStatusAsACTIVE(StripeWebhookEvent.StripeObject stripeObject) {
        Long reservationId = MetadataIdRetriever.getReservationIdFromMetadata(stripeObject);
        log.info("Updating reservation status to ACTIVE for reservation with id: {}.", reservationId);
        Reservation reservation = findReservation(reservationId);
        if (reservation.hasStatus(Reservation.ReservationStatus.NEW)) {
            repository.changeReservationStatusToActive(reservationId);
            log.info("Reservation with id '{}' has been successfully updated after payment.", reservationId);
        } else {
            throw new IllegalArgumentException("Reservation has status " + reservation.getReservationStatus() + " and can not be updated.");
        }
    }

    @Transactional
    public void deleteVehiclesFromReservation(Long reservationId) {
        handleRemoveVehicles(reservationId);
    }

    private void validateNotNull(Object object, String message) {
        inputValidator.throwExceptionIfObjectIsNull(object, message);
    }

    private Reservation findReservation(Long id) {
        log.debug("Searching for reservation by ID : {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reservation with ID : {} not found", id);
                    return new NoSuchElementException("Reservation with id " + id + " does not exist.");
                });
    }

    private void handleRemoveVehicles(Long reservationId) {
        Collection<Long> vehicleIds = repository.findVehiclesIdsByReservationId(reservationId);
        log.warn("Deleting vehicles from reservation with id: {}", reservationId);
        repository.deleteVehiclesFromReservation(vehicleIds);
    }
}
