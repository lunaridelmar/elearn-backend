package com.ni.la.oa.elearn.repo;

import com.ni.la.oa.elearn.domain.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByQuiz_Id(Long quizId);
}
