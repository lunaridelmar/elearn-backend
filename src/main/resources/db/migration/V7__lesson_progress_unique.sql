CREATE TABLE IF NOT EXISTS lesson_progress (
  id           BIGSERIAL PRIMARY KEY,
  student_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  lesson_id    BIGINT NOT NULL REFERENCES lesson(id) ON DELETE CASCADE,
  completed    BOOLEAN NOT NULL DEFAULT FALSE,
  completed_at TIMESTAMPTZ,
  CONSTRAINT uq_lesson_progress UNIQUE (student_id, lesson_id)
);
