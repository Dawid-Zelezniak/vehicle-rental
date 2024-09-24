package com.vehicle.rental.zelezniak.reservation.service;

import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.rent.service.RentService;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.util.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class  ReservationService {

    private final ReservationRepository repository;
    private final RentService rentService;
    private final InputValidator inputValidator;
    private final NewReservationService newReservationService;


    @Transactional(readOnly = true)
    public Page<Reservation> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Reservation> findAllByClientId(Long id,Pageable pageable){
        return repository.findAllReservationsByClientId(id,pageable);
    }

    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        validateReservationId(id);
        return findReservation(id);
    }

    @Transactional
    public Reservation addReservation(ReservationCreationRequest request) {
        inputValidator.throwExceptionIfObjectIsNull(request, "Reservation creation request" + CAN_NOT_BE_NULL);
        return newReservationService.addNewReservation(request);
    }

    @Transactional
    public Reservation updateLocation(Long id, Reservation newData) {
        validateReservationId(id);
        validateReservation(newData);
        Reservation r = findReservation(id);
        return newReservationService.updateLocationForReservation(r, newData);
    }

    @Transactional
    public Reservation updateDuration(Long id, RentDuration duration) {
        validateReservationId(id);
        inputValidator.throwExceptionIfObjectIsNull(duration, "Rent duration" + CAN_NOT_BE_NULL);
        Reservation r = findReservation(id);
        return newReservationService.updateDurationForReservation(r, duration);
    }

    @Transactional
    public void deleteReservation(Long id) {
        validateReservationId(id);
        Reservation r = findReservation(id);
        newReservationService.deleteReservation(r);
    }

    @Transactional
    public void addVehicleToReservation(Long id, Long vehicleId) {
        validateReservationId(id);
        validateVehicleId(vehicleId);
        Reservation r = findReservation(id);
        newReservationService.addVehicleToReservation(r, vehicleId);
    }

    @Transactional
    public void deleteVehicleFromReservation(Long id, Long vehicleId) {
        validateReservationId(id);
        validateVehicleId(vehicleId);
        Reservation r = findReservation(id);
        newReservationService.deleteVehicleFromReservation(r, vehicleId);
    }

    @Transactional
    public Money calculateCost(Long id) {
        validateReservationId(id);
        Reservation r = findReservation(id);
        return newReservationService.calculateCost(r);
    }

    public void setReservationStatusAsACTIVE(StripeObject stripeObject) {
        Long reservationId = getReservationIdFromMetadata(stripeObject);
        Reservation reservation = findReservation(reservationId);
        log.info("Updating reservation status to ACTIVE for reservation id: {}", reservationId);
        reservation.setReservationStatus(Reservation.ReservationStatus.ACTIVE);
        repository.save(reservation);
    }

    private void validateReservationId(Long id) {
        inputValidator.throwExceptionIfObjectIsNull(id, RESERVATION_ID_NOT_NULL);
    }

    private Reservation findReservation(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reservation with id " + id + " does not exist."));
    }

    private void validateReservation(Reservation reservation) {
        inputValidator.throwExceptionIfObjectIsNull(reservation, RESERVATION_NOT_NULL);
    }

    private void validateVehicleId(Long id) {
        inputValidator.throwExceptionIfObjectIsNull(id, VEHICLE_ID_NOT_NULL);
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
