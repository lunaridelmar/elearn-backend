package com.ni.la.oa.elearn.api.dto;

public record AuthResponse(String accessToken, String refreshToken, String tokenType) {
    public AuthResponse(String access, String refresh) { this(access, refresh, "Bearer"); }
}
