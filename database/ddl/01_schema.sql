-- ====================================================================
-- SIGEA-GTT: Sistema Inteligente de Gestão de Eventos Adversos
-- Script DDL: Estrutura Canônica do Banco de Dados
-- Compatibilidade: PostgreSQL 16 LTS
-- Autor Líder: Matheus Araujo Pereira (UFS - DCOMP / Enfermagem)
-- ====================================================================

-- 1. Extensão para Geração de Identificadores Únicos (UUID v4)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Tipo Enumerado para Perfis de Usuário
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('ADMIN', 'PROFESSOR', 'STUDENT');
    END IF;
END$$;

-- ====================================================================
-- TABELAS DE AUTENTICAÇÃO E USUÁRIOS
-- ====================================================================

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    registration_number VARCHAR(30) NULL, -- Obrigatório apenas para STUDENT; nulo para ADMIN/PROFESSOR
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_email_domain CHECK (email LIKE '%@academico.ufs.br'),
    CONSTRAINT chk_registration_required CHECK (
        (role = 'STUDENT' AND registration_number IS NOT NULL AND TRIM(registration_number) <> '') OR
        (role IN ('ADMIN', 'PROFESSOR') AND registration_number IS NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- ====================================================================
-- TABELAS DE METODOLOGIA IHI-GTT
-- ====================================================================

CREATE TABLE IF NOT EXISTS gtt_modules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_gtt_modules_code ON gtt_modules(code);

CREATE TABLE IF NOT EXISTS gtt_triggers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    module_id UUID NOT NULL REFERENCES gtt_modules(id) ON DELETE RESTRICT,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_gtt_triggers_code ON gtt_triggers(code);
CREATE INDEX IF NOT EXISTS idx_gtt_triggers_module ON gtt_triggers(module_id);

CREATE TABLE IF NOT EXISTS harm_severities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_letter VARCHAR(1) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    is_harm BOOLEAN NOT NULL, -- True para E, F, G, H, I; False para A, B, C, D
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_harm_severities_letter ON harm_severities(category_letter);

-- ====================================================================
-- TABELAS DO MÓDULO EDUCACIONAL
-- ====================================================================

CREATE TABLE IF NOT EXISTS academic_classes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subject_name VARCHAR(120) NOT NULL,
    class_code VARCHAR(20) NOT NULL,
    academic_period VARCHAR(10) NOT NULL, -- Ex: 2025.2, 2026.1
    professor_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_class_identifier UNIQUE (subject_name, class_code, academic_period)
);

CREATE INDEX IF NOT EXISTS idx_classes_professor ON academic_classes(professor_id);
CREATE INDEX IF NOT EXISTS idx_classes_period ON academic_classes(academic_period);

CREATE TABLE IF NOT EXISTS class_students (
    class_id UUID NOT NULL REFERENCES academic_classes(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_class_students_student ON class_students(student_id);

CREATE TABLE IF NOT EXISTS activities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    class_id UUID NOT NULL REFERENCES academic_classes(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    clinical_case_data JSONB NOT NULL, -- Prontuário simulado completo
    deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_activities_class ON activities(class_id);

CREATE TABLE IF NOT EXISTS activity_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    activity_id UUID NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    identified_triggers JSONB NOT NULL, -- Gatilhos identificados e gravidades
    quality_tools_data JSONB NOT NULL, -- Ferramentas de qualidade (Ishikawa, 5W2H, GUT, PDCA)
    submission_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    grade NUMERIC(4,2) NULL CHECK (grade >= 0.0 AND grade <= 10.0),
    professor_feedback TEXT NULL,
    graded_at TIMESTAMP WITH TIME ZONE NULL,
    CONSTRAINT uk_activity_student UNIQUE (activity_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_submissions_activity ON activity_submissions(activity_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student ON activity_submissions(student_id);
