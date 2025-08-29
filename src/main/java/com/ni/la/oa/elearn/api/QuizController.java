package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.ApiResponse;
import com.ni.la.oa.elearn.api.dto.cource.*;
import com.ni.la.oa.elearn.domain.*;
import com.ni.la.oa.elearn.repo.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizRepository quizzes;
    private final LessonRepository lessons;
    private final QuizQuestionRepository questions;
    private final QuizSubmissionRepository submissions;
    private final UserRepository users;

    public QuizController(QuizRepository quizzes, LessonRepository lessons,
                          QuizQuestionRepository questions, QuizSubmissionRepository submissions,
                          UserRepository users) {
        this.quizzes = quizzes;
        this.lessons = lessons;
        this.questions = questions;
        this.submissions = submissions;
        this.users = users;
    }

    // TEACHER: create quiz
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<QuizResponse>> create(@RequestBody QuizRequest req) {
        Lesson lesson = lessons.findById(req.lessonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        Quiz q = new Quiz();
        q.setTitle(req.title());
        q.setLesson(lesson);

        for (QuizRequest.QuestionDto dto : req.questions()) {
            QuizQuestion qq = new QuizQuestion();
            qq.setQuestion(dto.question());
            qq.setCorrectAnswer(dto.correctAnswer());
            qq.setQuiz(q);
            q.getQuestions().add(qq);
        }
        quizzes.save(q);

        QuizResponse body = new QuizResponse(
                q.getId(),
                q.getTitle(),
                lesson.getId(),
                q.getQuestions().stream()
                        .map(qq -> new QuizResponse.QuestionDto(qq.getId(), qq.getQuestion()))
                        .toList()
        );

        return ResponseEntity
                .created(URI.create("/quizzes/" + q.getId()))
                .body(ApiResponse.success(body));
    }

    // STUDENT: submit answer
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{quizId}/submit")
    @Transactional
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> submit(@PathVariable Long quizId,
                                           @Valid @RequestBody List<SubmissionRequest> reqs) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // in our setup, username = email
        User student = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Quiz quiz = quizzes.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));
        List<SubmissionResponse> result = reqs.stream().map(req -> {
            if (req.questionId() == null)
                throw new IllegalArgumentException("questionId is required");
            if (req.answer() == null)
                throw new IllegalArgumentException("answer is required");
            QuizQuestion q = questions.findById(req.questionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
            if (!q.getQuiz().getId().equals(quiz.getId())) {
                throw new IllegalArgumentException("Question does not belong to this quiz");
            }
            // 🚨 NEW VALIDATION: prevent double submission
            if (submissions.existsByStudent_IdAndQuestion_Id(student.getId(), q.getId())) {
                throw new IllegalStateException("Already submitted for this question");
            }
            boolean correct = q.getCorrectAnswer().equalsIgnoreCase(req.answer());
            QuizSubmission sub = new QuizSubmission();
            sub.setAnswer(req.answer());
            sub.setCorrect(correct);
            sub.setQuestion(q);
            sub.setStudent(student);
            submissions.save(sub);
            return new SubmissionResponse(sub.getId(), q.getId(), correct);
        }).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/{quizId}/questions")
    @Transactional(readOnly = true) // optional safety
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getQuestions(@PathVariable Long quizId) {
        quizzes.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<QuestionDto> list = questions.findByQuiz_Id(quizId).stream()
                .map(q -> new QuestionDto(q.getId(), q.getQuestion()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{quizId}/my-submissions")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<MySubmissionDto>>> mySubmissions(@PathVariable Long quizId, Authentication auth) {
        String email = auth.getName();
        var student = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        quizzes.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<MySubmissionDto> list = submissions.findByQuestion_Quiz_IdAndStudent_Id(quizId, student.getId())
                .stream()
                .map(s -> new MySubmissionDto(
                        s.getQuestion().getId(),
                        s.getAnswer(),
                        s.isCorrect()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{quizId}/submissions")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<QuizSubmissionDto>>> allSubmissions(@PathVariable Long quizId) {
        quizzes.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<QuizSubmissionDto> list = submissions.findByQuestion_Quiz_Id(quizId)
                .stream()
                .map(s -> new QuizSubmissionDto(
                        s.getId(),
                        s.getQuestion().getId(),
                        s.getQuestion().getQuestion(),
                        s.getStudent().getEmail(),
                        s.getAnswer(),
                        s.isCorrect()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

}
