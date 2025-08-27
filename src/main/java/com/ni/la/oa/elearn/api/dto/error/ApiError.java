package com.ni.la.oa.elearn.api.dto.error;

public record ApiError(
        String code,
        String message,
        String details
) {
    public ApiError(String code, String message) {
        this(code, message, null);
    }
}
