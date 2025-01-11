package com.vehicle.rental.zelezniak.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseSetup {

    private final JdbcTemplate jdbcTemplate;

    public void setupAllTables() {
        try {
            String schemaSql = new String(Files.readAllBytes(Paths.get("src/test/resources/schema.sql")));
            String dataSql = new String(Files.readAllBytes(Paths.get("src/test/resources/testData.sql")));
            executeQueries(schemaSql, dataSql);
        } catch (IOException e) {
            log.error("Exception while reading sql files: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void executeQueries(String... queries) {
        Arrays.stream(queries).forEach(jdbcTemplate::execute);
    }
}
