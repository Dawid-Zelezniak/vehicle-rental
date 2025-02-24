package com.vehicle.rental.zelezniak.rent.repository;

import com.vehicle.rental.zelezniak.rent.model.Rent;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Set;

public interface RentRepository extends JpaRepository<Rent, Long> {

    @Query("SELECT r FROM Rent r WHERE r.user.id = :id")
    Page<Rent> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT v FROM Rent r JOIN r.vehicles v WHERE r.id = :id")
    Page<Vehicle> findVehiclesByRentId(Long id, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v.id FROM Rent r " +
            "JOIN r.vehicles v " +
            "WHERE r.rentInformation.rentDuration.rentalStart <= :end " +
            "AND r.rentInformation.rentDuration.rentalEnd >= :start " +
            "AND r.rentStatus = 'ACTIVE'")
    Set<Long> findUnavailableVehicleIdsForRentInPeriod(LocalDateTime start,LocalDateTime end);
}
