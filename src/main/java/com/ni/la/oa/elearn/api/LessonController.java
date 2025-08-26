package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.LessonProgressResponse;
import com.ni.la.oa.elearn.api.dto.LessonResponse;
import com.ni.la.oa.elearn.domain.Lesson;
import com.ni.la.oa.elearn.domain.LessonProgress;
import com.ni.la.oa.elearn.domain.User;
import com.ni.la.oa.elearn.repo.LessonProgressRepository;
import com.ni.la.oa.elearn.repo.LessonRepository;
import com.ni.la.oa.elearn.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private final LessonRepository lessons;
    private final LessonProgressRepository progresses;
    private final UserRepository users;

    public LessonController(LessonRepository lessons, LessonProgressRepository progresses, UserRepository users) {
        this.lessons = lessons;
        this.progresses = progresses;
        this.users = users;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{lessonId}/complete")
    @Transactional
    public ResponseEntity<LessonProgressResponse> completeLesson(@PathVariable Long lessonId,
                                                                 Authentication auth) {
        User student = users.findByEmail(auth.getName()).orElseThrow();
        Lesson lesson  = lessons.findById(lessonId).orElseThrow();

        // If already exists, return OK
        Optional<LessonProgress> existing = progresses.findByStudent_IdAndLesson_Id(student.getId(), lessonId);
        if (existing.isPresent()) {
            var lp = existing.get();
            return ResponseEntity.ok(new LessonProgressResponse(lp.isCompleted(), lp.getCompletedAt().toString()));
        }

        // Create new completion
        var lp = new LessonProgress();
        lp.setStudent(student);
        lp.setLesson(lesson);
        if (!lp.isCompleted()) {
            lp.setCompleted(true);
            lp.setCompletedAt(Instant.now());
        }

        try {
            lp = progresses.save(lp);
            return ResponseEntity.status(201).body(new LessonProgressResponse(true, lp.getCompletedAt().toString()));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Unique constraint hit due to race -> treat as already completed
            return ResponseEntity.ok(new LessonProgressResponse(true, lp.getCompletedAt().toString()));
        }
    }


    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{lessonId}/progress")
    @Transactional(readOnly = true)
    public LessonProgressResponse myProgress(@PathVariable Long lessonId, Authentication auth) {
        var student = users.findByEmail(auth.getName()).orElseThrow();
        var opt = progresses.findByStudent_IdAndLesson_Id(student.getId(), lessonId);
        return opt.map(p -> new LessonProgressResponse(true, p.getCompletedAt().toString()))
                .orElseGet(() -> new LessonProgressResponse(false, null));
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/{lessonId}")
    @Transactional(readOnly = true)
    public LessonResponse getById(@PathVariable Long lessonId) {
        Lesson l = lessons.findById(lessonId).orElseThrow();
        return new LessonResponse(l.getId(), l.getTitle(), l.getContent(), l.getCourse().getId());
    }
}
