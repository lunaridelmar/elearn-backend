package com.ni.la.oa.elearn.api.dto.cource;

public record MySubmissionDto(
        Long questionId,
        String answer,
        boolean correct
) {}
