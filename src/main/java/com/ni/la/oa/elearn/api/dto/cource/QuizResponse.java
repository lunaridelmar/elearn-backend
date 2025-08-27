package com.ni.la.oa.elearn.api.dto.cource;

import java.util.List;

public record QuizResponse(Long id, String title, Long lessonId, List<QuestionDto> questions) {
    public record QuestionDto(Long id, String question) {}
}
