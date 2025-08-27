package com.ni.la.oa.elearn.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "submission",
       indexes = {
           @Index(name = "ix_submission_student", columnList = "student_id"),
           @Index(name = "ix_submission_question", columnList = "question_id")
       })
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who answered
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    // which question was answered
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(nullable = false, length = 2000)
    private String answer;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false, updatable = false)
    private Instant submittedAt = Instant.now();

    // ---- getters/setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
}
