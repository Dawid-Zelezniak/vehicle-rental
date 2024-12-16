package com.vehicle.rental.zelezniak.vehicle.service.criteria_search;

import com.vehicle.rental.zelezniak.vehicle.model.vehicles.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VehicleSpecification {

    private static final String INFORMATION = "vehicleInformation";

    private VehicleSpecification() {
    }

    public static Specification<Vehicle> hasBrand(String brand) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(INFORMATION).get("brand"), brand));
    }

    public static Specification<Vehicle> hasModel(String model) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(INFORMATION).get("model"), model));
    }

    public static Specification<Vehicle> hasProductionYear(Integer year) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(INFORMATION).get("productionYear").get("year"), year));
    }

    public static Specification<Vehicle> hasRegistration(String registration) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get(INFORMATION).get("registrationNumber").get("registration"), registration));
    }

    public static Specification<Vehicle> hasStatus(String status) {
        Vehicle.VehicleStatus vehicleStatus = Vehicle.VehicleStatus.getStatusFromString(status);
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), vehicleStatus));
    }
}
