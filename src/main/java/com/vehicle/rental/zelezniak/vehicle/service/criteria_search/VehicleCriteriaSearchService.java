package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.util.ListToPageConverter;
import com.vehicle.rental.zelezniak.vehicle.exception.CriteriaAccessException;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleCriteriaSearchService {

    private final VehicleRepository repository;

    public Page<Vehicle> findVehiclesByCriteria(CriteriaSearchRequest searchRequest, Pageable pageable) {
        if (searchRequest.registration() != null) {
            CriteriaAccessValidator.checkIfUserCanSearchByRegistration();
        }
        return createQueryAndExecute(searchRequest, pageable);
    }

    private Page<Vehicle> createQueryAndExecute(CriteriaSearchRequest searchRequest, Pageable pageable) {
        Specification<Vehicle> specification = Specification.where(null);
        if (isValid(searchRequest.brand()))
            specification = specification.and(VehicleSpecification.hasBrand(searchRequest.brand()));
        if (isValid(searchRequest.model()))
            specification = specification.and(VehicleSpecification.hasModel(searchRequest.model()));
        if (searchRequest.year() != null && 0 < searchRequest.year())
            specification = specification.and(VehicleSpecification.hasProductionYear(searchRequest.year()));
        if (isValid(searchRequest.registration()))
            specification = specification.and(VehicleSpecification.hasRegistration(searchRequest.registration()));
        if (isValid(searchRequest.status()))
            specification = specification.and(VehicleSpecification.hasStatus(searchRequest.status()));
        if (isValid(searchRequest.sortBy())) {
            Sort sort = getSort(searchRequest);
            List<Vehicle> vehicles = repository.findAll(specification, sort);
            return ListToPageConverter.convertToPage(vehicles, pageable);
        }
        return repository.findAll(specification, pageable);
    }

    private boolean isValid(String value) {
        return value != null && !value.isEmpty();
    }

    private Sort getSort(CriteriaSearchRequest searchRequest) {
        if (searchRequest.descending()) {
            return Sort.by(searchRequest.sortBy()).descending();
        }
        return Sort.by(searchRequest.sortBy());
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
