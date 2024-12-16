package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Engine;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.VehicleInformation;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Year;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Car;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Motorcycle;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
public class VehicleCreator {

    public Vehicle createCarWithId1() {
        Engine engine = buildCarEngine();
        var information = buildCarInformation(engine);
        return Car.builder()
                .id(1L)
                .vehicleInformation(information)
                .bodyType(Car.BodyType.HATCHBACK)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .driveType(Car.DriveType.FRONT_WHEEL_DRIVE)
                .pricePerDay(new Money(BigDecimal.valueOf(50.00)))
                .deposit(new Money(BigDecimal.valueOf(1000.00)))
                .doorsNumber(5)
                .build();
    }

    public Vehicle createMotorcycleWithId2() {
        Engine engine = buildMotorcycleEngine();
        var information = buildMotorcycleInformation(engine);
        return Motorcycle.builder()
                .id(2L)
                .vehicleInformation(information)
                .motorcycleType(Motorcycle.MotorcycleType.SPORT)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .pricePerDay(new Money(BigDecimal.valueOf(100.00)))
                .deposit(new Money(BigDecimal.valueOf(1500.00)))
                .build();
    }

    public Vehicle buildVehicle1WithDifferentData() {
        Engine engine = updateEngine();
        VehicleInformation information = updateInformation(engine);
        return Car.builder()
                .id(1L)
                .vehicleInformation(information)
                .bodyType(Car.BodyType.HATCHBACK)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .driveType(Car.DriveType.FOUR_WHEEL_DRIVE)
                .pricePerDay(new Money(BigDecimal.valueOf(150.00)))
                .deposit(new Money(BigDecimal.valueOf(1000.00)))
                .doorsNumber(5)
                .build();
    }

    public Vehicle createTestCar() {
        Engine engine = buildTestCarEngine();
        VehicleInformation information = buildTestCarInformation(engine);
        return Car.builder()
                .vehicleInformation(information)
                .bodyType(Car.BodyType.COUPE)
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .driveType(Car.DriveType.FOUR_WHEEL_DRIVE)
                .pricePerDay(new Money(BigDecimal.valueOf(1000.00)))
                .deposit(new Money(BigDecimal.valueOf(4000.00)))
                .doorsNumber(3)
                .build();
    }

    public Set<Vehicle> createSetWithVehicle5And6(){
        Set<Vehicle> vehicles = new HashSet<>();
        vehicles.add(createCarWithId1());
        vehicles.add(createMotorcycleWithId2());
        return vehicles;
    }

    private Engine buildCarEngine() {
        return Engine.builder()
                .engineType("1.9TDI AVG")
                .fuelType(Engine.FuelType.DIESEL)
                .cylinders(4)
                .displacement(1900)
                .horsepower(110)
                .build();
    }

    private VehicleInformation buildCarInformation(Engine engine) {
        return VehicleInformation.builder()
                .brand("Seat")
                .model("Leon 1M")
                .seatsNumber(5)
                .productionYear(new Year(2001))
                .registrationNumber(new RegistrationNumber("ABC55555"))
                .description("Seat Leon car")
                .engine(engine)
                .gearType(VehicleInformation.GearType.MANUAL)
                .build();
    }


    private Engine buildMotorcycleEngine() {
        return Engine.builder()
                .engineType("Minarelli-Yamaha 5D1E")
                .fuelType(Engine.FuelType.GASOLINE)
                .cylinders(1)
                .displacement(125)
                .horsepower(15)
                .build();
    }


    private VehicleInformation buildMotorcycleInformation(Engine engine) {
        return VehicleInformation.builder()
                .brand("Yamaha")
                .model("YZF-R125")
                .seatsNumber(2)
                .productionYear(new Year(2015))
                .registrationNumber(new RegistrationNumber("ABC66666"))
                .description("Legendary Yamaha 125")
                .engine(engine)
                .gearType(VehicleInformation.GearType.MANUAL)
                .build();
    }

    private Engine updateEngine() {
        return Engine.builder()
                .engineType("1.9TDI AVG")
                .fuelType(Engine.FuelType.DIESEL)
                .cylinders(4)
                .displacement(1900)
                .horsepower(150)
                .build();
    }

    private VehicleInformation updateInformation(Engine engine) {
        return VehicleInformation.builder()
                .brand("Seat")
                .model("Leon 1M")
                .productionYear(new Year(2001))
                .registrationNumber(new RegistrationNumber("ABC55555"))
                .description("Tuned Seat Leon")
                .engine(engine)
                .gearType(VehicleInformation.GearType.MANUAL)
                .seatsNumber(5)
                .build();
    }

    private Engine buildTestCarEngine() {
        return Engine.builder()
                .engineType("VR38DETT")
                .fuelType(Engine.FuelType.GASOLINE)
                .cylinders(6)
                .displacement(3800)
                .horsepower(565)
                .build();
    }

    private VehicleInformation buildTestCarInformation(Engine engine) {
        return VehicleInformation.builder()
                .brand("Nissan")
                .model("GT-R R35")
                .seatsNumber(5)
                .productionYear(new Year(2021))
                .registrationNumber(new RegistrationNumber("GTR54321"))
                .description("Nissan GT-R R35 high-performance sports car")
                .engine(engine)
                .gearType(VehicleInformation.GearType.AUTOMATIC)
                .build();
    }
}
