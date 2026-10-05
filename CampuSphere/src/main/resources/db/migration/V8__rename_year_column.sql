-- ============================================================
-- V8: Rename student_profiles.year → academic_year
-- Root cause: "year" is a reserved keyword in H2 (used for tests)
-- and causes JdbcSQLSyntaxErrorException during schema generation.
-- Using an explicit safe column name keeps MySQL and H2 compatible.
-- ============================================================

ALTER TABLE student_profiles
    CHANGE COLUMN `year` `academic_year` VARCHAR(50) NOT NULL;
