package com.vehicle.rental.zelezniak.vehicle.service;

import com.vehicle.rental.zelezniak.vehicle.exception.CriteriaAccessException;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.service.criteria_search.CriteriaSearchStrategyFactory;
import com.vehicle.rental.zelezniak.vehicle.service.criteria_search.VehicleSearchStrategy;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleCriteriaSearch {

    // add dynamic queries

    private final CriteriaSearchStrategyFactory searchStrategyFactory;

    public Page<Vehicle> findVehiclesByCriteria(CriteriaSearchRequest searchRequest, Pageable pageable) {
        if(searchRequest.getRegistration() != null){
            CriteriaAccessValidator.checkIfUserCanSearchByRegistration();
        }

        VehicleSearchStrategy strategy = searchStrategyFactory.getStrategy(criteria);
        return strategy.findByCriteria(searchRequest.getValue(), pageable);
    }

    private static class CriteriaAccessValidator {

        private static final String ROLE_ADMIN = "ROLE_ADMIN";

        private static void checkIfUserCanSearchByRegistration() {
                validateUserHasAdminRole();
        }

        private static void validateUserHasAdminRole() {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            boolean hasRoleAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(ROLE_ADMIN));
            if (!hasRoleAdmin) {
                log.error("User with role CLIENT tried to search vehicles by registration number.");
                throwException();
            }
        }

        private static void throwException() {
            throw new CriteriaAccessException("Access denied: Only admins can search by registration number");
        }
    }
}
