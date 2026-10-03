-- ====================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- Esquema Canônico Completo do Banco de Dados (DDL Unificado)
-- SGBD Homologado: PostgreSQL 16 LTS / Neon.tech Serverless
-- Instituição: Universidade Federal de Sergipe (UFS) - DCOMP / Enfermagem
-- Autor Líder: Matheus Araujo Pereira (Matrícula: 202100114080)
-- Orientadores: Profª. Drª. Ana Waleska de Menezes Seixas Souza
--               Prof. Dr. Gilton José Ferreira da Silva
-- Aprovação Ética: CEP/UFS - CAAE nº 91836925.8.0000.5546
-- ====================================================================

-- 1. Extensão para Geração de Identificadores Únicos Universais (UUID v4)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. Tipo Enumerado para Perfis de Acesso Institucionais (RBAC)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('ADMIN', 'PROFESSOR', 'STUDENT');
    END IF;
END$$;

-- 3. Função Trigger para Atualização Automática de Timestamp (updated_at)
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ====================================================================
-- 4. TABELAS DE AUTENTICAÇÃO E GESTÃO DE USUÁRIOS
-- ====================================================================

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    registration_number VARCHAR(30) NULL, -- Obrigatório apenas para STUDENT; nulo para ADMIN e PROFESSOR
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

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();

COMMENT ON TABLE users IS 'Usuários autenticados no SIGEA com perfis RBAC e domínio @academico.ufs.br';
COMMENT ON COLUMN users.registration_number IS 'Matrícula acadêmica institucional (exclusiva para perfil STUDENT)';
COMMENT ON COLUMN users.must_change_password IS 'Sinalizador obrigatório de redefinição de senha no primeiro login';

-- ====================================================================
-- 5. TABELAS DA METODOLOGIA GLOBAL TRIGGER TOOL (IHI-GTT)
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

COMMENT ON TABLE gtt_modules IS 'Módulos assistenciais especializados da metodologia IHI-GTT (6 módulos oficiais)';

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

COMMENT ON TABLE gtt_triggers IS '53 gatilhos clínicos padronizados pelo IHI para detecção ativa de eventos adversos';

CREATE TABLE IF NOT EXISTS harm_severities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_letter VARCHAR(1) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    is_harm BOOLEAN NOT NULL, -- FALSE para Categorias A a D; TRUE para Categorias E a I
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_harm_severities_letter ON harm_severities(category_letter);

COMMENT ON TABLE harm_severities IS 'Classificações de gravidade de dano NCC MERP (Categorias A a I: E a I são EAs com dano)';

-- ====================================================================
-- 6. TABELAS DO MÓDULO EDUCACIONAL E AUDITORIA CLÍNICA
-- ====================================================================

CREATE TABLE IF NOT EXISTS academic_classes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subject_name VARCHAR(120) NOT NULL,
    class_code VARCHAR(20) NOT NULL,
    academic_period VARCHAR(10) NOT NULL, -- Exemplo: 2025.2, 2026.1
    professor_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_class_identifier UNIQUE (subject_name, class_code, academic_period)
);

CREATE INDEX IF NOT EXISTS idx_classes_professor ON academic_classes(professor_id);
CREATE INDEX IF NOT EXISTS idx_classes_period ON academic_classes(academic_period);

COMMENT ON TABLE academic_classes IS 'Turmas acadêmicas da UFS vinculadas a um professor responsável';

