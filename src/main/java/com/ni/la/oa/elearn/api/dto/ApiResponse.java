package com.ni.la.oa.elearn.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ni.la.oa.elearn.api.dto.error.ApiError;

@JsonInclude(JsonInclude.Include.NON_NULL) // hide null fields in JSON
public record ApiResponse<T>(
        T data,
        ApiError error
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
