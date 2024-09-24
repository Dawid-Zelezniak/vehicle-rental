package com.vehicle.rental.zelezniak.vehicle.service;

import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.rent.repository.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/**
 * Class responsible for finding vehicles available in period selected by user.
 */
@Component
@RequiredArgsConstructor
public class AvailableVehiclesRetriever {

    private final RentRepository rentRepository;
    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findVehiclesAvailableInPeriod(RentDuration duration, Pageable pageable) {
        Set<Long> unavailableVehiclesIdsInPeriod = findReservedAndRentedVehiclesInPeriod(duration);
        return vehicleRepository.findVehiclesByIdNotIn(unavailableVehiclesIdsInPeriod,pageable);
    }

    public Collection<Vehicle> findVehiclesAvailableInPeriod(RentDuration duration) {
        Set<Long> unavailableVehiclesIdsInPeriod = findReservedAndRentedVehiclesInPeriod(duration);
        return vehicleRepository.findVehiclesByIdNotIn(unavailableVehiclesIdsInPeriod);
    }

    private Set<Long> findReservedAndRentedVehiclesInPeriod(RentDuration duration) {
        LocalDateTime start = duration.getRentalStart();
        LocalDateTime end = duration.getRentalEnd();
        Set<Long> unavailableForRents = rentRepository.findUnavailableVehicleIdsForRentInPeriod(start, end);
        Set<Long> unavailableForReservations = reservationRepository.unavailableVehicleIdsForReservationInPeriod(start, end);
        unavailableForRents.addAll(unavailableForReservations);
        return unavailableForRents;
    }
}
