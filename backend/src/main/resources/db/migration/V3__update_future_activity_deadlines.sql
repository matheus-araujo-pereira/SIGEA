-- SIGEA-GTT: Flyway Migration V3
-- Prorroga o prazo das atividades dos períodos letivos ativos (2026.1 e posteriores) para permitir resolução por estudantes em homologação

UPDATE activities 
SET deadline = '2026-12-31 23:59:59+00' 
WHERE deadline >= '2026-01-01 00:00:00+00';
