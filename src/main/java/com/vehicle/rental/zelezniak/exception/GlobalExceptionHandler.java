package com.vehicle.rental.zelezniak.exception;

import com.vehicle.rental.zelezniak.vehicle.exception.CriteriaAccessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorInformation> handleException(NoSuchElementException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return createResponse(status, exception.getMessage());
    }

    @ExceptionHandler(CriteriaAccessException.class)
    public ResponseEntity<ErrorInformation> handleException(CriteriaAccessException exception) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return createResponse(status, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorInformation> handleException(IllegalArgumentException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return createResponse(status, exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorInformation> handleException(IllegalStateException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return createResponse(status, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInformation> handleException(MethodArgumentNotValidException exception) {
        LinkedHashSet<String> errors = new LinkedHashSet<>();

        BindingResult bindingResult = exception.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        allErrors.forEach(error -> {
            String message = error.getDefaultMessage();
            errors.add(message);
        });
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorInformation(status.value(), errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorInformation> handleException(ConstraintViolationException exception) {
        LinkedHashSet<String> errors = new LinkedHashSet<>();

        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        constraintViolations.forEach(constraintViolation -> errors.add(constraintViolation.getMessage()));
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new ErrorInformation(status.value(), errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInformation> handleException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return createResponse(status, exception.getMessage());
    }

    private ResponseEntity<ErrorInformation> createResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorInformation(message, status.value()));
    }
}
