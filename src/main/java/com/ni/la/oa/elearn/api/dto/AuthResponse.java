package com.ni.la.oa.elearn.api.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn // seconds until access token expires
) {
    public AuthResponse(String access, String refresh, long expiresIn) {
        this(access, refresh, "Bearer", expiresIn);
    }
}
