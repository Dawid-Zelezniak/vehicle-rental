package com.vehicle.rental.zelezniak.reservation.controller;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.dto.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.security.validation.AccessValidator;
import com.vehicle.rental.zelezniak.security.validation.UserAccess;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;
    private final AccessValidator validator;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Reservation> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Reservation findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/user/{userId}")
    public Page<Reservation> findAllByUserId(@PathVariable Long userId, Pageable pageable, Principal principal) {
        validator.validateUserAccess(new UserAccess(principal,userId,
                "You can not search for other users reservations."));
        return service.findAllByUserId(userId, pageable);
    }

    @GetMapping("/vehicles/from_reservation/{id}")
    public Page<Vehicle> findVehiclesByReservation(@PathVariable Long id, Pageable pageable) {
        return service.findVehiclesByReservationId(id, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Reservation add(@RequestBody @Valid ReservationCreationRequest request) {
        return service.addReservation(request);
    }

    @PutMapping("/{id}/location")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Reservation updateLocation(@PathVariable Long id, @RequestBody @Valid RentInformation updatedLocation) {
        return service.updateLocationForNewReservation(id, updatedLocation);
    }

    @PutMapping("/{id}/duration")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public Reservation updateDuration(@PathVariable Long id, @RequestBody @Valid RentDuration duration) {
        return service.updateDurationForNewReservation(id, duration);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteReservation(id);
    }

    @PutMapping("/{reservationId}/vehicle/{vehicleId}")
    public void addVehicle(@PathVariable Long reservationId, @PathVariable Long vehicleId) {
        service.addVehicleToNewReservation(reservationId, vehicleId);
    }

    @DeleteMapping("/{reservationId}/vehicle/{vehicleId}")
    public void deleteVhicle(@PathVariable Long reservationId, @PathVariable Long vehicleId) {
        service.deleteVehicleFromNewReservation(reservationId, vehicleId);
    }

    @GetMapping("/calculate/cost/{id}")
    public Money calculate(@PathVariable Long id) {
        return service.calculateNewReservationCost(id);
    }

    //activate reservation
    // admin must send post request with reservation id
    // system validates the reservation status ,is Paid or not
    // reservation changes status to inactive
    // reservation will be converted to rent
}
