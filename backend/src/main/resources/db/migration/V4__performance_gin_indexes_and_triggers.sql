-- ====================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- Migração Flyway V4: Otimização de Performance, Índices GIN e Triggers
-- Suporte: PostgreSQL 16 LTS / NeonDB Serverless
-- ====================================================================

-- 1. Índices GIN para Pesquisa Acelerada em Estruturas Semiestruturadas JSONB
CREATE INDEX IF NOT EXISTS idx_activities_case_data_gin ON activities USING gin (clinical_case_data);
CREATE INDEX IF NOT EXISTS idx_submissions_triggers_gin ON activity_submissions USING gin (identified_triggers);
CREATE INDEX IF NOT EXISTS idx_submissions_quality_tools_gin ON activity_submissions USING gin (quality_tools_data);
CREATE INDEX IF NOT EXISTS idx_clinical_case_templates_data_gin ON clinical_case_templates USING gin (clinical_case_data);

-- 2. Função de Atualização Automática de Timestamp (updated_at)
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. Triggers para users e clinical_case_templates
DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();

DROP TRIGGER IF EXISTS trg_clinical_case_templates_updated_at ON clinical_case_templates;
CREATE TRIGGER trg_clinical_case_templates_updated_at
    BEFORE UPDATE ON clinical_case_templates
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();

-- 4. Comentários DDL Descritivos
COMMENT ON TABLE users IS 'Usuários autenticados no SIGEA com perfis RBAC e domínio @academico.ufs.br';
COMMENT ON TABLE gtt_modules IS 'Módulos assistenciais especializados da metodologia IHI-GTT (6 módulos oficiais)';
COMMENT ON TABLE gtt_triggers IS '53 gatilhos clínicos padronizados pelo IHI para detecção ativa de eventos adversos';
COMMENT ON TABLE harm_severities IS 'Classificações de gravidade de dano NCC MERP (Categorias A a I)';
COMMENT ON TABLE academic_classes IS 'Turmas acadêmicas da UFS vinculadas a um professor responsável';
COMMENT ON TABLE class_students IS 'Matrícula de alunos nas turmas acadêmicas da UFS';
COMMENT ON TABLE activities IS 'Atividades avaliativas contendo prontuários simulados fictícios em JSONB';
COMMENT ON TABLE activity_submissions IS 'Resoluções individuais de auditoria clínica e ferramentas de qualidade pelos acadêmicos';
COMMENT ON TABLE clinical_case_templates IS 'Biblioteca de modelos de prontuários simulados 100% fictícios (CEP/UFS CAAE nº 91836925.8.0000.5546)';
COMMENT ON TABLE sus_evaluations IS 'Avaliações psicométricas de usabilidade pela System Usability Scale (SUS, Brooke 1996)';
