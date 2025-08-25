-- Optional: only for local/dev
INSERT INTO course (title, description)
VALUES ('Java Basics', 'Introductory Java course')
ON CONFLICT (title) DO NOTHING;

WITH c AS (
  SELECT id FROM course WHERE title = 'Java Basics'
)
INSERT INTO lesson (title, content, course_id)
SELECT 'Variables & Types', 'Content for variables...', c.id FROM c
ON CONFLICT DO NOTHING;

WITH c AS (
  SELECT id FROM course WHERE title = 'Java Basics'
)
INSERT INTO lesson (title, content, course_id)
SELECT 'Control Flow', 'Content for control flow...', c.id FROM c
ON CONFLICT DO NOTHING;
-- Promote an existing user to TEACHER
UPDATE users SET role = 'TEACHER' WHERE email = 'alice@example.com';
