package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RentDurationTest {

    @Test
    void shouldNotCreateRentDurationForInvalidData() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 21, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 7, 26, 10, 0, 0);
        IllegalArgumentException assertion = assertThrows(IllegalArgumentException.class, () ->
                new RentDuration(end, start));
        assertEquals("Rental start can not be after rental end.", assertion.getMessage());
    }
}
