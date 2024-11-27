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

    public <T> Page<Vehicle> findVehiclesByCriteria(CriteriaSearchRequest<T> searchRequest, Pageable pageable) {
        CriteriaType criteria = CriteriaType.getCriteriaFromString(searchRequest.getCriteriaName());
        CriteriaAccessValidator.checkIfUserCanSearchBySuchCriteria(criteria);
        VehicleSearchStrategy strategy = searchStrategyFactory.getStrategy(criteria);
        return strategy.findByCriteria(searchRequest.getValue(), pageable);
    }

    @Getter
    @AllArgsConstructor
    public enum CriteriaType {

        BRAND("brand"),
        MODEL("model"),
        REGISTRATION_NUMBER("registration number"),
        PRODUCTION_YEAR("production year"),
        STATUS("status");

        public static CriteriaType getCriteriaFromString(String value) {
            for (CriteriaType criteriaType : CriteriaType.values()) {
                if (criteriaType.getValue().equalsIgnoreCase(value)) {
                    return criteriaType;
                }
            }
            log.error("Undefined criteria type: {}", value);
            throw new IllegalArgumentException("Unknown criteria type " + value);
        }

        private final String value;
    }

    private static class CriteriaAccessValidator {

        private static final String ROLE_ADMIN = "ROLE_ADMIN";

        private static void checkIfUserCanSearchBySuchCriteria(CriteriaType criteria) {
            if (criteria == CriteriaType.REGISTRATION_NUMBER) {
                validateUserHasAdminRole();
            }
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
