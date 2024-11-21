package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.VehicleCreator;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.VehicleInformation;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.service.validation.VehicleValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleValidatorTest {

    private Vehicle vehicleWithId1;
    private Vehicle vehicleWithId2;

    @Autowired
    private VehicleValidator validator;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private DatabaseSetup databaseSetup;

    @BeforeAll
    void setupDatabase() {
        databaseSetup.setupAllTables();
        vehicleWithId1 = vehicleCreator.createCarWithId1();
        vehicleWithId2 = vehicleCreator.createMotorcycleWithId2();
    }

    @Test
    void shouldTestVehicleCanBeUpdated() {
        assertDoesNotThrow(() -> validator.validateVehicleUpdate(vehicleWithId1.getRegistrationNumber(), vehicleCreator.createTestCar()));
    }

    @Test
    void shouldTestVehicleCanNotBeUpdated() {
        RegistrationNumber existingRegistration = vehicleWithId2.getRegistrationNumber();
        Vehicle newCarData = vehicleCreator.createTestCar();
        VehicleInformation information = newCarData
                .getVehicleInformation()
                .toBuilder()
                .registrationNumber(existingRegistration)
                .build();
        newCarData.setVehicleInformation(information);
        RegistrationNumber registration = vehicleWithId1.getRegistrationNumber();

        assertThrows(IllegalArgumentException.class, () -> validator.validateVehicleUpdate(registration, newCarData));
    }

    @Test
    void shouldThrowExceptionIfVehicleExists() {
        RegistrationNumber registration = vehicleWithId1.getRegistrationNumber();

        assertThrows(IllegalArgumentException.class, () -> validator.ensureVehicleDoesNotExist(registration));
    }

    @Test
    void shouldTestVehicleTypesAreNotSame() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateVehicleTypeConsistency(vehicleWithId1, vehicleWithId2));
    }
}
