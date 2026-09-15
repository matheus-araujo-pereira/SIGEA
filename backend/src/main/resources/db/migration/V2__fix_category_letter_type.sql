-- SIGEA-GTT: Flyway Migration V2
-- Ajuste de compatibilidade: converte CHAR(1) para VARCHAR(1) em harm_severities.category_letter
-- Motivo: Hibernate 6 mapeia String → varchar; PostgreSQL CHAR(1) = bpchar, causando falha de schema-validation.
-- Semanticamente equivalente para uma coluna de letra única (A-I).

ALTER TABLE harm_severities
    ALTER COLUMN category_letter TYPE VARCHAR(1) USING TRIM(category_letter);
