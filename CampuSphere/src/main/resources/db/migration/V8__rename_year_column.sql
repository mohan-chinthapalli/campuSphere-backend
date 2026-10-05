-- ============================================================
-- V8: Rename student_profiles.year → academic_year
-- PostgreSQL note: V1 already creates this column as academic_year
-- so this migration is a no-op on a fresh PostgreSQL install.
-- This migration exists to maintain Flyway version history parity
-- with older MySQL installs that had the column named 'year'.
-- On PostgreSQL this migration validates successfully as a no-op.
-- ============================================================

-- No-op: column is already named academic_year in V1 (PostgreSQL)
SELECT 1;
