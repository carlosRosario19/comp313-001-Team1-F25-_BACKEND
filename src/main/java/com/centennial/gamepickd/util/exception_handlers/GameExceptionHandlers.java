package com.centennial.gamepickd.util.exception_handlers;

import com.centennial.gamepickd.util.Exceptions;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GameExceptionHandlers {

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleGameAlreadyExistsException(Exceptions.GameAlreadyExistsException ex, HttpServletRequest request){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Existing Game Error");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }
}
