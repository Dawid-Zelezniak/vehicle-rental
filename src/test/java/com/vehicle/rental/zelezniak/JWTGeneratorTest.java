package com.vehicle.rental.zelezniak;

import com.vehicle.rental.zelezniak.config.DatabaseSetup;
import com.vehicle.rental.zelezniak.config.UserLogin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JWTGeneratorTest {

    private final String FILE_PATH = "src/test/resources/expiredToken.txt";
    private final Pageable PAGEABLE = PageRequest.of(0, 5);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private UserLogin login;

    private String EXPIRED_TOKEN;

    @BeforeAll
    void setupData() {
        EXPIRED_TOKEN = readFile();
        databaseSetup.setupAllTables();
    }

    private String readFile() {
        var builder = new StringBuilder();
        try (var br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("File on path" + FILE_PATH + " not found.");
        }
        return builder.toString();
    }

    @Test
    void shouldTestTokenFormatIsCorrect() {
        String token = login.loginUser("usertwo@gmail.com", "somepass");
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
    }

    @Test
    void userWithExpiredTokenShouldNotHaveAccessToApplication() throws Exception {
        mockMvc.perform(get("/vehicles")
                        .header("Authorization", "Bearer " + EXPIRED_TOKEN)
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize())))
                .andExpect(status().isUnauthorized());
    }
}
