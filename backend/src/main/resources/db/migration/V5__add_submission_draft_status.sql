-- =====================================================================================
-- SIGEA - Migration V5: Suporte a Rascunho Automático (Auto-Save) em Submissões
-- Migration estritamente aditiva e retrocompatível com dados de homologação.
-- Todas as submissões existentes antes desta migration permanecem definitivas (FALSE).
-- =====================================================================================

ALTER TABLE activity_submissions
    ADD COLUMN IF NOT EXISTS is_draft BOOLEAN NOT NULL DEFAULT FALSE;
