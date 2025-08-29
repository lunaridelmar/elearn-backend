package com.ni.la.oa.elearn.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ni.la.oa.elearn.api.dto.error.ApiError;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic wrapper for all API responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic wrapper for API responses")
public record ApiResponse<T>(
        @Schema(description = "Payload returned on success") T data,
        @Schema(description = "Error details when the request is not successful") ApiError error
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> error(ApiError error) {
        return new ApiResponse<>(null, error);
    }
}
