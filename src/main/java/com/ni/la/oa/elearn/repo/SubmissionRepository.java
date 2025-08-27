package com.ni.la.oa.elearn.repo;

import com.ni.la.oa.elearn.api.dto.cource.QuestionStatDto;
import com.ni.la.oa.elearn.api.dto.cource.StudentScoreDto;
import com.ni.la.oa.elearn.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    long countByQuestion_Quiz_Id(Long quizId);

    @Query("""
        select count(distinct s.student.id)
        from Submission s
        where s.question.quiz.id = :quizId
    """)
    long countDistinctStudentsByQuizId(Long quizId);

    @Query("""
    select new com.ni.la.oa.elearn.api.dto.QuestionStatDto(
        s.question.id,
        count(s),
        sum(case when s.correct = true then 1 else 0 end),
        (1.0 * sum(case when s.correct = true then 1 else 0 end)) / nullif(count(s), 0)
    )
    from Submission s
    where s.question.quiz.id = :quizId
    group by s.question.id
    order by s.question.id
""")
    List<QuestionStatDto> perQuestionStats(@Param("quizId") Long quizId);


    @Query("""
    select new com.ni.la.oa.elearn.api.dto.StudentScoreDto(
        s.student.id,
        s.student.email,
        sum(case when s.correct = true then 1 else 0 end),
        count(s),
        (1.0 * sum(case when s.correct = true then 1 else 0 end)) / nullif(count(s),0)
    )
    from Submission s
    where s.question.quiz.id = :quizId
    group by s.student.id, s.student.email
    order by sum(case when s.correct = true then 1 else 0 end) desc, s.student.email asc
    """)
    List<StudentScoreDto> leaderboard(@Param("quizId") Long quizId);
}
