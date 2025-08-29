package com.ni.la.oa.elearn.api;

import com.ni.la.oa.elearn.api.dto.ApiResponse;
import com.ni.la.oa.elearn.api.dto.cource.CourseRequest;
import com.ni.la.oa.elearn.api.dto.cource.CourseResponse;
import com.ni.la.oa.elearn.api.dto.cource.LessonRequest;
import com.ni.la.oa.elearn.api.dto.cource.LessonResponse;
import com.ni.la.oa.elearn.api.dto.error.ApiError;
import com.ni.la.oa.elearn.domain.Course;
import com.ni.la.oa.elearn.domain.Lesson;
import com.ni.la.oa.elearn.repo.CourseRepository;
import com.ni.la.oa.elearn.repo.LessonRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<ApiResponse<CourseResponse>> create(@Valid @RequestBody CourseRequest req) {
        if (courses.existsByTitle(req.title())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(new ApiError("TITLE_EXISTS", "Course title already exists"))
            );
        }
        Course c = new Course();
        c.setTitle(req.title());
        c.setDescription(req.description());
        courses.save(c);
        return ResponseEntity.ok(ApiResponse.success(
                new CourseResponse(c.getId(), c.getTitle(), c.getDescription()))
        );
    }

    // ---- STUDENT: view all courses
    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> all() {
        List<CourseResponse> list = courses.findAll().stream()
            .map(c -> new CourseResponse(c.getId(), c.getTitle(), c.getDescription()))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // ---- TEACHER: add lesson to a course
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> addLesson(@PathVariable Long courseId,
                                                    @Valid @RequestBody LessonRequest req) {
        Course c = courses.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        Lesson l = new Lesson();
        l.setTitle(req.title());
        l.setContent(req.content());
        l.setCourse(c);
        lessons.save(l);
        return ResponseEntity.ok(ApiResponse.success(
                new LessonResponse(l.getId(), l.getTitle(), l.getContent(), c.getId()))
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> byId(@PathVariable Long id) {
        Course c = courses.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        return ResponseEntity.ok(ApiResponse.success(
                new CourseResponse(c.getId(), c.getTitle(), c.getDescription()))
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessons(@PathVariable Long courseId) {
        Course c = courses.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        List<LessonResponse> list = lessons.findByCourseId(c.getId()).stream()
                .map(l -> new LessonResponse(l.getId(), l.getTitle(), l.getContent(), courseId))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
