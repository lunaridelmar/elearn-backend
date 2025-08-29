package com.ni.la.oa.elearn.api.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details about an error returned by the API")
public record ApiError(
        @Schema(description = "Machine readable error code") String code,
        @Schema(description = "Human readable error message") String message,
        @Schema(description = "Additional details that may help debugging", nullable = true) String details
) {
    public ApiError(String code, String message) {
        this(code, message, null);
    }
}
