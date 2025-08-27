package com.ni.la.oa.elearn.api.dto;

public record StudentScoreDto(
        Long studentId,
        String email,
        Long correct,
        Long total,
        Double percent // 0..100
) {}