package com.vehicle.rental.zelezniak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vehicle.rental.zelezniak.common_value_objects.Money;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.common_value_objects.RentInformation;
import com.vehicle.rental.zelezniak.common_value_objects.location.Location;
import com.vehicle.rental.zelezniak.config.*;
import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.reservation.model.dto.ReservationCreationRequest;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.reservation.service.ReservationService;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
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
import java.util.List;

import static com.vehicle.rental.zelezniak.config.TestConstants.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = VehicleRentalApplication.class)
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
class ReservationControllerTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, NUMBER_OF_RESERVATIONS);
    private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;

    private static Reservation reservationWithId2;
    private static String adminToken;
    private static String userToken;

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
    private UserLogin login;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ReservationCreationRequest creationRequest;
    private Vehicle vehicleWithId6;

    @BeforeEach
    void setupDatabase() throws IOException {
        creationRequest = new ReservationCreationRequest(CLIENT_2_ID, durationCreator.createDuration2());
        databaseSetup.setupAllTables();
        reservationWithId2 = reservationCreator.createReservationWithId2();
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId2();
        if (adminToken == null && userToken == null) {
            adminToken = generateToken("admin@gmail.com", "admin1234");
            userToken = generateToken("usertwo@gmail.com", "somepass");
        }
    }

    @Test
    void shouldFindAllReservationsForRoleADMIN() throws Exception {
        ResultActions actions = mockMvc.perform(get("/reservations")
                .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                .param("size", String.valueOf(PAGEABLE.getPageSize()))
                .header("Authorization", "Bearer " + adminToken));
        performReservationExpectations(actions, NUMBER_OF_RESERVATIONS, reservationWithId2);
    }

    @Test
    void shouldNotFindReservationsForRoleUser() throws Exception {
        mockMvc.perform(get("/reservations")
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldFindReservationById() throws Exception {
        ResultActions actions = mockMvc.perform(get("/reservations/{id}", reservationWithId2.getId())
                .header("Authorization", "Bearer " + userToken));
        performReservationExpectations(actions, reservationWithId2);
    }

    @Test
    void shouldFindAllReservationsByClientId() throws Exception {
        Long clientId = CLIENT_2_ID;

        ResultActions actions = mockMvc.perform(get("/reservations/client/{id}", clientId)
                .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                .param("size", String.valueOf(PAGEABLE.getPageSize()))
                .header("Authorization", "Bearer " + userToken));
        performReservationExpectations(actions, NUMBER_OF_CLIENT_2_RESERVATIONS, reservationWithId2);
    }

    @Test
    void shouldFindVehiclesByReservationId() throws Exception {
        Long reservationId = TestConstants.RESERVATION_3_ID;
        Long vehicleId = TestConstants.VEHICLE_3_ID;

        mockMvc.perform(get("/reservations/vehicles/from_reservation/{id}", reservationId)
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(jsonPath("$.content", hasSize(VEHICLES_IN_RESERVATION_3)))
                .andExpect(jsonPath("$.content[0].id").value(vehicleWithId6.getId()))
                .andExpect(jsonPath("$.content[1].id").value(vehicleId));
    }

    @Test
    void shouldReturnEmptyPageOfVehiclesWhenReservationDoesNotExist() throws Exception {
        Long reservationId = 90L;

        mockMvc.perform(get("/reservations/vehicles/from_reservation/{id}", reservationId)
                        .param("page", String.valueOf(PAGEABLE.getPageNumber()))
                        .param("size", String.valueOf(PAGEABLE.getPageSize()))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void shouldAddNewReservationForClientWhenRequestCorrect() throws Exception {
        Reservation newReservation = reservationCreator.buildNewReservation();
        RentInformation information = newReservation.getRentInformation();
        RentDuration rentDuration = information.getRentDuration();

        List<Reservation> reservations = reservationRepository.findAll();
        assertEquals(NUMBER_OF_RESERVATIONS, reservations.size());

        mockMvc.perform(post("/reservations")
                        .content(mapper.writeValueAsString(creationRequest))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.reservationStatus").value(newReservation.getReservationStatus().toString()))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalStart").value(rentDuration.getRentalStart().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalEnd").value(rentDuration.getRentalEnd().format(formatter)));

        reservations = reservationRepository.findAll();
        assertEquals(NUMBER_OF_RESERVATIONS + 1, reservations.size());
    }

    @SuppressWarnings("UnreachableCode")
    @Test
    void shouldNotAddReservationWhenRequestIsInvalid() throws Exception {
        Long invalidId = 0L;
        ReservationCreationRequest request = new ReservationCreationRequest(invalidId, durationCreator.createDuration2());

        mockMvc.perform(post("/reservations")
                        .content(mapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldValidationErrors").value(
                        containsInAnyOrder("Client id can not be lower than 1")));
    }

    /**
     * Create new reservation , then update location
     */
    @Test
    void shouldUpdateNewReservationLocationWhenStatusCorrect() throws Exception {
        Reservation reservation = reservationService.addReservation(creationRequest);
        updateLocation(reservation);
        Long id = reservation.getId();
        RentInformation information = reservation.getRentInformation();
        RentDuration rentDuration = information.getRentDuration();
        Location pickUpLocation = information.getPickUpLocation();

        mockMvc.perform(put("/reservations/{id}/location", id)
                        .content(mapper.writeValueAsString(information))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(jsonPath("$.reservationStatus").value(reservation.getReservationStatus().toString()))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalStart").value(rentDuration.getRentalStart().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.rentDuration.rentalEnd").value(rentDuration.getRentalEnd().format(formatter)))
                .andExpect(jsonPath("$.rentInformation.pickUpLocation.city.cityName").value(pickUpLocation.getCity().cityName()))
                .andExpect(jsonPath("$.rentInformation.pickUpLocation.street.streetName").value(pickUpLocation.getStreet().streetName()));

        assertEquals(reservation, findReservationById(id));
    }

    @Test
    void shouldNotUpdateLocationWhenStatusIncorrect() throws Exception {
        Reservation newData = reservationWithId2;
        updateLocation(newData);

        mockMvc.perform(put("/reservations/{id}/location", reservationWithId2.getId())
                        .content(mapper.writeValueAsString(newData.getRentInformation()))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not update reservation with status: "
                        + newData.getReservationStatus()));
    }

    @Test
    void shouldUpdateDurationWhenStatusCorrect() throws Exception {
        Long id = reservationWithId2.getId();
        setReservationStatusToNew(reservationWithId2);
        RentDuration duration = durationCreator.createDuration2();
        updateRentDuration(reservationWithId2, duration);

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        ResultActions actions = mockMvc.perform(put("/reservations/{id}/duration", id)
                .content(mapper.writeValueAsString(duration))
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer " + userToken));
        performReservationExpectations(actions, reservationWithId2);

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2 - 1);
        assertEquals(reservationWithId2, findReservationById(id));
    }

    @Test
    void shouldNotUpdateReservationWhenStatusIncorrect() throws Exception {
        Long id = reservationWithId2.getId();
        RentDuration duration = durationCreator.createDuration2();
        updateRentDuration(reservationWithId2, duration);

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        mockMvc.perform(put("/reservations/{id}/duration", id)
                        .content(mapper.writeValueAsString(duration))
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not update duration for reservation with status: "
                        + reservationWithId2.getReservationStatus()));

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);
    }

    @Test
    void shouldDeleteReservationWhenStatusCorrect() throws Exception {
        setReservationStatusToNew(reservationWithId2);
        Long clientId = CLIENT_2_ID;
        Long reservationId = reservationWithId2.getId();

        findReservationsByClientIdAndAssertSize(clientId, NUMBER_OF_CLIENT_2_RESERVATIONS);

        mockMvc.perform(delete("/reservations/{id}", reservationId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());

        findReservationsByClientIdAndAssertSize(clientId, NUMBER_OF_CLIENT_2_RESERVATIONS - 1);

        for (Reservation reservation : reservationRepository.findAll()) {
            assertNotEquals(reservationWithId2, reservation);
        }
    }

    @Test
    void shouldNotDeleteReservationWhenStatusIncorrect() throws Exception {
        Long id = reservationWithId2.getId();

        mockMvc.perform(delete("/reservations/{id}", id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not remove reservation with status: " +
                        reservationWithId2.getReservationStatus()));
    }

    @Test
    void shouldAddVehicleToReservationWhenStatusCorrect() throws Exception {
        setReservationStatusToNew(reservationWithId2);
        Long reservationId = reservationWithId2.getId();
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId2();
        Long vehicleId = vehicleWithId6.getId();

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        mockMvc.perform(put("/reservations/{reservationId}/vehicle/{vehicleId}", reservationId, vehicleId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2 + 1);
    }

    @Test
    void shouldNotAddVehicleToReservationWhenStatusIncorrect() throws Exception {
        vehicleWithId6 = vehicleCreator.createMotorcycleWithId2();
        Long reservationId = reservationWithId2.getId();
        Long vehicleId = vehicleWithId6.getId();

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        mockMvc.perform(put("/reservations/{reservationId}/vehicle/{vehicleId}", reservationId, vehicleId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not add vehicle to reservation with status: " +
                        reservationWithId2.getReservationStatus()));

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);
    }

    @Test
    void shouldRemoveVehicleFromReservationWhenStatusCorrect() throws Exception {
        setReservationStatusToNew(reservationWithId2);
        Long reservationId = reservationWithId2.getId();

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        mockMvc.perform(delete("/reservations/{reservationId}/vehicle/{vehicleId}", reservationId, VEHICLE_1_ID)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2 - 1);
    }

    @Test
    void shouldNotRemoveVehicleFromReservationWhenStatusIncorrect() throws Exception {
        Long reservationId = reservationWithId2.getId();

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);

        mockMvc.perform(delete("/reservations/{reservationId}/vehicle/{vehicleId}", reservationId, VEHICLE_5_ID)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Can not remove vehicle from reservation with status: " +
                        reservationWithId2.getReservationStatus()));

        findVehiclesByReservationIdAssertSize(reservationWithId2.getId(), VEHICLES_IN_RESERVATION_2);
    }

    @Test
    void shouldCalculateTotalCostWhenStatusCorrect() throws Exception {
        Long id = reservationWithId2.getId();
        Money totalCost = reservationWithId2.getTotalCost();
        reservationWithId2.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationWithId2.setTotalCost(null);
        reservationWithId2.setDepositAmount(null);
        reservationRepository.save(reservationWithId2);

        mockMvc.perform(get("/reservations/calculate/cost/{id}", id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(jsonPath("$.value").value(getValueFromMoney(totalCost)));
    }

    @Test
    void shouldNotCalculateTotalCostWhenStatusIncorrect() throws Exception {
        Long id = reservationWithId2.getId();

        mockMvc.perform(get("/reservations/calculate/cost/{id}", id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "When calculating the total cost, the reservation status should be NEW"));
    }

    private void performReservationExpectations(ResultActions actions, int size, Reservation expected) throws Exception {
        RentInformation rentInformation = expected.getRentInformation();
        RentDuration rentDuration = rentInformation.getRentDuration();
        Location pickUpLocation = rentInformation.getPickUpLocation();
        Location dropOffLocation = rentInformation.getDropOffLocation();

        String pickUpCity = pickUpLocation.getCity().cityName();
        String pickUpStreet = pickUpLocation.getStreet().streetName();
        String pickUpAdditionalInfo = pickUpLocation.getAdditionalInformation();
        String dropOffCity = dropOffLocation.getCity().cityName();
        String dropOffStreet = dropOffLocation.getStreet().streetName();
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

    private String generateToken(String email, String password) {
        return login.loginUser(email, password);
    }

    private double getValueFromMoney(Money totalCost) {
        BigDecimal value = totalCost.value();
        return value.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void performReservationExpectations(ResultActions actions, Reservation reservation) throws Exception {
        RentInformation rentInformation = reservation.getRentInformation();
        RentDuration rentDuration = rentInformation.getRentDuration();
        Location pickUpLocation = rentInformation.getPickUpLocation();
        Location dropOffLocation = rentInformation.getDropOffLocation();

        String pickUpCity = pickUpLocation.getCity().cityName();
        String pickUpStreet = pickUpLocation.getStreet().streetName();
        String pickUpAdditionalInfo = pickUpLocation.getAdditionalInformation();
        String dropOffCity = dropOffLocation.getCity().cityName();
        String dropOffStreet = dropOffLocation.getStreet().streetName();
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
        reservationWithId2.setRentInformation(information.toBuilder()
                .rentDuration(duration)
                .build());
    }

    private void setReservationStatusToNew(Reservation reservation) {
        reservation.setReservationStatus(Reservation.ReservationStatus.NEW);
        reservationRepository.save(reservation);
    }

    private void findReservationsByClientIdAndAssertSize(Long clientId, int expectedSize) {
        Page<Reservation> page = reservationRepository.findAllReservationsByClientId(clientId, PAGEABLE);
        List<Reservation> content = page.getContent();
        assertEquals(expectedSize, content.size());
    }

    private void findVehiclesByReservationIdAssertSize(Long reservationId, int expectedSize) {
        Page<Vehicle> page = reservationRepository.findVehiclesByReservationId(reservationId, PAGEABLE);
        List<Vehicle> content = page.getContent();
        assertEquals(expectedSize, content.size());
    }

    private Reservation findReservationById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
