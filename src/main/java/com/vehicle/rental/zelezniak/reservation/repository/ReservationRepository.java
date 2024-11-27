package com.vehicle.rental.zelezniak.reservation.repository;

import com.vehicle.rental.zelezniak.reservation.model.Reservation;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.client.id = :id")
    Page<Reservation> findAllReservationsByClientId(Long id, Pageable pageable);

    @Query("SELECT v FROM Reservation r JOIN r.vehicles v WHERE r.id = :id")
    Collection<Vehicle> findVehiclesByReservationId(Long id);

    @Query("SELECT v.id FROM Reservation r JOIN r.vehicles v WHERE r.id = :id")
    Collection<Long> findVehiclesIdsByReservationId(Long id);

    @Query("SELECT v FROM Reservation r JOIN r.vehicles v WHERE r.id = :id")
    Page<Vehicle> findVehiclesByReservationId(Long id, Pageable pageable);

    @Query("SELECT v.id FROM Reservation r " +
            "JOIN r.vehicles v " +
            "WHERE r.rentInformation.rentDuration.rentalStart <= :end " +
            "AND r.rentInformation.rentDuration.rentalEnd >= :start " +
            "AND r.reservationStatus = 'ACTIVE'")
    Set<Long> unavailableVehicleIdsForReservationInPeriod(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM reserved_vehicles " +
            "WHERE reservation_id = :reservationId AND vehicle_id = :vehicleId")
    void deleteVehicleFromReservation(Long reservationId, Long vehicleId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM reserved_vehicles " +
            "WHERE vehicle_id IN (:vehicleIds)")
    void deleteVehiclesFromReservation(Collection<Long> vehicleIds);

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO reserved_vehicles " +
            "(reservation_id,vehicle_id) values (:reservationId,:vehicleId)")
    void addVehicleToReservation(Long vehicleId, Long reservationId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE reservations SET reservation_status = 'ACTIVE' WHERE id = :id")
    void updateReservationStatusAsActive(Long id);
}
