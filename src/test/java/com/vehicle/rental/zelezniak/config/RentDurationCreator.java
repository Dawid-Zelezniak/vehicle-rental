package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RentDurationCreator {

    public RentDuration createDuration1(){
        return RentDuration.builder()
                .rentalStart(LocalDateTime.of(2024, 7, 21, 10, 0, 0))
                .rentalEnd(LocalDateTime.of(2024, 7, 26, 10, 0, 0))
                .build();
    }

    public RentDuration createDuration2(){
        return RentDuration.builder()
                .rentalStart(LocalDateTime.of(2024, 7, 24, 10, 0, 0))
                .rentalEnd(LocalDateTime.of(2024, 7, 28, 10, 0, 0))
                .build();
    }

    public RentDuration createDuration3(){
        return RentDuration.builder()
                .rentalStart(LocalDateTime.of(2024, 7, 21, 10, 0, 0))
                .rentalEnd(LocalDateTime.of(2024, 7, 24, 10, 0, 0))
                .build();
    }
}
