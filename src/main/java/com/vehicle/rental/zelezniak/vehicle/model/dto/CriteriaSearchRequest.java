package com.vehicle.rental.zelezniak.vehicle.model.dto;

public record CriteriaSearchRequest(
        String brand,
        String model,
        Integer year,
        String registration,
        String status,
        String sortBy,
        boolean descending
) {


}
