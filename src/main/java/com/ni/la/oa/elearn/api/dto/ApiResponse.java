package com.ni.la.oa.elearn.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ni.la.oa.elearn.api.dto.error.ApiError;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic wrapper for all API responses")
@JsonInclude(JsonInclude.Include.NON_NULL) // hide null fields in JSON
public record ApiResponse<T>(
        @Schema(description = "Payload returned on success") T data,
        @Schema(description = "Error details when the request is not successful") ApiError error
) {
    // Success factory
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    // Error factory
    public static <T> ApiResponse<T> error(ApiError error) {
        return new ApiResponse<>(null, error);
    }
}
