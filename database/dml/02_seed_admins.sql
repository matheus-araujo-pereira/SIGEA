-- ====================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- Script DML: Inserção dos Administradores do Sistema
-- 1º Acesso Obrigatório Pendente (must_change_password = TRUE)
-- ====================================================================

INSERT INTO users (id, full_name, email, password_hash, role, registration_number, is_active, must_change_password)
VALUES 
(
    'a1000000-0000-0000-0000-000000000001',
    'Matheus Araujo Pereira',
    'matheusaraujopereira@academico.ufs.br',
    '$2a$12$e2gg/066sAz11ugh4tdsBu9z4hakyHQqxafgz.N8wfcmYrW98xY.2',
    'ADMIN',
    NULL,
    TRUE,
    TRUE
),
(
    'a2000000-0000-0000-0000-000000000001',
    'Profª. Drª. Ana Waleska de Menezes Seixas Souza',
    'anawaleska@academico.ufs.br',
    '$2a$12$e2gg/066sAz11ugh4tdsBu9z4hakyHQqxafgz.N8wfcmYrW98xY.2',
    'ADMIN',
    NULL,
    TRUE,
    TRUE
)
ON CONFLICT (email) DO UPDATE SET
    full_name = EXCLUDED.full_name,
    role = EXCLUDED.role,
    password_hash = EXCLUDED.password_hash,
    must_change_password = EXCLUDED.must_change_password,
    is_active = EXCLUDED.is_active;
