-- ====================================================================
-- SIGEA-GTT: Sistema Inteligente de Gestão de Eventos Adversos
-- Script DML: Administrador Base Original do Sistema
-- Nota: Para o ambiente de homologação, utilize o script 03_seed_homologation.sql
-- ====================================================================

INSERT INTO users (id, full_name, email, password_hash, role, registration_number, is_active, must_change_password)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'Administrador do Sistema',
    'admin.sigea@academico.ufs.br',
    '$2a$12$e2gg/066sAz11ugh4tdsBu9z4hakyHQqxafgz.N8wfcmYrW98xY.2', -- SigeaUFS@2026
    'ADMIN',
    NULL,
    TRUE,
    TRUE
)
ON CONFLICT (email) DO NOTHING;
