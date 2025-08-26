-- Ensure lesson_progress.id is auto-generated

-- 1) Create a sequence if it's missing
CREATE SEQUENCE IF NOT EXISTS lesson_progress_id_seq;

-- 2) Attach the sequence as the default for id
ALTER TABLE lesson_progress
    ALTER COLUMN id SET DEFAULT nextval('lesson_progress_id_seq');

-- 3) Make the sequence owned by the column (so it's dropped with the column/table)
ALTER SEQUENCE lesson_progress_id_seq OWNED BY lesson_progress.id;

-- 4) Advance sequence to > current max(id) to avoid conflicts
SELECT setval(
  'lesson_progress_id_seq',
  COALESCE((SELECT MAX(id) FROM lesson_progress), 0) + 1,
  false
);
