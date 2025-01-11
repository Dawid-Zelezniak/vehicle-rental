package com.vehicle.rental.zelezniak.vehicle.controller;

import com.vehicle.rental.zelezniak.vehicle.model.dto.AvailableVehiclesCriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public Vehicle findById(@PathVariable Long id) {
        return vehicleService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle add(@RequestBody @Validated Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Vehicle update(@PathVariable Long id, @RequestBody @Validated Vehicle newData) {
        return vehicleService.update(id, newData);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }

    @PostMapping("/criteria/search")
    public Page<Vehicle> findByCriteria(@RequestBody @Validated CriteriaSearchRequest searchRequest, Pageable pageable) {
        return vehicleService.findByCriteria(searchRequest, pageable);
    }

    @PostMapping("/available/in_period")
    public Page<Vehicle> findAvailableVehicles(@RequestBody @Validated AvailableVehiclesCriteriaSearchRequest searchRequest, Pageable pageable) {
        return vehicleService.findAvailableVehicles(searchRequest, pageable);
    }
}
