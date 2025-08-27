-- student answers to questions
CREATE TABLE IF NOT EXISTS submission (
    id BIGSERIAL PRIMARY KEY,
    student_id  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES question(id) ON DELETE CASCADE,
    answer      VARCHAR(1000),
    correct     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_submission_student_question UNIQUE (student_id, question_id)
);

-- helpful indexes
CREATE INDEX IF NOT EXISTS idx_submission_student ON submission(student_id);
CREATE INDEX IF NOT EXISTS idx_submission_question ON submission(question_id);
