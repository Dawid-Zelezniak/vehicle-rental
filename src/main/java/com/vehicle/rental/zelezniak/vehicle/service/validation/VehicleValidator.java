package com.vehicle.rental.zelezniak.vehicle.service;

import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VehicleValidator {

    private final VehicleRepository vehicleRepository;

    public void ensureVehicleDoesNotExist(RegistrationNumber registrationNumber) {
        if (registrationAlreadyAssigned(registrationNumber)) {
            createMessageAndThrowException(registrationNumber);
        }
    }

    public void validateVehicleTypeConsistency(Vehicle vehicleFromDb, Vehicle newData) {
        if (vehicleTypesAreDifferent(vehicleFromDb, newData)) {
            throwException("Provided data does not match the vehicle type.");
        }
    }

    /**
     * Vehicle can not be updated when new vehicle data contains registration number
     * which exists in database (is assigned to other vehicle)
     */
    public void validateVehicleUpdate(RegistrationNumber registrationNumber, Vehicle newData) {
        RegistrationNumber newDataRegistrationNumber = newData.getRegistrationNumber();
        if (registrationsDifferent(registrationNumber, newDataRegistrationNumber) &&
                registrationAlreadyAssigned(newDataRegistrationNumber)) {
            createMessageAndThrowException(newDataRegistrationNumber);
        }
    }

    private boolean registrationAlreadyAssigned(RegistrationNumber newDataRegistrationNumber) {
        return vehicleRepository.existsByVehicleInformationRegistrationNumber(newDataRegistrationNumber);
    }

    private void createMessageAndThrowException(RegistrationNumber n) {
        throwException("Vehicle with registration number : " + n.getRegistration() + " already exist.");
    }

    private void throwException(String message) {
        log.error("Exception thrown : {}", message);
        throw new IllegalArgumentException(message);
    }

    private boolean vehicleTypesAreDifferent(Vehicle vehicleFromDb, Vehicle newData) {
        return !vehicleFromDb.getClass().equals(newData.getClass());
    }

    private boolean registrationsDifferent(RegistrationNumber registrationNumber,
                                           RegistrationNumber newDataRegistrationNumber) {
        return !registrationNumber.equals(newDataRegistrationNumber);
    }
}
