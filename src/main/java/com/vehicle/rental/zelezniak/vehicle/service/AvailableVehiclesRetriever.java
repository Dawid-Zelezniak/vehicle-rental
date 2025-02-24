package com.vehicle.rental.zelezniak.vehicle.service;

import com.vehicle.rental.zelezniak.common_value_objects.RentDuration;
import com.vehicle.rental.zelezniak.rent.repository.RentRepository;
import com.vehicle.rental.zelezniak.reservation.repository.ReservationRepository;
import com.vehicle.rental.zelezniak.vehicle.model.dto.AvailableVehiclesCriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.criteria_search.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class responsible for finding vehicles available in period selected by user.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AvailableVehiclesRetriever {

    private final VehicleRepository vehicleRepository;
    private final RentRepository rentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public Page<Vehicle> findAvailableVehiclesByCriteria(AvailableVehiclesCriteriaSearchRequest searchRequest, Pageable pageable) {
        log.debug("Search for vehicles in a specified period according to specific criteria");
        Set<Long> unavailableVehiclesIdsInPeriod = findReservedAndRentedVehiclesIdsInPeriod(searchRequest.duration());
        Specification<Vehicle> specification = VehicleSpecification.buildSpecificationForAvailableVehicles(
                searchRequest, unavailableVehiclesIdsInPeriod);
        return vehicleRepository.findAll(specification, pageable);
    }

    @Transactional
    public Collection<Vehicle> findAvailableVehiclesByRentDuration(RentDuration duration) {
        log.debug("Search for vehicles in a specified period");
        Set<Long> unavailableVehiclesIdsInPeriod = findReservedAndRentedVehiclesIdsInPeriod(duration);
        return vehicleRepository.findVehiclesByIdNotIn(unavailableVehiclesIdsInPeriod);
    }

    private Set<Long> findReservedAndRentedVehiclesIdsInPeriod(RentDuration duration) {
        LocalDateTime start = duration.getRentalStart();
        LocalDateTime end = duration.getRentalEnd();
        return Stream.concat(
                        findUnavailableVehicleIdsForRents(start, end).stream(),
                        findUnavailableVehicleIdsForReservations(start, end).stream())
                .collect(Collectors.toSet());
    }

    private Set<Long> findUnavailableVehicleIdsForRents(LocalDateTime start, LocalDateTime end) {
        return rentRepository.findUnavailableVehicleIdsForRentInPeriod(start, end);
    }

    private Set<Long> findUnavailableVehicleIdsForReservations(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.unavailableVehicleIdsForReservationInPeriod(start, end);
    }
}