CREATE TABLE IF NOT EXISTS class_students (
    class_id UUID NOT NULL REFERENCES academic_classes(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    enrolled_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_class_students_student ON class_students(student_id);

COMMENT ON TABLE class_students IS 'Matrícula de alunos nas turmas acadêmicas da UFS';

CREATE TABLE IF NOT EXISTS activities (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    class_id UUID NOT NULL REFERENCES academic_classes(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    clinical_case_data JSONB NOT NULL, -- Prontuário hospitalar simulado completo
    deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_activities_class ON activities(class_id);
CREATE INDEX IF NOT EXISTS idx_activities_case_data_gin ON activities USING gin (clinical_case_data);

COMMENT ON TABLE activities IS 'Atividades avaliativas contendo prontuários simulados fictícios em JSONB';

CREATE TABLE IF NOT EXISTS activity_submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    activity_id UUID NOT NULL REFERENCES activities(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    identified_triggers JSONB NOT NULL, -- Gatilhos identificados e categorias de dano NCC MERP
    quality_tools_data JSONB NOT NULL, -- Análise causal (Ishikawa, 5W2H, GUT, PDCA, SWOT, Brainstorming)
    submission_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    grade NUMERIC(4,2) NULL CHECK (grade >= 0.0 AND grade <= 10.0),
    professor_feedback TEXT NULL,
    graded_at TIMESTAMP WITH TIME ZONE NULL,
    CONSTRAINT uk_activity_student UNIQUE (activity_id, student_id)
);

CREATE INDEX IF NOT EXISTS idx_submissions_activity ON activity_submissions(activity_id);
CREATE INDEX IF NOT EXISTS idx_submissions_student ON activity_submissions(student_id);
CREATE INDEX IF NOT EXISTS idx_submissions_triggers_gin ON activity_submissions USING gin (identified_triggers);
CREATE INDEX IF NOT EXISTS idx_submissions_quality_tools_gin ON activity_submissions USING gin (quality_tools_data);

COMMENT ON TABLE activity_submissions IS 'Resoluções individuais de auditoria clínica e ferramentas de qualidade pelos acadêmicos';

-- ====================================================================
-- 7. TABELA DE MODELOS DE CASOS CLÍNICOS SIMULADOS (TEMPLATES DO HU)
-- ====================================================================

CREATE TABLE IF NOT EXISTS clinical_case_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    module_code VARCHAR(20) NOT NULL,
    primary_trigger_code VARCHAR(10) NOT NULL,
    expected_severity VARCHAR(5) NOT NULL,
    clinical_case_data JSONB NOT NULL,
    is_system_template BOOLEAN NOT NULL DEFAULT FALSE,
    created_by UUID NULL REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_clinical_case_templates_module ON clinical_case_templates(module_code);
CREATE INDEX IF NOT EXISTS idx_clinical_case_templates_system ON clinical_case_templates(is_system_template);
CREATE INDEX IF NOT EXISTS idx_clinical_case_templates_data_gin ON clinical_case_templates USING gin (clinical_case_data);

DROP TRIGGER IF EXISTS trg_clinical_case_templates_updated_at ON clinical_case_templates;
CREATE TRIGGER trg_clinical_case_templates_updated_at
    BEFORE UPDATE ON clinical_case_templates
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();

COMMENT ON TABLE clinical_case_templates IS 'Biblioteca de modelos de prontuários simulados 100% fictícios (CEP/UFS CAAE nº 91836925.8.0000.5546)';

-- ====================================================================
-- 8. TABELA DA ESCALA DE USABILIDADE DO SISTEMA (SUS - BROOKE, 1996)
-- ====================================================================

CREATE TABLE IF NOT EXISTS sus_evaluations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    student_id UUID NOT NULL,
    academic_class_id UUID,
    q1 INT NOT NULL CHECK (q1 BETWEEN 1 AND 5),
    q2 INT NOT NULL CHECK (q2 BETWEEN 1 AND 5),
    q3 INT NOT NULL CHECK (q3 BETWEEN 1 AND 5),
    q4 INT NOT NULL CHECK (q4 BETWEEN 1 AND 5),
    q5 INT NOT NULL CHECK (q5 BETWEEN 1 AND 5),
    q6 INT NOT NULL CHECK (q6 BETWEEN 1 AND 5),
    q7 INT NOT NULL CHECK (q7 BETWEEN 1 AND 5),
    q8 INT NOT NULL CHECK (q8 BETWEEN 1 AND 5),
    q9 INT NOT NULL CHECK (q9 BETWEEN 1 AND 5),
    q10 INT NOT NULL CHECK (q10 BETWEEN 1 AND 5),
    score DOUBLE PRECISION NOT NULL CHECK (score >= 0 AND score <= 100),
    adjective_rating VARCHAR(50) NOT NULL,
    acceptability VARCHAR(50) NOT NULL,
    grade_level VARCHAR(10) NOT NULL,
    suggestions TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sus_student FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sus_academic_class FOREIGN KEY (academic_class_id) REFERENCES academic_classes(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_sus_student ON sus_evaluations(student_id);
CREATE INDEX IF NOT EXISTS idx_sus_class ON sus_evaluations(academic_class_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_sus_student_class ON sus_evaluations(student_id, academic_class_id) WHERE academic_class_id IS NOT NULL;

COMMENT ON TABLE sus_evaluations IS 'Avaliações psicométricas de usabilidade pela System Usability Scale (SUS, Brooke 1996)';
