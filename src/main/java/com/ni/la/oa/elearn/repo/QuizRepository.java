package com.ni.la.oa.elearn.repo;

import com.ni.la.oa.elearn.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    boolean existsByLesson_Id(Long lessonId);
    List<Quiz> findByLesson_Id(Long lessonId);
}
