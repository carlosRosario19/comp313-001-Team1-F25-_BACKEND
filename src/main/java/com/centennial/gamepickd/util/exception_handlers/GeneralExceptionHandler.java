package com.centennial.gamepickd.util.exception_handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GeneralExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error");
        problemDetail.setDetail("Invalid input data.");
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        // Add field-specific validation details
        Map<String, String> violations = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            violations.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        problemDetail.setProperty("violations", violations);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }


}
