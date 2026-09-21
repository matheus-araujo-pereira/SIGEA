-- SIGEA-GTT: Flyway Migration V5
-- Atualização do nome da professora coorientadora Ana Waleska de Menezes Seixas Souza

UPDATE users
SET full_name = 'Profª. Drª. Ana Waleska de Menezes Seixas Souza'
WHERE email = 'anawaleska@academico.ufs.br';
