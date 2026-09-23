# SIGEA — Estrutura e Organização da Base de Dados

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**SGBD Homologado**: PostgreSQL 16 LTS  

Este diretório contém a organização canônica de scripts DDL (definição de dados) e DML (manipulação e carga de dados) do sistema **SIGEA** (*Sistema Inteligente de Gestão de Eventos Adversos*).

---

## 📁 Estrutura de Diretórios

```
database/
├── ddl/
│   └── 01_schema.sql                      # Estrutura canônica de tabelas, tipos e constraints
├── dml/
│   ├── 01_seed_gtt_baseline.sql           # Taxonomia oficial IHI-GTT (6 módulos, 53 gatilhos, 9 gravidades)
│   └── 02_seed_admins.sql                 # Administradores do sistema com 1º acesso pendente
├── dados/                                 # Volume persistente do container PostgreSQL (ignorado no Git)
├── init_database.sql                      # Script orquestrador de inicialização sequencial
└── README.md                              # Esta documentação
```

---

## 🏗️ 1. Definição de Dados (DDL)

O arquivo [`ddl/01_schema.sql`](ddl/01_schema.sql) define:
- Extensão `uuid-ossp` para geração de identificadores universais únicos (UUID v4).
- Tipo enumerado `user_role` com valores estritos: `'ADMIN'`, `'PROFESSOR'`, `'STUDENT'`.
- Tabelas:
  1. `users`: Controle de acesso com regra de e-mail institucional `@academico.ufs.br` e matrícula obrigatória somente para discentes.
  2. `gtt_modules`: Os 6 módulos de auditoria do Institute for Healthcare Improvement (IHI).
  3. `gtt_triggers`: Os 53 gatilhos clínicos padronizados com vínculo aos módulos.
  4. `harm_severities`: Classificação de gravidades de dano da NCC MERP (Categorias A a I).
  5. `academic_classes`: Turmas no padrão institucional UFS (*Disciplina - Código - Período*).
  6. `class_students`: Associação N:M entre turmas e discentes matriculados.
  7. `activities`: Atividades avaliativas com prontuários simulados armazenados em formato `JSONB`.
  8. `activity_submissions`: Resoluções dos alunos contendo gatilhos identificados, ferramentas de qualidade (Ishikawa, 5W2H, GUT, PDCA), nota e feedback docente.

---

## 🏥 2. Carga de Dados Inicial (DML)

### 👤 Usuários Administradores Iniciais
Conforme diretriz de provisionamento limpo, o sistema é inicializado apenas com os dois administradores, com primeiro acesso obrigatório pendente (`must_change_password = true`):

| Nome Completo | E-mail Institucional | Perfil | Matrícula | Senha Provisória | Status do 1º Acesso |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `ADMIN` | — | `sigea123` | Pendente (`true`) |
| **Profª. Drª. Ana Waleska de Menezes Seixas Souza** | `anawaleska@academico.ufs.br` | `ADMIN` | — | `sigea123` | Pendente (`true`) |

### 📚 Catálogo Base IHI-GTT
- **6 Módulos Especializados**: Cuidados, Medicação, Cirúrgico, UTI, Perinatal e Urgência.
- **53 Gatilhos Clínicos Padronizados**: Códigos C1–C15, M1–M13, S1–S11, I1–I4, P1–P8 e E1–E2 (todos ativos).
- **9 Gravidades de Dano NCC MERP**: Categorias A a I (com `is_harm = true` para E a I).
- Zero turmas, alunos e atividades previamente cadastradas.

---

## 🚀 3. Comandos de Execução

### Execução via Podman/Docker:
```bash
docker exec -i sigea-postgres psql -U sigea_admin -d sigea < database/init_database.sql
```
