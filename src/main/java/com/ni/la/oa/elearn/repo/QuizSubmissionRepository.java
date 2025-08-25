package com.ni.la.oa.elearn.repo;

import com.ni.la.oa.elearn.domain.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByStudent_Id(Long userId);
    List<QuizSubmission> findByQuestion_Quiz_IdAndStudent_Id(Long quizId, Long userId);
    Optional<QuizSubmission> findFirstByQuestion_IdAndStudent_Id(Long questionId, Long userId);
    boolean existsByStudent_IdAndQuestion_Id(Long studentId, Long questionId);
}
