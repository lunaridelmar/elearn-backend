package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.ApiResponse;
import com.ni.la.oa.elearn.api.dto.cource.QuestionStatDto;
import com.ni.la.oa.elearn.api.dto.cource.QuizStatsResponse;
import com.ni.la.oa.elearn.api.dto.cource.StudentScoreDto;
import com.ni.la.oa.elearn.domain.Quiz;
import com.ni.la.oa.elearn.repo.QuizRepository;
import com.ni.la.oa.elearn.repo.SubmissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherDashboardController {

    private final SubmissionRepository submissions;
    private final QuizRepository quizzes;

    public TeacherDashboardController(SubmissionRepository submissions, QuizRepository quizzes) {
        this.submissions = submissions;
        this.quizzes = quizzes;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/quizzes/{quizId}/stats")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<QuizStatsResponse>> quizStats(@PathVariable Long quizId) {
        Quiz quiz = quizzes.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        long totalSubmissions = submissions.countByQuestion_Quiz_Id(quizId);
        long studentsAttempted = submissions.countDistinctStudentsByQuizId(quizId);

        // Per-question
        List<QuestionStatDto> rawQuestionStats = submissions.perQuestionStats(quizId);
        List<QuestionStatDto> perQuestion = rawQuestionStats.stream()
                .map(q -> new QuestionStatDto(
                        q.questionId(),
                        q.attempts(),
                        q.correctCount(),
                        q.attempts() == 0 ? 0.0 : (q.correctCount() * 100.0) / q.attempts()
                ))
                .toList();

        // Leaderboard per student
        List<StudentScoreDto> rawLeaderboard = submissions.leaderboard(quizId);
        List<StudentScoreDto> leaderboard = rawLeaderboard.stream()
                .map(s -> new StudentScoreDto(
                        s.studentId(),
                        s.email(),
                        s.correct(),
                        s.total(),
                        s.total() == 0 ? 0.0 : (s.correct() * 100.0) / s.total()
                ))
                .toList();

        // Average score across students (mean of student percents)
        double averageScorePercent = leaderboard.isEmpty()
                ? 0.0
                : leaderboard.stream().mapToDouble(StudentScoreDto::percent).average().orElse(0.0);

        QuizStatsResponse body = new QuizStatsResponse(
                quiz.getId(),
                studentsAttempted,
                totalSubmissions,
                averageScorePercent,
                perQuestion,
                leaderboard
        );
        return ResponseEntity.ok(ApiResponse.success(body));
    }
}
