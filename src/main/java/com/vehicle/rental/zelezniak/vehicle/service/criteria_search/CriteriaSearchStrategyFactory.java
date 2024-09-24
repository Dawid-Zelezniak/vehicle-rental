package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import com.vehicle.rental.zelezniak.vehicle.service.VehicleCriteriaSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CriteriaSearchStrategyFactory {

    private final VehicleRepository repository;

    public VehicleSearchStrategy getStrategy(VehicleCriteriaSearch.CriteriaType criteriaType) {
        VehicleSearchStrategy strategy;
        switch (criteriaType) {
            case BRAND -> strategy = new BrandCriteriaSearch(repository);
            case MODEL -> strategy = new ModelCriteriaSearch(repository);
            case REGISTRATION_NUMBER -> strategy = new RegistrationNumberCriteriaSearch(repository);
            case PRODUCTION_YEAR -> strategy = new ProductionYearCriteriaSearch(repository);
            case STATUS -> strategy = new VehicleStatusCriteriaSearch(repository);
            default -> {
                log.error("Unknown criteria type: {}", criteriaType.getValue());
                throw new IllegalArgumentException("Unknown criteria type: " + criteriaType.getValue());
            }
        }
        return strategy;
    }
}
