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

    public void throwExceptionIfVehicleExist(RegistrationNumber registrationNumber) {
        if (vehicleRepository.existsByVehicleInformationRegistrationNumber(registrationNumber)) {
            createMessageAndThrowException(registrationNumber);
        }
    }

    /**
     Vehicle can not be updated when new vehicle data contains registration number
     which exists in database (is assigned to other vehicle)
     */
    public void checkIfVehicleCanBeUpdated(RegistrationNumber registrationNumber, Vehicle newData) {
        RegistrationNumber newDataRegistrationNumber = newData.getRegistrationNumber();
        if (registrationsAreNotSame(registrationNumber, newDataRegistrationNumber)
                && vehicleRegistrationNumberExists(newDataRegistrationNumber)) {
            createMessageAndThrowException(newDataRegistrationNumber);
        }
    }

    public void checkIfVehiclesHasSameTypes(Vehicle vehicleFromDb, Vehicle newData) {
        if (typesAreDifferent(vehicleFromDb, newData)) {
            throwException("Provided data does not match the vehicle type.");
        }
    }

    private void createMessageAndThrowException(RegistrationNumber registrationNumber) {
        String message = createMessage(registrationNumber);
        throwException(message);
    }

    private String createMessage(RegistrationNumber n) {
        return "Vehicle with registration number : " + n.getRegistration() + " already exist.";
    }

    private void throwException(String message) {
        log.error("Exception thrown : {}", message);
        throw new IllegalArgumentException(message);
    }

    private boolean registrationsAreNotSame(
            RegistrationNumber registrationNumber,
            RegistrationNumber newDataRegistrationNumber) {
        return !registrationNumber.equals(newDataRegistrationNumber);
    }

    private boolean vehicleRegistrationNumberExists(RegistrationNumber newDataRegistrationNumber) {
        return vehicleRepository.existsByVehicleInformationRegistrationNumber(newDataRegistrationNumber);
    }

    private boolean typesAreDifferent(Vehicle vehicleFromDb, Vehicle newData) {
        return !vehicleFromDb.getClass().equals(newData.getClass());
    }
}
