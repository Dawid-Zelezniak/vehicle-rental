package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.common_value_objects.location.Location;
import com.vehicle.rental.zelezniak.common_value_objects.location.City;
import com.vehicle.rental.zelezniak.common_value_objects.location.Street;
import org.springframework.stereotype.Component;

@Component
public class LocationCreator {

    public Location buildTestLocation() {
        return Location.builder()
                .city(new City("TestCity"))
                .street(new Street("TestStreet"))
                .additionalInformation("Test information")
                .build();
    }
}
