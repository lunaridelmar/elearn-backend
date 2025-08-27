package com.ni.la.oa.elearn.api.dto.cource;

public record QuizSubmissionDto(
        Long submissionId,
        Long questionId,
        String question,
        String studentEmail,
        String answer,
        boolean correct
) {}
