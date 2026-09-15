-- ====================================================================
-- SIGEA-GTT: Sistema Inteligente de Gestão de Eventos Adversos
-- Script Orquestrador de Inicialização Completa do Banco de Dados
-- ====================================================================

\echo '======================================================'
\echo 'Iniciando provisionamento do banco de dados SIGEA-GTT...'
\echo '======================================================'

\echo '1. Aplicando DDL de Estrutura Canônica...'
\i /docker-entrypoint-initdb.d/ddl/01_schema.sql

\echo '2. Aplicando DML Baseline da Metodologia IHI-GTT...'
\i /docker-entrypoint-initdb.d/dml/01_seed_gtt_baseline.sql

\echo '3. Aplicando DML de Carga Hiper-Realista para Homologação...'
\i /docker-entrypoint-initdb.d/dml/03_seed_homologation.sql

\echo '======================================================'
\echo 'Provisionamento concluído com sucesso!'
\echo '======================================================'
