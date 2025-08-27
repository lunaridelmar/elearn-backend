package com.ni.la.oa.elearn.api.dto.cource;

import java.util.List;

public record QuizStatsResponse(
        Long quizId,
        long studentsAttempted,
        long totalSubmissions,
        double averageScorePercent,
        List<QuestionStatDto> perQuestion,
        List<StudentScoreDto> leaderboard
) {}