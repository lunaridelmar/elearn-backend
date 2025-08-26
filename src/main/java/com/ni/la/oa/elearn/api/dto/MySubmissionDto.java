package com.ni.la.oa.elearn.api.dto;

public record MySubmissionDto(
        Long questionId,
        String answer,
        boolean correct
) {}
