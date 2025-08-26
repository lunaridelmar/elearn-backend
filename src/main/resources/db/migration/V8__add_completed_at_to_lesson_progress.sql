-- Add missing completed_at column for LessonProgress
ALTER TABLE lesson_progress
    ADD COLUMN IF NOT EXISTS completed_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
