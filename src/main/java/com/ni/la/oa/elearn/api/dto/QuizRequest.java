package com.ni.la.oa.elearn.api.dto;

import java.util.List;

public record QuizRequest(String title, Long lessonId, List<QuestionDto> questions) {
    public record QuestionDto(String question, String correctAnswer) {}
}
