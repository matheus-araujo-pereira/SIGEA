-- ====================================================================
-- SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
-- Script Orquestrador de Inicialização Completa do Banco de Dados
-- SGBD Homologado: PostgreSQL 16 LTS
-- Instituição: Universidade Federal de Sergipe (UFS) - DCOMP / Enfermagem
-- ====================================================================

\echo '===================================================================='
\echo 'Iniciando provisionamento canônico do banco de dados SIGEA (PostgreSQL 16)...'
\echo '===================================================================='

\echo '1. Aplicando DDL Canônico Unificado (Esquema, Tabelas, Índices e Enums)...'
\ir 01_schema.sql

\echo '2. Aplicando DML Canônico Unificado (Módulos, 53 Gatilhos IHI-GTT, Gravidades NCC MERP e Administradores)...'
\ir 02_seeds.sql

\echo '3. Aplicando Modelos Canônicos de Casos Clínicos Simulados (Templates HU/UFS em JSONB)...'
\ir 03_clinical_templates.sql

\echo '===================================================================='
\echo 'Provisionamento concluído com 100% de conformidade com o protocolo IHI-GTT!'
\echo '===================================================================='
