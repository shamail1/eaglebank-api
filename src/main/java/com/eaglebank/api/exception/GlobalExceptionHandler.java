package com.eaglebank.api.exception;

import com.eaglebank.api.dto.common.BadRequestErrorResponse;
import com.eaglebank.api.dto.common.ErrorResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<BadRequestErrorResponse.ValidationErrorDetail> details = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return new BadRequestErrorResponse.ValidationErrorDetail(
                            fieldName,
                            errorMessage != null ? errorMessage : "Validation failed",
                            "validation_error"
                    );
                })
                .collect(Collectors.toList());
        
        BadRequestErrorResponse response = new BadRequestErrorResponse(
                "Invalid details supplied",
                details
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        ErrorResponse response = new ErrorResponse(ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }
    
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(OptimisticLockingFailureException ex) {
        ErrorResponse response = new ErrorResponse("Concurrent modification detected. Please retry your request.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BadRequestErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        BadRequestErrorResponse response = new BadRequestErrorResponse(
                "Invalid details supplied",
                List.of(new BadRequestErrorResponse.ValidationErrorDetail(
                        "request",
                        ex.getMessage(),
                        "validation_error"
                ))
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse response = new ErrorResponse("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

