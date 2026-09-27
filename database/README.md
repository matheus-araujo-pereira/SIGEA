# SIGEA — Estrutura e Organização da Base de Dados

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza, Prof. Dr. Gilton José Ferreira da Silva  
**SGBD Homologado**: PostgreSQL 16 LTS  
**Referência Normativa**: Institute for Healthcare Improvement (IHI) — *Global Trigger Tool for Measuring Adverse Events* (2ª Edição)

Este diretório contém a estrutura canônica e otimizada de scripts SQL para inicialização, versionamento e auditoria clínica do sistema **SIGEA** (*Sistema Inteligente de Gestão de Eventos Adversos*).

---

## 📁 Estrutura Unificada do Diretório

A base de dados foi consolidada em arquivos únicos de alta densidade e manutenção simplificada, eliminando fragmentações e pastas vazias:

```
database/
├── 01_schema.sql         # DDL canônico unificado (10 tabelas, enums, restrições e índices)
├── 02_seeds.sql          # DML canônico unificado (6 módulos, 53 gatilhos 100% PDF, 9 gravidades, 2 admins)
├── init_database.sql     # Script orquestrador de inicialização portátil (executa 01_schema e 02_seeds)
└── README.md             # Documentação técnica da base de dados
```

> **Nota**: Não são incluídos modelos/templates pré-cadastrados de casos clínicos nos seeds iniciais. A criação e curadoria dos templates de prontuários simulados é realizada diretamente pela equipe do Hospital Universitário (HU/UFS) através da interface do sistema.

---

## 🏗️ 1. Esquema Canônico (`01_schema.sql`)

O script de DDL contempla a totalidade das tabelas do domínio clínico-acadêmico:
1. **Extensão e Tipos**:
   - `uuid-ossp` para geração de identificadores UUID v4.
   - Enum `user_role` (`'ADMIN'`, `'PROFESSOR'`, `'STUDENT'`).
2. **Tabelas de Autenticação e Usuários**:
   - `users`: Usuários com validação estrita de domínio `@academico.ufs.br`, matrícula obrigatória apenas para discentes e sinalizador de primeiro acesso `must_change_password`.
3. **Tabelas da Metodologia IHI-GTT**:
   - `gtt_modules`: Os 6 módulos de auditoria do IHI-GTT.
   - `gtt_triggers`: Os 53 gatilhos clínicos padronizados com índice e chave estrangeira para os módulos.
   - `harm_severities`: Classificação de gravidades de dano da NCC MERP (Categorias A a I).
4. **Tabelas do Módulo Educacional e Auditoria**:
   - `academic_classes`: Turmas institucionais UFS (*Disciplina - Código - Período*).
   - `class_students`: Matrícula N:M entre turmas e alunos.
   - `activities`: Atividades com casos clínicos simulados em formato `JSONB`.
   - `activity_submissions`: Submissões de alunos com gatilhos detectados, gravidades NCC MERP, ferramentas de qualidade (Ishikawa, 5W2H, GUT, PDCA), nota e feedback.
5. **Modelos de Casos Clínicos Simulados**:
   - `clinical_case_templates`: Biblioteca de modelos clínicos para importação rápida em atividades pelo corpo docente e HU.
6. **Avaliação de Usabilidade do Sistema**:
   - `sus_evaluations`: Instrumento padronizado da Escala de Usabilidade do Sistema (SUS - Brooke, 1996; Bangor et al., 2008).

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

## 🚀 3. Instruções de Execução

### Execução via Podman / Docker
```bash
docker exec -i sigea-postgres psql -U sigea_admin -d sigea < database/init_database.sql
```

### Execução Direta via Terminal `psql`
```bash
psql -U sigea_admin -d sigea -f database/init_database.sql
```
