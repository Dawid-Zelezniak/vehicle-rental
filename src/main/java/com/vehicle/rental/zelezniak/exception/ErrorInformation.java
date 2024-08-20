package com.vehicle.rental.zelezniak.exception;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class ErrorInformation {

    private String message;
    private Integer code;
}
