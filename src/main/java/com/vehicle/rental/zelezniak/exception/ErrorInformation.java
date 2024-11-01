package com.vehicle.rental.zelezniak.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
public class ErrorInformation {

    private String message;
    private Integer code;
    private Set<String> fieldValidationErrors = new HashSet<>();

    public ErrorInformation(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ErrorInformation(Integer code, Set<String> errors) {
        this.code = code;
        this.fieldValidationErrors = errors;
    }
}
