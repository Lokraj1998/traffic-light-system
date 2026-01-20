package com.lrsharma.trafficlightsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // existing handlers...

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        String paramName = ex.getName();
        String invalidValue = String.valueOf(ex.getValue());

        String message = "Invalid value '" + invalidValue +
                "' for parameter '" + paramName + "'";

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "error", "Bad Request",
                "message", message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(TrafficLightException.class)
    public ResponseEntity<Map<String, Object>> handleTrafficLightException(
            TrafficLightException ex) {

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "error", "Traffic Light Rule Violation",
                "message", ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "error", "Internal Server Error",
                "message", "Something went wrong. Please contact support."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
