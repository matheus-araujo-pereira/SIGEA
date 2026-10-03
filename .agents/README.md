# SIGEA — Arquitetura de Agentes, Regras e Plugins Antigravity

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Departamento de Enfermagem  
**Autor Líder**: Matheus Araujo Pereira | Matrícula: 202100114080  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza · Prof. Dr. Gilton José Ferreira da Silva  
**Aprovação Ética**: CEP/UFS — CAAE nº 91836925.8.0000.5546  

---

## 1. Visão Geral dos Agentes Especializados

O workspace SIGEA conta com 5 agentes inteligentes especializados, configurados para atuar de forma sinérgica no ciclo completo de desenvolvimento, auditoria clínica e homologação em nuvem:

| Agente | Identificador | Especialidade e Foco de Atuação |
| :--- | :--- | :--- |
| **Backend Engineer** | `backend-engineer` | Java 21 LTS, Spring Boot 3.3.6, Spring Security 6 JWT, PostgreSQL 16 / NeonDB Serverless, Flyway e 100% JaCoCo. |
| **Frontend Engineer** | `frontend-engineer` | Angular 18+, Standalone Components, Signals, componentes nativos instalados (@angular/material e HTML5 semântico), TailwindCSS puro sem classes ad-hoc e 100% Jest. |
| **Clinical GTT Auditor** | `clinical-gtt-auditor` | Metodologia IHI Global Trigger Tool (6 módulos e 53 gatilhos oficiais: C1-C15, M1-M13, S1-S11, I1-I4, P1-P8, E1-E2), NCC MERP (A-I), 3 taxas epidemiológicas e bioética CEP/UFS. |
| **DCOMP Academic** | `dcomp-academic` | Integridade acadêmica e conformidade com a monografia entregue e aprovada na UFS (`projeto_departamental/` intocado). |
| **DevOps & Homologation** | `devops-homologation` | Docker/Podman, Neon.tech PostgreSQL Serverless com SSL, Web Service Render (Java 21), Vercel SPA e CI/CD. |

---

## 2. Estrutura de Customizações do Antigravity

```
.agents/
├── agents/                     # Definições descritivas em Markdown dos 5 agentes
│   ├── backend-engineer.md
│   ├── clinical-gtt-auditor.md
│   ├── dcomp-academic.md
│   ├── devops-homologation.md
│   └── frontend-engineer.md
├── plugins/                    # Plugins canônicos do Antigravity
│   └── sigea-platform/
│       ├── plugin.json         # Manifesto do plugin da plataforma SIGEA
│       └── rules/
│           └── AGENTS.md       # Regras consolidadas aplicadas automaticamente
├── rules/                      # Regras estruturadas do repositório
│   ├── 01_stack_arquitetura.md
│   ├── 02_regras_clinicas_gtt.md
│   ├── 03_ergonomia_desktop.md
│   ├── 04_regras_negocio_institucionais.md
│   └── 05_quality_gate_testes.md
├── skills/                     # Runbooks operacionais especializados
│   ├── deploy-homologacao/SKILL.md
│   ├── gtt-audit/SKILL.md
│   ├── latex-dcomp/SKILL.md
│   └── quality-gate/SKILL.md
├── plugins.json                # Registro oficial de plugins
├── rules.json                  # Catálogo de regras e agentes
└── skills.json                 # Registro de skills locais
```

---

## 3. Diretrizes Inegociáveis dos Agentes

1. **Intocabilidade do Projeto Departamental**: O diretório `projeto_departamental/` é somente-leitura. Todo o código do software deve espelhar fielmente o que foi homologado perante o DCOMP/UFS.
2. **Quality Gate 100%**: Backend com 100% JaCoCo (linhas e branches) e Frontend com 100% Jest. Zero testes pulados ou desabilitados.
3. **Ergonomia Desktop e Componentes Nativos**: Foco exclusivo em estações de trabalho clínicas de mesa (1280×720 a Ultrawide 3440×1440), paginação de 10 registros por página, sem CSS customizado inventado e com componentes nativos instalados.
4. **Deploy Triplo Homologado**: NeonDB (banco relacional com JSONB e SSL), Render (serviço backend) e Vercel (SPA Angular com proxy de API).
