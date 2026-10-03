# SIGEA — Estrutura e Organização da Base de Dados

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Enfermagem  
**Autor Líder**: Matheus Araujo Pereira (Matrícula: 202100114080)  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza, Prof. Dr. Gilton José Ferreira da Silva  
**Aprovação Ética**: CEP/UFS — CAAE nº 91836925.8.0000.5546  
**SGBD Homologado**: PostgreSQL 16 LTS / Neon.tech Serverless Postgres  
**Referência Normativa**: Institute for Healthcare Improvement (IHI) — *Global Trigger Tool for Measuring Adverse Events* (2ª Edição)

Este diretório contém a estrutura canônica, modular e otimizada de scripts SQL para inicialização, versionamento e auditoria clínica do sistema **SIGEA** (*Sistema Inteligente de Gestão de Eventos Adversos*).

---

## 📁 Estrutura Canônica do Diretório

A base de dados organiza-se em scripts de alta densidade técnica e idempotência rigorosa:

```
database/
├── 01_schema.sql             # DDL canônico (10 tabelas, enums, índices GIN em JSONB, triggers e comentários)
├── 02_seeds.sql              # DML canônico (6 módulos, 53 gatilhos oficiais, 9 gravidades NCC MERP, 2 admins)
├── 03_clinical_templates.sql # DML de templates de prontuários simulados (6 casos clínicos fictícios do HU/UFS)
├── init_database.sql         # Script orquestrador de inicialização portátil (executa 01, 02 e 03)
└── README.md                 # Documentação técnica da base de dados
```

---

## 🏗️ 1. Esquema Canônico (`01_schema.sql`)

O script DDL contempla as 10 tabelas do domínio clínico-acadêmico:

1. **Extensão e Tipos**:
   - `uuid-ossp` para geração de identificadores universais UUID v4.
   - Enum `user_role` (`'ADMIN'`, `'PROFESSOR'`, `'STUDENT'`).
2. **Função Trigger de Auditoria**:
   - `trigger_set_timestamp()` para atualização automática de `updated_at` em `users` e `clinical_case_templates`.
3. **Tabelas de Autenticação e Usuários**:
   - `users`: Usuários com validação estrita de domínio `@academico.ufs.br`, matrícula obrigatória apenas para discentes e sinalizador de primeiro acesso `must_change_password`.
4. **Tabelas da Metodologia IHI-GTT**:
   - `gtt_modules`: Os 6 módulos de auditoria do IHI-GTT.
   - `gtt_triggers`: Os 53 gatilhos clínicos padronizados com chave estrangeira e restrição de integridade.
   - `harm_severities`: Classificação de gravidades de dano da NCC MERP (Categorias A a I: E a I configuram EAs com dano).
5. **Tabelas do Módulo Educacional e Auditoria**:
   - `academic_classes`: Turmas institucionais UFS (*Disciplina - Código - Período*).
   - `class_students`: Matrícula N:M entre turmas e alunos.
   - `activities`: Atividades com casos clínicos simulados em formato `JSONB` com índice GIN.
   - `activity_submissions`: Submissões individuais com índices GIN em `identified_triggers` e `quality_tools_data` (Ishikawa, 5W2H, GUT, PDCA, SWOT, Brainstorming).
6. **Modelos de Casos Clínicos Simulados**:
   - `clinical_case_templates`: Biblioteca de modelos clínicos com índice GIN em `clinical_case_data`.
7. **Avaliação de Usabilidade do Sistema**:
   - `sus_evaluations`: Instrumento psicométrico padronizado da Escala de Usabilidade do Sistema (SUS - Brooke, 1996).

---

## 🏥 2. Carga de Dados Oficiais (`02_seeds.sql`)

Todos os dados inseridos em `02_seeds.sql` seguem **100% de correspondência textual** com o documento de referência oficial `docs/referencias/metodologia_global_trigger_tool.pdf`:

### 👤 Usuários Administradores Iniciais
Inicializados com primeiro acesso obrigatório pendente (`must_change_password = true`):

| Nome Completo | E-mail Institucional | Perfil | Matrícula | Senha Provisória | 1º Acesso |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `ADMIN` | *null* | `SigeaUFS@2026` | Pendente (`true`) |
| **Profª. Drª. Ana Waleska de Menezes Seixas Souza** | `anawaleska@academico.ufs.br` | `ADMIN` | *null* | `SigeaUFS@2026` | Pendente (`true`) |

### 📚 Módulos e Gatilhos IHI-GTT (53 Gatilhos Oficiais)
- **Módulo Cuidados (`CUIDADOS`)**: 15 gatilhos (C1 a C15).
- **Módulo Medicação (`MEDICACAO`)**: 13 gatilhos (M1 a M13).
- **Módulo Cirúrgico (`CIRURGICO`)**: 11 gatilhos (S1 a S11).
- **Módulo Cuidados Intensivos/Terapia Intensiva (`UTI`)**: 4 gatilhos (I1 a I4).
- **Módulo Perinatal (`PERINATAL`)**: 8 gatilhos (P1 a P8).
- **Módulo Serviço de Urgência/Pronto Atendimento (`URGENCIA`)**: 2 gatilhos (E1 e E2).

### ⚖️ Gravidades de Dano NCC MERP
- **Sem Dano (`is_harm = false`)**: Categorias A, B, C e D.
- **Com Dano / Eventos Adversos (`is_harm = true`)**: Categorias E, F, G, H e I.

---

## 📋 3. Casos Clínicos Simulados do HU/UFS (`03_clinical_templates.sql`)

Contém 6 casos clínicos simulados abrangendo cada um dos módulos do IHI-GTT (IRA por Vancomicina, Queda com Fratura, Parada Cardíaca no Bloco Cirúrgico, Pneumonia por Ventilação Mecânica / PAV, Hemorragia Pós-Parto e Hipoglicemia Severa na Emergência). Todos os prontuários são **100% fictícios** em conformidade estrita com o CEP/UFS (CAAE nº 91836925.8.0000.5546).

---

## 🚀 4. Instruções de Execução

### Execução via Podman / Docker
```bash
docker exec -i sigea-postgres psql -U sigea_admin -d sigea < database/init_database.sql
```

### Execução em Nuvem (Neon.tech PostgreSQL Serverless)
```bash
psql "postgres://[user]:[password]@[endpoint].neon.tech/sigea?sslmode=require" -f database/init_database.sql
```
