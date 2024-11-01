package com.vehicle.rental.zelezniak.reservation.service;

import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.rent.service.RentService;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.util.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;
import static com.vehicle.rental.zelezniak.util.validation.InputValidator.*;

//refactor validation and add logs
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
    public Page<Reservation> findAllByClientId(Long clientId, Pageable pageable) {
        log.info("Searching for reservations by client ID : {}", clientId);
        validateNotNull(clientId, CLIENT_ID_NOT_NULL);
        return repository.findAllReservationsByClientId(clientId, pageable);
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

    @Transactional
    public Reservation addReservation(ReservationCreationRequest request) {
        log.debug("Received new ReservationCreationRequest : {}", request);
        inputValidator.throwExceptionIfObjectIsNull(request, "Reservation creation request" + CAN_NOT_BE_NULL);
        return newReservationService.addNewReservation(request);
    }

    @Transactional
    public Reservation updateLocation(Long id, RentInformation newData) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(newData, "Rent information" + CAN_NOT_BE_NULL);
        Reservation r = findReservation(id);
        return newReservationService.updateLocationForReservation(r, newData);
    }

    @Transactional
    public Reservation updateDuration(Long id, RentDuration duration) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(duration, "Rent duration" + CAN_NOT_BE_NULL);
        Reservation r = findReservation(id);
        return newReservationService.updateDurationForReservation(r, duration);
    }

    @Transactional
    public void deleteReservation(Long id) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        Reservation r = findReservation(id);
        newReservationService.deleteReservation(r);
    }

    @Transactional
    public void addVehicleToReservation(Long id, Long vehicleId) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(vehicleId, VEHICLE_ID_NOT_NULL);
        Reservation r = findReservation(id);
        newReservationService.addVehicleToReservation(r, vehicleId);
    }

    @Transactional
    public void deleteVehicleFromReservation(Long id, Long vehicleId) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        validateNotNull(vehicleId, VEHICLE_ID_NOT_NULL);
        Reservation r = findReservation(id);
        newReservationService.deleteVehicleFromReservation(r, vehicleId);
    }

    @Transactional(readOnly = true)
    public Money calculateCost(Long id) {
        validateNotNull(id, RESERVATION_ID_NOT_NULL);
        Reservation r = findReservation(id);
        return newReservationService.calculateCost(r);
    }

    @Transactional
    public void setReservationStatusAsACTIVE(StripeObject stripeObject) {
        Long reservationId = getReservationIdFromMetadata(stripeObject);
        Reservation reservation = findReservation(reservationId);
        log.debug("Updating reservation status to ACTIVE for reservation id: {}", reservationId);
        reservation.setReservationStatus(Reservation.ReservationStatus.ACTIVE);
        repository.save(reservation);
    }

    private void validateNotNull(Object o, String message) {
        inputValidator.throwExceptionIfObjectIsNull(o, message);
    }

    private Reservation findReservation(Long id) {
        log.debug("Searching for reservation by ID : {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Reservation with ID : {} not found", id);
                    return new NoSuchElementException("Reservation with id " + id + " does not exist.");
                });
    }

    private Long getReservationIdFromMetadata(StripeObject stripeObject) {
        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        Map<String, String> metadata = paymentIntent.getMetadata();
        String idAsString = metadata.get("reservation_id");
        if (idAsString == null) {
            throw new IllegalArgumentException("Reservation id not found in metadata");
        }
        try {
            return Long.valueOf(idAsString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid id format in metadata");
        }
    }
}
