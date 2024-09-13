package com.vehicle.rental.zelezniak.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
@Slf4j
public class DatabaseSetup {

    private static final String DROP = "DROP ALL OBJECTS";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void setupAllTables() {
        try {
            String schemaSql = new String(Files.readAllBytes(Paths.get("src/test/resources/schema.sql")));
            String dataSql = new String(Files.readAllBytes(Paths.get("src/test/resources/data.sql")));
            executeQueries(schemaSql, dataSql);
        } catch (IOException e) {
            logInformation(e);
            throw new RuntimeException(e);
        }
    }

    private void logInformation(IOException e) {
        log.error("Exception while reading sql files: {}", e.getMessage());
    }

    public void dropAllTables() {
        executeQueries(DROP);
    }

    private void executeQueries(String... queries) {
        Arrays.stream(queries)
                .forEach(jdbcTemplate::execute);
    }
}
