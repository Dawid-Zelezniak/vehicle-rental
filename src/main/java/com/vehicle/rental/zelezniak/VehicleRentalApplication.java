package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.user.repository.ClientRepository;
import com.vehicle.rental.zelezniak.user.service.AdminInitializationService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class VehicleRentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleRentalApplication.class, args);
    }

    @Bean
//    @Profile("!test")
    public CommandLineRunner createAdmin(AdminInitializationService service, ClientRepository repository, JdbcTemplate jdbc) {
        return arg -> {
            String[] queries = new String(Files.readAllBytes(Paths.get("src/main/resources/data.sql"))).split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    jdbc.execute(query);
                }
            }
            service.createAdmin();
        };
    }
}
