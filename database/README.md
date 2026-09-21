# SIGEA-GTT — Estrutura e Organização da Base de Dados

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**SGBD Homologado**: PostgreSQL 16 LTS  

Este diretório contém a organização canônica de scripts DDL (definição de dados) e DML (manipulação e carga de dados) do sistema **SIGEA-GTT**.

---

## 📁 Estrutura de Diretórios

```
database/
├── ddl/
│   └── 01_schema.sql                      # Estrutura canônica de tabelas, tipos e constraints
├── dml/
│   ├── 01_seed_gtt_baseline.sql           # Taxonomia oficial IHI-GTT (6 módulos, 53 gatilhos, 9 gravidades)
│   ├── 02_seed_initial_admin.sql          # Administrador padrão da migração base
│   └── 03_seed_homologation.sql           # Carga hiper-realista da fase de homologação (UFS)
├── dados/                                 # Volume persistente do container PostgreSQL (ignorado no Git)
├── generate_seed.py                       # Gerador determinístico dos dados clínicos de homologação
├── init_database.sql                      # Script orquestrador de inicialização sequencial
└── README.md                              # Esta documentação
```

---

## 🏗️ 1. Definição de Dados (DDL)

O arquivo [`ddl/01_schema.sql`](file:///home/matheus/Projetos/SIGEA-GTT/database/ddl/01_schema.sql) define:
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

## 🏥 2. Carga de Dados para Homologação (DML)

O arquivo [`dml/03_seed_homologation.sql`](file:///home/matheus/Projetos/SIGEA-GTT/database/dml/03_seed_homologation.sql) provisiona:

### 👤 Usuários e Credenciais
| Nome Completo | E-mail | Perfil | Matrícula | Senha Padrão |
| :--- | :--- | :---: | :---: | :---: |
| **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `ADMIN` | — | `SigeaUFS@2026` |
| **Profª. Drª. Ana Waleska de Menezes Seixas Souza** | `anawaleska@academico.ufs.br` | `PROFESSOR` | — | `SigeaUFS@2026` |
| **Prof. Dr. Gilton José Ferreira da Silva** | `gilton@academico.ufs.br` | `PROFESSOR` | — | `SigeaUFS@2026` |
| **Lucas Gabriel Fontes** *(Discente Teste)* | `lucas.fontes@academico.ufs.br` | `STUDENT` | `20260001001` | `SigeaUFS@2026` |
| **Mariana Barreto Santana** *(1º Acesso)* | `primeiro.acesso@academico.ufs.br` | `STUDENT` | `20260001030` | `SigeaUFS@2026` |
| *Outros 118 Discentes Simulados* | `*@academico.ufs.br` | `STUDENT` | `2025...` / `2026...` | `SigeaUFS@2026` |

### 📚 Turmas Acadêmicas
1. **Enfermagem na Atenção à Saúde do Adulto e do Idoso I - T01 - 2025.2** (Profª Ana Waleska — Concluída, 30 alunos, 3 atividades).
2. **Gestão da Qualidade e Segurança do Paciente - T01 - 2025.2** (Prof. Gilton — Concluída, 30 alunos, 3 atividades).
3. **Enfermagem em Terapia Intensiva e Cuidados Críticos - T01 - 2026.1** (Profª Ana Waleska — Ativa, 30 alunos, 3 atividades).
4. **Auditoria Clínica e Metodologia Global Trigger Tool - T02 - 2026.1** (Prof. Gilton — Ativa, 30 alunos, 3 atividades).

---

## 🚀 3. Comandos de Execução

### Aplicar carga de homologação via Podman/Docker:
```bash
podman exec -i sigea-postgres psql -U sigea_admin -d sigea_gtt < database/dml/03_seed_homologation.sql
```

### Recriar todo o esquema do zero:
```bash
podman exec -i sigea-postgres psql -U sigea_admin -d sigea_gtt < database/ddl/01_schema.sql
podman exec -i sigea-postgres psql -U sigea_admin -d sigea_gtt < database/dml/01_seed_gtt_baseline.sql
podman exec -i sigea-postgres psql -U sigea_admin -d sigea_gtt < database/dml/03_seed_homologation.sql
```
