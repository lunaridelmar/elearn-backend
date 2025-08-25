-- Ensure the sample course exists
INSERT INTO course (title, description)
VALUES ('Spring Boot 101', 'Getting started with Spring Boot')
ON CONFLICT (title) DO NOTHING;

-- Ensure a sample lesson exists under that course
WITH c AS (
  SELECT id FROM course WHERE title = 'Spring Boot 101'
)
INSERT INTO lesson (title, content, course_id)
SELECT 'Intro', 'Welcome to Spring Boot!', c.id FROM c
ON CONFLICT DO NOTHING;

-- Create a quiz for the lesson if not present yet
WITH l AS (
  SELECT l.id
  FROM lesson l
  JOIN course c ON c.id = l.course_id
  WHERE c.title = 'Spring Boot 101' AND l.title = 'Intro'
),
q AS (
  -- only insert quiz if none exists for this lesson
  INSERT INTO quiz (title, lesson_id)
  SELECT 'Intro Quiz', l.id FROM l
  WHERE NOT EXISTS (SELECT 1 FROM quiz WHERE lesson_id = l.id)
  RETURNING id, lesson_id
)
-- Add questions if the quiz was just inserted (or if it existed, insert with WHERE NOT EXISTS)
INSERT INTO quiz_question (question, correct_answer, quiz_id)
SELECT 'What annotation starts a Spring Boot app?', '@SpringBootApplication', q.id FROM q
UNION ALL
SELECT 'Default HTTP port for Spring Boot?', '8080', q.id FROM q;

-- If quiz already existed, ensure at least these questions exist
WITH l AS (
  SELECT l.id
  FROM lesson l
  JOIN course c ON c.id = l.course_id
  WHERE c.title = 'Spring Boot 101' AND l.title = 'Intro'
),
qe AS (
  SELECT id FROM quiz WHERE lesson_id IN (SELECT id FROM l)
)
INSERT INTO quiz_question (question, correct_answer, quiz_id)
SELECT 'What annotation starts a Spring Boot app?', '@SpringBootApplication', q.id
FROM qe q
WHERE NOT EXISTS (
  SELECT 1 FROM quiz_question WHERE quiz_id = q.id AND question = 'What annotation starts a Spring Boot app?'
);

WITH l AS (
  SELECT l.id
  FROM lesson l
  JOIN course c ON c.id = l.course_id
  WHERE c.title = 'Spring Boot 101' AND l.title = 'Intro'
),
qe AS (
  SELECT id FROM quiz WHERE lesson_id IN (SELECT id FROM l)
)
INSERT INTO quiz_question (question, correct_answer, quiz_id)
SELECT 'Default HTTP port for Spring Boot?', '8080', q.id
FROM qe q
WHERE NOT EXISTS (
  SELECT 1 FROM quiz_question WHERE quiz_id = q.id AND question = 'Default HTTP port for Spring Boot?'
);
