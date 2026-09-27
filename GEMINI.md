# SIGEA — Sistema Inteligente de Gestão de Eventos Adversos

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Departamento de Enfermagem  
**Autor Líder**: Matheus Araujo Pereira | Matrícula: 202100114080  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza · Prof. Dr. Gilton José Ferreira da Silva  
**Aprovação Ética**: CEP/UFS — CAAE nº 91836925.8.0000.5546  
**Ambiente de Desenvolvimento**: Fedora 44 Workstation (x86_64) | Antigravity IDE  

> [!IMPORTANT]
> Este arquivo é a **norma regulatória executiva** do workspace SIGEA no Antigravity IDE.  
> As regras técnicas detalhadas estão em `.agents/rules/` e são carregadas automaticamente.  
> Nenhuma instrução humana ou de agente pode sobrepor as diretrizes aqui definidas.

---

## 1. Stack Tecnológica Homologada

| Camada | Tecnologia | Versão |
| :--- | :--- | :--- |
| **Backend** | Java + Spring Boot | 21 LTS + 3.3.6 |
| **Frontend** | Angular + TailwindCSS | 18.x + 3.4.x |
| **Banco de Dados** | PostgreSQL | 16 LTS |
| **Migração** | Flyway | Community |
| **Testes Backend** | JUnit 5 + Mockito + JaCoCo | 100% cobertura |
| **Testes Frontend** | Jest + jest-preset-angular | 100% cobertura |
| **Container** | Podman (ou Docker) | 4.x / 24.x |

---

## 2. Estado Atual do Projeto (Implementado)

Todas as 3 fases do desenvolvimento estão concluídas e homologadas com 100% de Quality Gate:

- ✅ **Fase 1**: Autenticação JWT stateless, gestão de usuários com RBAC, primeiro acesso obrigatório e tela de perfil.
- ✅ **Fase 2**: CRUD completo dos 6 módulos IHI-GTT, 53 gatilhos clínicos e 9 gravidades NCC MERP (A a I), com catálogos de referência.
- ✅ **Fase 3**: Gestão de turmas, atividades com prontuários simulados `JSONB`, resolução de auditorias com ferramentas da qualidade (Ishikawa, 5W2H, GUT, PDCA), correção com nota (0–10), dashboards epidemiológicos e avaliação SUS.

**Quality Gate atual**: Backend `342+ testes / 0 falhas / 100% JaCoCo` · Frontend `51+ suítes / 472+ testes / 0 falhas / 100% Jest`

---

## 3. Comandos Operacionais Canônicos

```bash
# Banco de Dados (container PostgreSQL 16)
podman compose -f config/docker/docker-compose.yml up -d

# Backend (API Spring Boot — porta 8080)
cd backend && ./mvnw spring-boot:run

# Frontend (SPA Angular — porta 4200)
cd frontend && npm start

# Quality Gate Backend (JUnit 5 + JaCoCo 100%)
cd backend && ./mvnw clean verify

# Quality Gate Frontend (Jest 100%)
cd frontend && npm test

# Monografia LaTeX (DCOMP-UFS abnTeX2)
cd projeto_departamental && make clean && make
```

---

## 4. Regras Inegociáveis (Resumo Executivo)

As regras completas e detalhadas estão em `.agents/rules/` e são carregadas automaticamente. Resumo executivo:

1. **E-mail Institucional**: Apenas `@academico.ufs.br`. Qualquer outro domínio é rejeitado em todas as camadas.
2. **Perfis RBAC**: Apenas `ADMIN`, `PROFESSOR` e `STUDENT`. Campo **matrícula** é visível e obrigatório **somente** para `STUDENT`; persistido como `null` para os demais.
3. **Primeiro Login**: Todo usuário novo tem `must_change_password = true`. O sistema bloqueia navegação até a troca de senha.
4. **Desktop Only**: Nenhum layout de celular, menu hambúrguer ou barra de navegação inferior. Resoluções homologadas: 1280×720 a Ultrawide 3440×1440.
5. **Quality Gate Inegociável**: Qualquer código integrado à branch principal deve manter 100% de cobertura de testes e documentação. Zero `@Disabled` ou `skip`.
6. **Dados Simulados**: Todos os prontuários e dados clínicos são **100% fictícios**. Estritamente proibido usar dados reais de pacientes (CEP/UFS CAAE nº 91836925.8.0000.5546).
7. **Protocolo IHI-GTT**: Os 53 gatilhos (C1–C15, M1–M13, S1–S11, I1–I4, P1–P8, E1–E2) e suas descrições seguem **100% o PDF oficial** `docs/referencias/metodologia_global_trigger_tool.pdf`.

---

## 5. Estrutura do Repositório

```
SIGEA/
├── backend/                    # API RESTful Spring Boot 3.3.6 (Java 21 LTS)
├── frontend/                   # SPA Angular 18 (Signals + TailwindCSS)
├── database/                   # DDL e DML canônicos para PostgreSQL 16
│   ├── 01_schema.sql           # 10 tabelas, enums e índices
│   ├── 02_seeds.sql            # 6 módulos, 53 gatilhos, 9 gravidades NCC MERP, 2 admins
│   └── init_database.sql       # Orquestrador (uso com psql ou container)
├── config/                     # Docker Compose local e IaC (Render / Vercel)
├── docs/                       # Documentação técnica e referências normativas
│   ├── arquitetura.md          # Arquitetura sistêmica e modelo de domínio
│   ├── desenvolvimento.md      # Onboarding e execução local
│   ├── homologacao.md          # Deploy em nuvem e roteiro de testes clínicos
│   └── referencias/            # PDFs oficiais (IHI-GTT e TCC/UFS)
├── projeto_departamental/      # Monografia LaTeX (abnTeX2 DCOMP-UFS)
└── .agents/                    # Regras, skills e agentes do Antigravity IDE
    ├── rules/                  # 5 regras always_on (stack, GTT, ergonomia, negócio, quality gate)
    ├── skills/                 # Runbooks especializados (gtt-audit, quality-gate, deploy, latex-dcomp)
    └── agents/                 # 5 agentes especializados (backend, frontend, clinical, academic, devops)
```

---

## 6. Agentes Especializados do Sistema

| Agente | Especialidade |
| :--- | :--- |
| `backend-engineer` | Java 21, Spring Boot 3.3, Spring Security 6 JWT, JPA, Flyway, JUnit 5, JaCoCo 100% |
| `frontend-engineer` | Angular 18+, Signals, TailwindCSS, ergonomia hospitalar desktop, Jest 100% |
| `clinical-gtt-auditor` | Metodologia IHI-GTT, 53 gatilhos, NCC MERP A–I, taxas epidemiológicas, CEP/UFS |
| `dcomp-academic` | LaTeX `dcomp-abntex2`, normas ABNT, monografia, relatórios de qualificação |
| `devops-homologation` | Podman, PostgreSQL Neon.tech, Render.com, Vercel, CI/CD GitHub Actions |