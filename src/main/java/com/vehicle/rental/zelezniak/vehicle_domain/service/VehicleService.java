package com.vehicle.rental.zelezniak.vehicle_domain.service;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.vehicle_domain.exception.VehicleDeletionException;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.model.util.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle_domain.service.vehicle_update.VehicleUpdateStrategy;
import com.vehicle.rental.zelezniak.vehicle_domain.service.vehicle_update.VehicleUpdateStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleValidator vehicleValidator;
    private final InputValidator inputValidator;
    private final VehicleUpdateStrategyFactory strategyFactory;
    private final VehicleCriteriaSearch criteriaSearch;
    private final AvailableVehiclesRetriever vehiclesRetriever;

    @Transactional(readOnly = true)
    public Page<Vehicle> findAll(Pageable pageable) {
        log.debug("Searching all vehicles");
        return vehicleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        validateNotNull(id, InputValidator.VEHICLE_ID_NOT_NULL);
        return findVehicle(id);
    }

    @Transactional
    public Vehicle addVehicle(Vehicle vehicle) {
        validateNotNull(vehicle, InputValidator.VEHICLE_NOT_NULL);
        vehicleValidator.throwExceptionIfVehicleExist(vehicle.getRegistrationNumber());
        log.info("Saving vehicle to database");
        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle with id: {} has been saved.", saved.getId());
        return saved;
    }

    @Transactional
    public Vehicle update(Long id, Vehicle newData) {
        validateNotNull(id, InputValidator.VEHICLE_ID_NOT_NULL);
        validateNotNull(newData, InputValidator.VEHICLE_NOT_NULL);
        Vehicle vehicleFromDb = findVehicle(id);
        return validateAndUpdateVehicle(vehicleFromDb, newData);
    }

    @Transactional
    public void delete(Long id) {
        validateNotNull(id, InputValidator.VEHICLE_ID_NOT_NULL);
        handleDeleteVehicle(id);
    }

    @Transactional(readOnly = true)
    public <T> Page<Vehicle> findByCriteria(CriteriaSearchRequest<T> searchRequest, Pageable pageable) {
        validateNotNull(searchRequest, "Criteria search request" + CAN_NOT_BE_NULL);
        log.debug("Searching vehicles by criteria: {}", searchRequest.getCriteriaName());
        return criteriaSearch.findVehiclesByCriteria(searchRequest, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findAvailableVehicles(RentDuration duration, Pageable pageable) {
        validateNotNull(duration, "Duration" + CAN_NOT_BE_NULL);
        log.debug("Finding available vehicles from: {} to {}", duration.getRentalStart(), duration.getRentalEnd());
        return vehiclesRetriever.findVehiclesAvailableInPeriod(duration, pageable);
    }

    private void validateNotNull(Object o, String message) {
        inputValidator.throwExceptionIfObjectIsNull(o, message);
    }

    private Vehicle findVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Vehicle with id: {} not found.", id);
                    return new NoSuchElementException(
                            "Vehicle with id: " + id + " does not exist.");
                });
    }

    private Vehicle validateAndUpdateVehicle(Vehicle vehicleFromDb, Vehicle newData) {
        validateVehicleBeforeUpdate(vehicleFromDb, newData);
        return getStrategyAndUpdate(vehicleFromDb, newData);
    }

    private void validateVehicleBeforeUpdate(Vehicle vehicleFromDb, Vehicle newData) {
        log.debug("Validating vehicle before update. vehicleFromDb id: {}, newData registration: {}",
                vehicleFromDb.getId(), newData.getRegistrationNumber());
        vehicleValidator.checkIfVehiclesHasSameTypes(vehicleFromDb, newData);
        vehicleValidator.checkIfVehicleCanBeUpdated(vehicleFromDb.getRegistrationNumber(), newData);
    }

    private Vehicle getStrategyAndUpdate(Vehicle vehicleFromDb, Vehicle newData) {
        VehicleUpdateStrategy strategy = strategyFactory.getStrategy(vehicleFromDb.getClass());
        Vehicle updated = strategy.update(vehicleFromDb, newData);
        log.info("Saving updated vehicle to database. Vehicle id: {}", updated.getId());
        Vehicle saved = vehicleRepository.save(updated);
        log.info("Vehicle with id: {} has been updated.", saved.getId());
        return saved;
    }

    private void handleDeleteVehicle(Long id) {
        Vehicle vehicle = findVehicle(id);
        if (vehicle.canBeDeleted()) {
            log.info("Deleting vehicle with id: {}", id);
            deleteVehicleFromAllTablesInDb(vehicle);
            log.info("Vehicle with id: {} has been deleted.", id);
        } else {
            log.info("Attempt to delete vehicle with id: {} failed. Status is not UNAVAILABLE", id);
            throw new IllegalStateException("Vehicle must be in status UNAVAILABLE before it can be deleted.");
        }
    }

    private void deleteVehicleFromAllTablesInDb(Vehicle v) {
        Long id = v.getId();
        try {
            deleteVehicleData(id);
            vehicleRepository.delete(v);
        } catch (Exception e) {
            log.error("Exception while deleting vehicle with id: {}", id, e);
            throw new VehicleDeletionException("Could not delete vehicle with id: " + id);
        }
    }

    private void deleteVehicleData(Long id) {
        vehicleRepository.removeVehicleFromReservedVehicles(id);
        vehicleRepository.removeVehicleFromRentedVehicles(id);
    }
}
