package com.vehicle.rental.zelezniak.reservation.controller;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.util.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;

    @GetMapping
    public Page<Reservation> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Reservation findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/client/{id}")
    public Page<Reservation> findAllByClientId(@PathVariable Long id, Pageable pageable) {
        return service.findAllByClientId(id, pageable);
    }

    @PostMapping("/create")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Reservation add(@RequestBody @Valid ReservationCreationRequest request) {
        return service.addReservation(request);
    }

    @PutMapping("/update/location/{id}")
    public Reservation updateLocation(@PathVariable Long id, @RequestBody @Validated RentInformation updatedLocation) {
        return service.updateLocation(id, updatedLocation);
    }

    @PutMapping("/update/duration/{id}")
    public Reservation updateDuration(@PathVariable Long id, @RequestBody @Validated RentDuration duration) {
        return service.updateDuration(id, duration);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteReservation(id);
    }

    @PutMapping("/add/vehicle/")
    public void addVehicle(@RequestParam Long reservationId, @RequestParam Long vehicleId) {
        service.addVehicleToReservation(reservationId, vehicleId);
    }

    @PutMapping("/delete/vehicle/")
    public void deleteVhicle(@RequestParam Long reservationId, @RequestParam Long vehicleId) {
        service.deleteVehicleFromReservation(reservationId, vehicleId);
    }

    @GetMapping("/calculate/cost/{id}")
    public Money calculate(@PathVariable Long id) {
        return service.calculateCost(id);
    }
}
