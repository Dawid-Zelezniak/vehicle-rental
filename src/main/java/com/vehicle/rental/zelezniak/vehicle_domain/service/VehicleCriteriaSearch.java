package com.vehicle.rental.zelezniak.vehicle_domain.service;

import com.vehicle.rental.zelezniak.vehicle_domain.exception.CriteriaAccessException;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle_domain.model.vehicles.util.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.util.validation.InputValidator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class VehicleCriteriaSearch {

    private final InputValidator validator;
    private final CriteriaSearchExecutor searchExecutor;
    private final Map<CriteriaType, Function<Object, Page<Vehicle>>> criteriaMap;

    public VehicleCriteriaSearch(InputValidator validator, CriteriaSearchExecutor searchExecutor) {
        this.validator = validator;
        this.searchExecutor = searchExecutor;
        criteriaMap = initializeCriteriaMap();
    }

    public <T> Page<Vehicle> findVehiclesByCriteria(CriteriaSearchRequest<T> searchRequest, Pageable pageable) {
        CriteriaType criteria = CriteriaType.getCriteriaFromString(searchRequest.getCriteriaName());
        checkIfUserCanUseSuchCriteria(criteria);
        Function<Object, Page<Vehicle>> queryFunction = criteriaMap.get(criteria);
        searchExecutor.setPageable(pageable);
        return handleExecuteFunction(searchRequest.getValue(), queryFunction);
    }

    private void checkIfUserCanUseSuchCriteria(CriteriaType criteria) {
        if (criteria == CriteriaType.REGISTRATION_NUMBER) {
            validateUserHasAdminRole();
        }
    }

    private void validateUserHasAdminRole() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority
                        .getAuthority()
                        .equals("ROLE_ADMIN"));
        checkIfUserIsAdmin(isAdmin);
    }

    private void checkIfUserIsAdmin(boolean isAdmin) {
        if (!isAdmin) {
            throwCriteriaAccessException();
        }
    }

    private void throwCriteriaAccessException() {
        throw new CriteriaAccessException(
                "Access denied: Only admins can search by registration number");
    }

    private <T> Page<Vehicle> handleExecuteFunction(T value, Function<T, Page<Vehicle>> queryFunction) {
        validator.throwExceptionIfObjectIsNull(queryFunction,
                "The specified criterion is not supported");
        return queryFunction.apply(value);
    }

    private <T> Map<CriteriaType, Function<T, Page<Vehicle>>> initializeCriteriaMap() {
        Map<CriteriaType, Function<T, Page<Vehicle>>> result = new EnumMap<>(CriteriaType.class);
        assert searchExecutor != null;
        result.put(CriteriaType.BRAND, searchExecutor::findByBrand);
        result.put(CriteriaType.MODEL, searchExecutor::findByModel);
        result.put(CriteriaType.REGISTRATION_NUMBER, searchExecutor::findByRegistrationNumber);
        result.put(CriteriaType.PRODUCTION_YEAR, searchExecutor::findByProductionYear);
        result.put(CriteriaType.STATUS, searchExecutor::findByStatus);
        return result;
    }

    @Getter
    @AllArgsConstructor
    private enum CriteriaType {

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
            throw new IllegalArgumentException("Unknown criteria type " + value);
        }

        private final String value;
    }
}
