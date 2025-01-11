package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle.model.dto.AvailableVehiclesCriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import com.vehicle.rental.zelezniak.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
@Slf4j
public class VehicleSpecification {

    private static final String INFORMATION = "vehicleInformation";

    private VehicleSpecification() {
    }

    public static Specification<Vehicle> buildSpecification(CriteriaSearchRequest searchRequest) {
        Specification<Vehicle> specification = Specification.where(null);
        if (isValid(searchRequest.brand()))
            specification = specification.and(hasBrand(searchRequest.brand()));
        if (isValid(searchRequest.model()))
            specification = specification.and(hasModel(searchRequest.model()));
        if (searchRequest.year() != null && 0 < searchRequest.year())
            specification = specification.and(hasProductionYear(searchRequest.year()));
        if (isValid(searchRequest.registration()))
            specification = specification.and(hasRegistration(searchRequest.registration()));
        if (isValid(searchRequest.status()))
            specification = specification.and(hasStatus(searchRequest.status()));
        return specification;
    }

    public static Specification<Vehicle> buildSpecificationForAvailableVehicles(
            AvailableVehiclesCriteriaSearchRequest searchRequest, Set<Long> unavailableVehiclesIds) {
        Specification<Vehicle> specification = buildSpecification(searchRequest.searchRequest());
        specification = specification.and(idNotIn(unavailableVehiclesIds)).and(hasStatus("AVAILABLE"));
        return specification;
    }

    private static Specification<Vehicle> idNotIn(Set<Long> unavailableVehiclesIds) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.not(root.get("id").in(unavailableVehiclesIds)));
    }

    private static Specification<Vehicle> hasBrand(String brand) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(INFORMATION).get("brand"), brand));
    }

    private static Specification<Vehicle> hasModel(String model) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(INFORMATION).get("model"), model));
    }

    private static Specification<Vehicle> hasProductionYear(Integer year) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(INFORMATION).get("productionYear").get("year"), year));
    }

    private static Specification<Vehicle> hasRegistration(String registration) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(INFORMATION).get("registrationNumber").get("registration"), registration));
    }

    private static Specification<Vehicle> hasStatus(String status) {
        Vehicle.VehicleStatus vehicleStatus = Vehicle.VehicleStatus.getStatusFromString(status);
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), vehicleStatus));
    }

    private static boolean isValid(String value) {
        return value != null && !value.isEmpty();
    }
}
