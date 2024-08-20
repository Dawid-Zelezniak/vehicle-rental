package com.vehicle.rental.zelezniak.vehicle_domain.service;

import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleValidator {

    private final VehicleRepository vehicleRepository;

    public void throwExceptionIfVehicleExists(RegistrationNumber registrationNumber) {
        if (vehicleRepository.existsByVehicleInformationRegistrationNumber(registrationNumber)) {
            String message = createMessage(registrationNumber);
            throwException(message);
        }
    }

    public void checkIfVehicleCanBeUpdated(RegistrationNumber registrationNumber, Vehicle newData) {
        RegistrationNumber newDataRegistrationNumber = newData.getRegistrationNumber();
        if (registrationsAreNotSame(registrationNumber, newDataRegistrationNumber)
                && vehicleRegistrationNumberExists(newDataRegistrationNumber)) {
            String message = createMessage(newDataRegistrationNumber);
            throwException(message);
        }
    }

    public void checkIfVehiclesHasSameTypes(Vehicle vehicleFromDb, Vehicle newData) {
        if (typesAreDifferent(vehicleFromDb, newData)) {
            throwException("Provided data does not match the vehicle type.");
        }
    }

    private String createMessage(RegistrationNumber n) {
        return "Vehicle with registration number : " + n.getRegistration() + " already exists";
    }

    private void throwException(String message) {
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
