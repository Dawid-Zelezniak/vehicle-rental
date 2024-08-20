package com.vehicle.rental.zelezniak.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static LocalDateTime getFormattedActualDateTime(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN);
        LocalDateTime actualDate = LocalDateTime.now();
        String formattedDate = actualDate.format(dateTimeFormatter);
        return LocalDateTime.parse(formattedDate,dateTimeFormatter);
    }
}
