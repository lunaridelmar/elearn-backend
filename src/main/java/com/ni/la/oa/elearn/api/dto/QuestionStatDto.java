package com.ni.la.oa.elearn.api.dto;

public record QuestionStatDto(
        Long questionId,
        Long attempts,
        Long correctCount,
        Double correctRate // 0..100
) {}