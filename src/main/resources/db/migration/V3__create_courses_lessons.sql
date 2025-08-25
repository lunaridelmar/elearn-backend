-- Courses
CREATE TABLE IF NOT EXISTS course (
  id           BIGSERIAL PRIMARY KEY,
  title        VARCHAR(200) NOT NULL UNIQUE,
  description  TEXT,
  created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Lessons (each belongs to a course)
CREATE TABLE IF NOT EXISTS lesson (
  id         BIGSERIAL PRIMARY KEY,
  title      VARCHAR(200) NOT NULL,
  content    TEXT,
  course_id  BIGINT NOT NULL REFERENCES course(id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_lesson_course_id ON lesson(course_id);
CREATE INDEX IF NOT EXISTS idx_course_title ON course(title);
