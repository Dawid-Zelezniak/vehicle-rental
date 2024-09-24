package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.user.model.client.Client;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Car;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Motorcycle;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestBeans {

    @Bean
    @Scope(scopeName = "prototype")
    public Client createAppUser(){
        return new Client();
    }

    @Bean(name = "car")
    @Scope(scopeName = "prototype")
    public Vehicle createCar(){
        return new Car();
    }

    @Bean(name = "motorcycle")
    @Scope(scopeName = "prototype")
    public Vehicle createMotorcycle(){
        return new Motorcycle();
    }
}
