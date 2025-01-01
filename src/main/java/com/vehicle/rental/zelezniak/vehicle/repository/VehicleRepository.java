package com.vehicle.rental.zelezniak.vehicle.repository;

import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.RegistrationNumber;
import com.vehicle.rental.zelezniak.vehicle.model.vehicle_value_objects.Year;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    boolean existsByVehicleInformationRegistrationNumber(RegistrationNumber registrationNumber);

    Vehicle findByVehicleInformationRegistrationNumber(RegistrationNumber registrationNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN :idSet AND v.status = 'AVAILABLE'")
    Page<Vehicle> findVehiclesByIdNotIn(@Param("idSet") Set<Long> vehiclesIds, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN :idSet AND v.status = 'AVAILABLE'")
    Collection<Vehicle> findVehiclesByIdNotIn(@Param("idSet") Set<Long> vehiclesIds);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM reserved_vehicles rv WHERE rv.vehicle_id = :id")
    void deleteFromReservedVehicles(Long id);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM rented_vehicles rv WHERE rv.vehicle_id = :id")
    void deleteFromRentedVehicles(Long id);
}
