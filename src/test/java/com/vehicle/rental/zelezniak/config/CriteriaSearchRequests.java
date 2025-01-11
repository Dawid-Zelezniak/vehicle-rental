package com.vehicle.rental.zelezniak.config;

import com.vehicle.rental.zelezniak.vehicle.model.dto.CriteriaSearchRequest;
import org.springframework.stereotype.Component;

@Component
public class CriteriaSearchRequests {

    public CriteriaSearchRequest getEmptySearchRequest() {
        return new CriteriaSearchRequest(null, null, null, null, null, null,false);
    }

    public CriteriaSearchRequest getBrandSearchRequest(String brand) {
        return new CriteriaSearchRequest(brand, null, null, null, null, null,false);
    }

    public CriteriaSearchRequest getModelSearchRequest(String model) {
        return new CriteriaSearchRequest(null, model, null, null, null, null,false);
    }

    public CriteriaSearchRequest getProductionYearSearchRequest(Integer year) {
        return new CriteriaSearchRequest(null, null, year, null, null, null,false);
    }

    public CriteriaSearchRequest getRegistrationSearchRequest(String registration) {
        return new CriteriaSearchRequest(null, null, null, registration, null, null,false);
    }

    public CriteriaSearchRequest getStatusSearchRequest(String status) {
        return new CriteriaSearchRequest(null, null, null, null, status, null,false);
    }

    public CriteriaSearchRequest getBrandAndProductionYearSearchRequest(String brand, Integer year) {
        return new CriteriaSearchRequest(brand, null, year, null, null, null,false);
    }

    public CriteriaSearchRequest getModelAndProductionYearSearchRequest(String model,Integer year) {
        return new CriteriaSearchRequest(null, model, year, null, null, null,false);
    }

    public CriteriaSearchRequest getSearchByProductionYearAndSortByRequest(Integer year, String sortBy) {
        return new CriteriaSearchRequest(null, null, year, null, null, sortBy,false);
    }

    public CriteriaSearchRequest getSearchByProductionYearAndSortDescByRequest(Integer year, String sortBy) {
        return new CriteriaSearchRequest(null, null, year, null, null, sortBy,true);
    }
}
