package com.vehicle.rental.zelezniak.vehicle.controller;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle add(@RequestBody @Validated Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    @PutMapping("/update/{id}")
    public Vehicle update(@PathVariable Long id, @RequestBody @Validated Vehicle newData) {
        return vehicleService.update(id, newData);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }

    @PostMapping("/criteria/search")
    public <T> Page<Vehicle> findByCriteria(@RequestBody @Validated CriteriaSearchRequest<T> searchRequest, Pageable pageable) {
        return vehicleService.findByCriteria(searchRequest, pageable);
    }

    @PostMapping("/available/in_period")
    public Page<Vehicle> findAvailableVehicles(@RequestBody @Validated RentDuration duration, Pageable pageable) {
        return vehicleService.findAvailableVehicles(duration, pageable);
    }
}
