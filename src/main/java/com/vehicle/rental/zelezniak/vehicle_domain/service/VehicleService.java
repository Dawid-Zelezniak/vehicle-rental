package com.vehicle.rental.zelezniak.vehicle_domain.service;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.util.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle_domain.service.vehicle_update.VehicleUpdateStrategy;
import com.vehicle.rental.zelezniak.vehicle_domain.service.vehicle_update.VehicleUpdateStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleValidator vehicleValidator;
    private final InputValidator inputValidator;
    private final VehicleUpdateStrategyFactory strategyFactory;
    private final VehicleCriteriaSearch criteriaSearch;
    private final AvailableVehiclesRetriever vehiclesRetriever;

    @Transactional(readOnly = true)
    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        validateId(id);
        return findVehicle(id);
    }

    @Transactional
    public Vehicle addVehicle(Vehicle vehicle) {
        validateVehicle(vehicle);
        vehicleValidator.throwExceptionIfVehicleExists(vehicle.getRegistrationNumber());
       return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle update(Long id, Vehicle newData) {
        validateId(id);
        validateVehicle(newData);
        Vehicle vehicleFromDb = findVehicle(id);
        return validateAndUpdateVehicle(vehicleFromDb, newData);
    }

    @Transactional
    public void delete(Long id) {
        validateId(id);
        handleDeleteVehicle(id);
    }

    @Transactional(readOnly = true)
    public <T> Page<Vehicle> findByCriteria(CriteriaSearchRequest<T> searchRequest, Pageable pageable) {
        validateCriteriaSearchRequest(searchRequest);
        return criteriaSearch.findVehiclesByCriteria(searchRequest, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findAvailableVehicles(RentDuration duration, Pageable pageable) {
        validateDuration(duration);
        return vehiclesRetriever.findAvailableVehiclesInPeriod(duration, pageable);
    }

    private void validateId(Long id) {
        inputValidator.throwExceptionIfObjectIsNull(id, InputValidator.VEHICLE_ID_NOT_NULL);
    }

    private Vehicle findVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Vehicle with id: " + id + " does not exists."));
    }

    private void validateVehicle(Vehicle vehicle) {
        inputValidator.throwExceptionIfObjectIsNull(vehicle, InputValidator.VEHICLE_NOT_NULL);
    }

    private Vehicle validateAndUpdateVehicle(Vehicle vehicleFromDb, Vehicle newData) {
        vehicleValidator.checkIfVehiclesHasSameTypes(vehicleFromDb, newData);
        vehicleValidator.checkIfVehicleCanBeUpdated(vehicleFromDb.getRegistrationNumber(), newData);
        return pickUpStrategyAndUpdate(vehicleFromDb, newData);
    }

    private Vehicle pickUpStrategyAndUpdate(Vehicle vehicleFromDb, Vehicle newData) {
        VehicleUpdateStrategy strategy = strategyFactory.getStrategy(vehicleFromDb.getClass());
        Vehicle updated = strategy.update(vehicleFromDb, newData);
        return vehicleRepository.save(updated);
    }

    private void handleDeleteVehicle(Long id) {
        Vehicle vehicle = findVehicle(id);
        if (vehicle.getStatus() == Vehicle.Status.UNAVAILABLE) {
            deleteVehicleAndFromAllTablesInDb(vehicle);
        } else throw new IllegalStateException("Vehicle must be in status UNAVAILABLE before it can be deleted.");
    }

    private void deleteVehicleAndFromAllTablesInDb(Vehicle v) {
        vehicleRepository.removeVehicleFromReservedVehicles(v.getId());
        vehicleRepository.removeVehicleFromRentedVehicles(v.getId());
        vehicleRepository.delete(v);
    }

    private <T> void validateCriteriaSearchRequest(CriteriaSearchRequest<T> searchRequest) {
        inputValidator.throwExceptionIfObjectIsNull(searchRequest, "Criteria search request" + CAN_NOT_BE_NULL);
    }


    private void validateDuration(RentDuration duration) {
        inputValidator.throwExceptionIfObjectIsNull(duration, "Duration" + CAN_NOT_BE_NULL);
    }
}
