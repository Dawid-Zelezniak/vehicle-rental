package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.Location;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation_domain.model.Reservation;
import com.vehicle.rental.zelezniak.reservation_domain.model.util.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation_domain.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation_domain.service.ReservationService;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class ReservationControllerTest {

    private static Reservation reservationWithId5;
    private static final Pageable pageable = PageRequest.of(0, 5);
    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private DatabaseSetup databaseSetup;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReservationCreator reservationCreator;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private VehicleCreator vehicleCreator;
    @Autowired
    private RentDurationCreator durationCreator;
    @Autowired
    private LocationCreator locationCreator;
    @Autowired
    private TokenGenerator tokenGenerator;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ReservationCreationRequest creationRequest;
    private Vehicle vehicleWithId6;

    @BeforeEach
    void setupDatabase() throws IOException {
        creationRequest = new ReservationCreationRequest(5L, durationCreator.createDuration2());
        databaseSetup.setupAllTables();
        reservationWithId5 = reservationCreator.createReservationWithId5();
    }

    @AfterEach
    void cleanupDatabase() {
        databaseSetup.dropAllTables();
    }

    @Test
    void shouldFindAllReservationsForRoleADMIN() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_ADMIN);

        ResultActions actions = mockMvc.perform(get("/reservations/")
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .header("Authorization", "Bearer " + token));
        performExpectations(actions, 5, reservationWithId5);
    }

    @Test
    void shouldNotFindReservationsForRoleUser() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);

        mockMvc.perform(get("/reservations/")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindReservationById() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);

        ResultActions actions = mockMvc.perform(get("/reservations/{id}", reservationWithId5.getId())
                .header("Authorization", "Bearer " + token));
        performExpectations(actions, reservationWithId5);
    }

    @Test
    void shouldFindAllReservationsByClientId() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long clientId = 5L;

        ResultActions actions = mockMvc.perform(get("/reservations/client/{id}", clientId)
                .param("page", String.valueOf(pageable.getPageNumber()))
                .param("size", String.valueOf(pageable.getPageSize()))
                .header("Authorization", "Bearer " + token));
        performExpectations(actions, 2, reservationWithId5);
    }

    @Test
    void shouldAddNewReservationForClient() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Reservation newReservation = reservationCreator.buildNewReservation();
        RentInformation information = newReservation.getRentInformation();
        RentDuration rentDuration = information.getRentDuration();

        List<Reservation> reservations = reservationRepository.findAll();
        assertEquals(5, reservations.size());

        mockMvc.perform(post("/reservations/create")
                        .content(mapper.writeValueAsString(creationRequest))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationStatus").value(newReservation.getReservationStatus().toString()))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalStart").value(rentDuration.getRentalStart().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalEnd").value(rentDuration.getRentalEnd().format(formatter)));

        reservations = reservationRepository.findAll();
        assertEquals(6, reservations.size());
    }

    @Test
    void shouldUpdateNewReservationLocation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Reservation reservation = reservationService.addReservation(creationRequest);
        updateLocation(reservation);
        Long id = reservation.getId();
        RentInformation information = reservation.getRentInformation();
        RentDuration rentDuration = information.getRentDuration();
        Location pickUpLocation = information.getPickUpLocation();

        mockMvc.perform(put("/reservations/update/location/{id}", id)
                        .content(mapper.writeValueAsString(reservation))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.reservationStatus").value(reservation.getReservationStatus().toString()))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalStart").value(rentDuration.getRentalStart().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalEnd").value(rentDuration.getRentalEnd().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.pickUpLocation.city.cityName").value(pickUpLocation.getCity().getCityName()))
                .andExpect(jsonPath("$.rentInformation.pickUpLocation.street.streetName").value(pickUpLocation.getStreet().getStreetName()));

        assertEquals(reservation, findReservationById(id));
    }

    @Test
    void shouldUpdateLocation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        setReservationStatusToNew(reservationWithId5);
        Long id = reservationWithId5.getId();
        updateLocation(reservationWithId5);

        ResultActions actions = mockMvc.perform(put("/reservations/update/location/{id}", id)
                .content(mapper.writeValueAsString(reservationWithId5))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token));
        performExpectations(actions,reservationWithId5);

        assertEquals(reservationWithId5, findReservationById(id));
    }

    @Test
    void shouldNotUpdateLocation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Reservation newData = reservationWithId5;
        updateLocation(newData);

        mockMvc.perform(put("/reservations/update/location/{id}", reservationWithId5.getId())
                        .content(mapper.writeValueAsString(newData))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not update reservation with status: "
                        + reservationWithId5.getReservationStatus()));
    }

    @Test
    void shouldUpdateDuration() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long id = reservationWithId5.getId();
        setReservationStatusToNew(reservationWithId5);
        RentDuration duration = durationCreator.createDuration2();
        updateRentDuration(reservationWithId5, duration);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(id);
        assertEquals(1, vehicles.size());

        ResultActions actions = mockMvc.perform(put("/reservations/update/duration/{id}", id)
                .content(mapper.writeValueAsString(duration))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token));
        performExpectations(actions,reservationWithId5);

        vehicles = reservationRepository.findVehiclesByReservationId(id);
        assertEquals(0, vehicles.size());
        assertEquals(reservationWithId5, findReservationById(id));
    }

    @Test
    void shouldNotUpdateDuration() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long id = reservationWithId5.getId();
        RentDuration duration = durationCreator.createDuration2();
        updateRentDuration(reservationWithId5, duration);

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(id);
        assertEquals(1, vehicles.size());

        mockMvc.perform(put("/reservations/update/duration/{id}", id)
                        .content(mapper.writeValueAsString(duration))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not update duration for reservation with status: "
                        + reservationWithId5.getReservationStatus()));

        vehicles = reservationRepository.findVehiclesByReservationId(id);
        assertEquals(1, vehicles.size());
    }

    @Test
    void shouldDeleteReservation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        setReservationStatusToNew(reservationWithId5);
        Long client5Id = 5L;
        Long id = reservationWithId5.getId();

        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(client5Id, pageable);
        List<Reservation> allByClientId = page.getContent();
        assertEquals(2, allByClientId.size());

        mockMvc.perform(delete("/reservations/delete/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        page = reservationRepository.findAllReservationsByClientId(client5Id, pageable);
        allByClientId = page.getContent();
        assertEquals(1, allByClientId.size());

        for (Reservation reservation : reservationRepository.findAll()) {
            assertNotEquals(reservationWithId5, reservation);
        }
    }

    @Test
    void shouldNotDeleteReservation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long id = reservationWithId5.getId();

        mockMvc.perform(delete("/reservations/delete/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not remove reservation with status: " +
                        reservationWithId5.getReservationStatus()));
    }

    @Test
    void shouldAddVehicleToReservation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        setReservationStatusToNew(reservationWithId5);
        Long reservationId = reservationWithId5.getId();
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
        Long vehicleId = vehicleWithId6.getId();

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(1, vehicles.size());

        mockMvc.perform(put("/reservations/add/vehicle/{reservationId}/{vehicleId}", reservationId, vehicleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(2, vehicles.size());
    }

    @Test
    void shouldNotAddVehicleToReservation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId6();
        Long reservationId = reservationWithId5.getId();
        Long vehicleId = vehicleWithId6.getId();

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(1, vehicles.size());

        mockMvc.perform(put("/reservations/add/vehicle/{reservationId}/{vehicleId}", reservationId, vehicleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not add vehicle to reservation with status: " +
                        reservationWithId5.getReservationStatus()));

        vehicles = reservationRepository.findVehiclesByReservationId(reservationId);
        assertEquals(1, vehicles.size());
    }

    @Test
    void shouldRemoveVehicleFromReservation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        setReservationStatusToNew(reservationWithId5);
        Long vehicleId = 5L;
        Long reservationId = reservationWithId5.getId();

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(1, vehicles.size());

        mockMvc.perform(put("/reservations/delete/vehicle/{reservationId}/{vehicleId}", reservationId, vehicleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(0, vehicles.size());
    }

    @Test
    void shouldNotRemoveVehicleFromReservation() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long vehicleId = 5L;
        Long reservationId = reservationWithId5.getId();

        Collection<Vehicle> vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(1, vehicles.size());

        mockMvc.perform(put("/reservations/delete/vehicle/{reservationId}/{vehicleId}", reservationId, vehicleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not remove vehicle from reservation with status: " +
                        reservationWithId5.getReservationStatus()));

        vehicles = reservationRepository.findVehiclesByReservationId(reservationWithId5.getId());
        assertEquals(1, vehicles.size());
    }

    @Test
    void shouldCalculateTotalCost() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long id = reservationWithId5.getId();
        Money totalCost = reservationWithId5.getTotalCost();
        Money deposit = reservationWithId5.getDepositAmount();
        reservationWithId5.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationWithId5.setTotalCost(null);
        reservationWithId5.setDepositAmount(null);
        reservationRepository.save(reservationWithId5);

        mockMvc.perform(get("/reservations/calculate/cost/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.totalCost.value").value(getValueFromMoney(totalCost)))
                .andExpect(jsonPath("$.depositAmount.value").value(getValueFromMoney(deposit)));
    }

    @Test
    void shouldNotCalculateTotalCost() throws Exception {
        String token = tokenGenerator.generateToken(ROLE_USER);
        Long id = reservationWithId5.getId();

        mockMvc.perform(get("/reservations/calculate/cost/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("When calculating the total cost, the reservation status should be NEW"));
    }

    private void performExpectations(ResultActions actions, int size, Reservation expected) throws Exception {
        RentInformation rentInformation = expected.getRentInformation();
        RentDuration rentDuration = rentInformation.getRentDuration();
        Location pickUpLocation = rentInformation.getPickUpLocation();
        Location dropOffLocation = rentInformation.getDropOffLocation();

        String pickUpCity = pickUpLocation.getCity().getCityName();
        String pickUpStreet = pickUpLocation.getStreet().getStreetName();
        String pickUpAdditionalInfo = pickUpLocation.getAdditionalInformation();
        String dropOffCity = dropOffLocation.getCity().getCityName();
        String dropOffStreet = dropOffLocation.getStreet().getStreetName();
        String dropOffAdditionalInfo = dropOffLocation.getAdditionalInformation();

        actions.andExpect(jsonPath("$.content", hasSize(size)))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.content[1].id").value(expected.getId()))
                .andExpect(jsonPath("$.content[1].reservationStatus").value(expected.getReservationStatus().toString()))
                .andExpect(jsonPath("$.content[1].rentInformation.rentDuration.rentalStart").value(rentDuration.getRentalStart().format(formatter)))
                .andExpect(jsonPath("$.content[1].rentInformation.rentDuration.rentalEnd").value(rentDuration.getRentalEnd().format(formatter)))
                .andExpect(jsonPath("$.content[1].rentInformation.pickUpLocation.city.cityName").value(pickUpCity))
                .andExpect(jsonPath("$.content[1].rentInformation.pickUpLocation.street.streetName").value(pickUpStreet))
                .andExpect(jsonPath("$.content[1].rentInformation.pickUpLocation.additionalInformation").value(pickUpAdditionalInfo))
                .andExpect(jsonPath("$.content[1].rentInformation.dropOffLocation.city.cityName").value(dropOffCity))
                .andExpect(jsonPath("$.content[1].rentInformation.dropOffLocation.street.streetName").value(dropOffStreet))
                .andExpect(jsonPath("$.content[1].rentInformation.dropOffLocation.additionalInformation").value(dropOffAdditionalInfo))
                .andExpect(jsonPath("$.content[1].totalCost.value").value(getValueFromMoney(expected.getTotalCost())))
                .andExpect(jsonPath("$.content[1].depositAmount.value").value(getValueFromMoney(expected.getDepositAmount())));
    }

    private double getValueFromMoney(Money totalCost) {
        BigDecimal value = totalCost.getValue();
        return value.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void performExpectations(ResultActions actions, Reservation reservation) throws Exception {
        RentInformation rentInformation = reservation.getRentInformation();
        RentDuration rentDuration = rentInformation.getRentDuration();
        Location pickUpLocation = rentInformation.getPickUpLocation();
        Location dropOffLocation = rentInformation.getDropOffLocation();

        String pickUpCity = pickUpLocation.getCity().getCityName();
        String pickUpStreet = pickUpLocation.getStreet().getStreetName();
        String pickUpAdditionalInfo = pickUpLocation.getAdditionalInformation();
        String dropOffCity = dropOffLocation.getCity().getCityName();
        String dropOffStreet = dropOffLocation.getStreet().getStreetName();
        String dropOffAdditionalInfo = dropOffLocation.getAdditionalInformation();

        actions.andExpect(jsonPath("$.id").value(reservation.getId()))
                .andExpect(jsonPath("$.reservationStatus").value(reservation.getReservationStatus().toString()))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalStart").value(rentDuration.getRentalStart().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalEnd").value(rentDuration.getRentalEnd().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.pickUpLocation.city.cityName").value(pickUpCity))
                .andExpect(jsonPath("$.rentInformation.pickUpLocation.street.streetName").value(pickUpStreet))
                .andExpect(jsonPath("$.rentInformation.pickUpLocation.additionalInformation").value(pickUpAdditionalInfo))
                .andExpect(jsonPath("$.rentInformation.dropOffLocation.city.cityName").value(dropOffCity))
                .andExpect(jsonPath("$.rentInformation.dropOffLocation.street.streetName").value(dropOffStreet))
                .andExpect(jsonPath("$.rentInformation.dropOffLocation.additionalInformation").value(dropOffAdditionalInfo))
                .andExpect(jsonPath("$.totalCost.value").value(getValueFromMoney(reservation.getTotalCost())))
                .andExpect(jsonPath("$.depositAmount.value").value(getValueFromMoney(reservation.getDepositAmount())));
    }

    private void updateLocation(Reservation reservation) {
        RentInformation rentInformation = reservation.getRentInformation();
        reservation.setRentInformation(rentInformation.toBuilder()
                .pickUpLocation(locationCreator.buildTestLocation())
                .build());
    }

    private void updateRentDuration(Reservation reservation, RentDuration duration) {
        RentInformation information = reservation.getRentInformation();
        reservationWithId5.setRentInformation(information.toBuilder()
                .rentDuration(duration)
                .build());
    }

    private void setReservationStatusToNew(Reservation reservation) {
        reservation.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationRepository.save(reservation);
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
