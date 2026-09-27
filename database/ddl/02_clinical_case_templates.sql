-- ====================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- DDL: Tabela de Modelos de Casos Clínicos Simulados
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
