package com.ni.la.oa.elearn.repo;

import com.ni.la.oa.elearn.domain.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByStudent_Id(Long studentId);
    boolean existsByStudent_IdAndLesson_Id(Long studentId, Long lessonId);
    Optional<LessonProgress> findByStudent_IdAndLesson_Id(Long studentId, Long lessonId);
}
