package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.*;
import com.ni.la.oa.elearn.domain.Course;
import com.ni.la.oa.elearn.domain.Lesson;
import com.ni.la.oa.elearn.repo.CourseRepository;
import com.ni.la.oa.elearn.repo.LessonRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courses;
    private final LessonRepository lessons;

    public CourseController(CourseRepository courses, LessonRepository lessons) {
        this.courses = courses;
        this.lessons = lessons;
    }

    // ---- TEACHER: create course
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<CourseResponse> create(@Valid @RequestBody CourseRequest req) {
        if (courses.existsByTitle(req.title())) {
            return ResponseEntity.badRequest().build();
        }
        Course c = new Course();
        c.setTitle(req.title());
        c.setDescription(req.description());
        courses.save(c);
        return ResponseEntity.ok(new CourseResponse(c.getId(), c.getTitle(), c.getDescription()));
    }

    // ---- STUDENT: view all courses
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping
    public List<CourseResponse> all() {
        return courses.findAll().stream()
            .map(c -> new CourseResponse(c.getId(), c.getTitle(), c.getDescription()))
            .toList();
    }

    // ---- TEACHER: add lesson to a course
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<LessonResponse> addLesson(@PathVariable Long courseId,
                                                    @Valid @RequestBody LessonRequest req) {
        Course c = courses.findById(courseId).orElseThrow();
        Lesson l = new Lesson();
        l.setTitle(req.title());
        l.setContent(req.content());
        l.setCourse(c);
        lessons.save(l);
        return ResponseEntity.ok(new LessonResponse(l.getId(), l.getTitle(), l.getContent(), c.getId()));
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> byId(@PathVariable Long id) {
        return courses.findById(id)
                .map(c -> ResponseEntity.ok(new CourseResponse(c.getId(), c.getTitle(), c.getDescription())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<List<LessonResponse>> getLessons(@PathVariable Long courseId) {
        if (!courses.existsById(courseId)) return ResponseEntity.notFound().build();
        var list = lessons.findByCourseId(courseId).stream()
                .map(l -> new LessonResponse(l.getId(), l.getTitle(), l.getContent(), courseId))
                .toList();
        return ResponseEntity.ok(list);
    }
}
