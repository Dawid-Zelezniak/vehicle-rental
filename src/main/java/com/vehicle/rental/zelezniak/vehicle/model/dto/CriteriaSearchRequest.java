package com.vehicle.rental.zelezniak.vehicle.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static com.vehicle.rental.zelezniak.constants.ValidationMessages.CAN_NOT_BE_NULL;

@Getter
@Setter
@AllArgsConstructor
public class CriteriaSearchRequest<T> {

    @NotNull(message = "Criteria name" + CAN_NOT_BE_NULL)
    private String criteriaName;

    @NotNull(message = "Searched value" + CAN_NOT_BE_NULL)
    private T value;
}
