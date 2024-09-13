package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.user_domain.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user_domain.service.AdminInitializationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class VehicleRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleRentalApplication.class, args);
    }

    @Bean
//    @Profile("!test")
    public CommandLineRunner createAdmin(AdminInitializationService service, ClientRepository repository) {
        return arg -> {
            service.createAdmin();
        };
    }
}
