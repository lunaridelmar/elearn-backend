package com.ni.la.oa.elearn.api.dto.cource;

public record StudentScoreDto(
        Long studentId,
        String email,
        Long correct,
        Long total,
        Double percent // 0..100
) {}