package com.vehicle.rental.zelezniak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Component
public class DatabaseSetup {

    private static final String DROP = "DROP ALL OBJECTS";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void setupAllTables() throws IOException {
        String schemaSql = new String(Files.readAllBytes(Paths.get("src/test/resources/schema.sql")));
        String dataSql = new String(Files.readAllBytes(Paths.get("src/test/resources/data.sql")));
        executeQueries(schemaSql,dataSql);
    }

    public void dropAllTables(){
        executeQueries(DROP);
    }

    private void executeQueries(String... queries) {
        Arrays.stream(queries)
                .forEach(jdbcTemplate::execute);
    }
}
