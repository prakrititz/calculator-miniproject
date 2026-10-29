package com.example.calculator_miniproject.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.calculator_miniproject.dto.ApiError;

import jakarta.servlet.http.HttpServletRequest;

// Watches every controller: when one throws, the matching handler below builds the 400 JSON.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // /divide?a=10&b=0
    @ExceptionHandler(DivideByZeroException.class)
    public ResponseEntity<ApiError> handleDivideByZero(DivideByZeroException ex, HttpServletRequest req) {
        return badRequest(ex.getMessage(), req);
    }

    // /add?a=2  (b missing)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParam(MissingServletRequestParameterException ex,
            HttpServletRequest req) {
        return badRequest("Missing required parameter: " + ex.getParameterName(), req);
    }

    // /add?a=foo&b=3  (not a number)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest req) {
        return badRequest("Invalid value for parameter '" + ex.getName() + "': must be a number", req);
    }

    // Shared by all handlers so the error JSON always has the same shape.
    private ResponseEntity<ApiError> badRequest(String message, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message,
                req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
