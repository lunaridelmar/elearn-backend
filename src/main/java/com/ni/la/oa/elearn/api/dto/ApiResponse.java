package com.ni.la.oa.elearn.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ni.la.oa.elearn.api.dto.error.ApiError;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Generic wrapper for all API responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Schema(description = "Payload returned on success")
    private T data;

    @Schema(description = "Error details when the request is not successful")
    private ApiError error;

    public ApiResponse() {
    }

    public ApiResponse(T data, ApiError error) {
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> error(ApiError error) {
        return new ApiResponse<>(null, error);
    }

    public T getData() {
        return data;
    }

    public ApiError getError() {
        return error;
    }
}
