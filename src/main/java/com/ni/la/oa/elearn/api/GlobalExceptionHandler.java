package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.ApiResponse;
import com.ni.la.oa.elearn.api.dto.error.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle explicit ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleResponseStatus(ResponseStatusException ex) {
        ApiError error = new ApiError(
                ex.getStatusCode().toString(),
                ex.getReason() != null ? ex.getReason() : "Unexpected error"
        );
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ApiResponse.error(error));
    }

    // Handle validation errors (@Valid on DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ApiError error = new ApiError("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(ApiResponse.error(error));
    }

    // Fallback handler for unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiError>> handleGeneric(Exception ex) {
        ApiError error = new ApiError("INTERNAL_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(error));
    }
}
