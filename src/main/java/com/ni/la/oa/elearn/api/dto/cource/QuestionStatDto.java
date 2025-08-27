package com.ni.la.oa.elearn.api.dto.cource;

public record QuestionStatDto(
        Long questionId,
        Long attempts,
        Long correctCount,
        Double correctRate // 0..100
) {}