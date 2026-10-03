# SIGEA — Sistema Inteligente de Gestão de Eventos Adversos

### Metodologia Global Trigger Tool (IHI-GTT) Aplicada ao Ensino de Segurança do Paciente

<div align="center">

[![Java](https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-18+-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.dev/)
[![TailwindCSS](https://img.shields.io/badge/TailwindCSS-3.4.x-38B2AC?style=for-the-badge&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16_LTS-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Quality Gate](https://img.shields.io/badge/Quality_Gate-100%25_Coverage-10B981?style=for-the-badge)](https://github.com/matheus-araujo-pereira/SIGEA/actions)

[![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)](https://github.com/matheus-araujo-pereira/SIGEA/actions)
[![Vercel](https://img.shields.io/badge/Frontend-Vercel-000000?style=flat-square&logo=vercel)](https://sigea.vercel.app)
[![Render](https://img.shields.io/badge/Backend-Render-46E3B7?style=flat-square&logo=render)](https://sigea-backend-vzq0.onrender.com/swagger-ui/index.html)
[![CEP/UFS](https://img.shields.io/badge/CEP%2FUFS-CAAE_91836925.8.0000.5546-1E3A8A?style=flat-square)](https://www.ufs.br/)

</div>

---

## 📋 Sumário

1. [Sobre o Projeto](#-sobre-o-projeto)
2. [Contexto Institucional](#-contexto-institucional--equipe)
3. [Funcionalidades do Sistema](#-funcionalidades-do-sistema)
4. [Metodologia IHI-GTT Implementada](#-metodologia-ihi-gtt-implementada)
5. [Stack Tecnológica](#-stack-tecnológica)
6. [Arquitetura do Sistema](#-arquitetura-do-sistema)
7. [Modelo de Banco de Dados](#-modelo-de-banco-de-dados)
8. [Executando o Projeto Localmente](#-executando-o-projeto-localmente)
9. [Ambiente de Produção](#-ambiente-de-produção-homologado)
10. [Quality Gate & Cobertura de Testes](#-quality-gate--cobertura-de-testes)
11. [CI/CD com GitHub Actions](#-cicd-com-github-actions)
12. [Estrutura do Repositório](#-estrutura-do-repositório)
13. [Documentação Técnica](#-documentação-técnica)
14. [Projeto Departamental (Monografia)](#-projeto-departamental-monografia)
15. [Segurança & Conformidade Ética](#-segurança--conformidade-ética)

---

## 🎯 Sobre o Projeto

O **SIGEA** (*Sistema Inteligente de Gestão de Eventos Adversos*) é uma plataforma educacional e de auditoria clínica que implementa digitalmente a metodologia canônica **Global Trigger Tool (GTT)** do *Institute for Healthcare Improvement* (IHI) para o ensino prático de segurança do paciente a acadêmicos de graduação em Enfermagem.

O sistema foi concebido para suprir uma lacuna identificada no ensino superior de enfermagem: a ausência de ferramentas digitais que integrem, em um único ambiente, a **auditoria ativa de gatilhos clínicos**, a **classificação padronizada de gravidade de danos** (índice NCC MERP, categorias A–I) e as **ferramentas consagradas de análise de causa raiz** utilizadas na gestão da qualidade hospitalar.

### O que torna o SIGEA único

| Característica | Detalhes |
| :--- | :--- |
| **100% Fiel ao IHI** | Os 53 gatilhos clínicos (C1–C15, M1–M13, S1–S11, I1–I4, P1–P8, E1–E2) implementados verbatim conforme o PDF oficial IHI-GTT 2ª Edição (2019). |
| **Auditoria em Duas Etapas** | Revisão Primária Individual (estudante) + Revisão de Consenso (professor/supervisor), replicando o fluxo clínico real da GTT. |
| **Prontuários Simulados JSONB** | Prontuários ficcionais hiper-realistas armazenados em JSONB no PostgreSQL 16, incluindo prescrições, evoluções, exames e procedimentos. |
| **Ferramentas da Qualidade Integradas** | Ishikawa 6M, Método 5W2H, Matriz GUT, Ciclo PDCA e Análise SWOT embutidos na tela de resolução de auditoria. |
| **Dashboards Epidemiológicos** | Cálculo automático das 3 taxas IHI: EAs por 1.000 pacientes-dia, EAs por 100 admissões e % de pacientes com dano. |
| **Avaliação SUS Integrada** | Escala de Usabilidade do Sistema (Brooke, 1996) com 10 questões Likert e cálculo automático do escore SUS (0–100). |
| **Quality Gate 100%** | 342+ testes backend (JaCoCo 100%) + 568+ testes frontend em 81 suítes (Jest 100%). Zero falhas. |

---

## 🏛️ Contexto Institucional & Equipe

| Campo | Informação |
| :--- | :--- |
| **Instituição** | Universidade Federal de Sergipe (UFS) — São Cristóvão/SE |
| **Unidades Envolvidas** | Departamento de Computação (DCOMP/CCET) e Departamento de Enfermagem (CCBS) |
| **Autor Líder / Desenvolvedor** | Matheus Araujo Pereira — Matrícula: `202100114080` |
| **Orientador (Computação)** | Prof. Dr. Gilton José Ferreira da Silva |
| **Coorientadora (Enfermagem)** | Profª. Drª. Ana Waleska de Menezes Seixas Souza |
| **Aprovação Ética (CEP/UFS)** | CAAE nº **91836925.8.0000.5546** |
| **Aprovação GEP/EBSERH** | Gerência de Ensino e Pesquisa — HU/UFS |
| **Natureza dos Dados** | 100% simulados e fictícios — zero contato com dados reais de pacientes |

---

## ✨ Funcionalidades do Sistema

O SIGEA está completamente implementado nas suas três fases de desenvolvimento:

### Fase 1 — Autenticação & Gestão de Usuários ✅

- **Login JWT Stateless**: Autenticação segura com tokens HMAC-SHA256, expiração configurável e filtro de autorização via `JwtAuthenticationFilter`.
- **RBAC com 3 Perfis**: `ADMIN`, `PROFESSOR` e `STUDENT` com permissões granulares por endpoint.
- **Primeiro Acesso Obrigatório**: Flag `must_change_password = true` no cadastro bloqueia a navegação e redireciona para troca de senha.
- **Domínio Institucional**: Validação `@academico.ufs.br` em todas as camadas (frontend + `@UfsEmail` annotation no backend).
- **Tela de Perfil**: Edição de dados pessoais, alteração voluntária de senha com validação de senha atual.
- **Gestão de Usuários (ADMIN)**: CRUD completo com inativação, reativação e redefinição de senhas.

### Fase 2 — Catálogos Clínicos IHI-GTT ✅

- **6 Módulos GTT**: Cuidados Clínicos (C), Medicação (M), Cirúrgico (S), Intensivo (I), Perinatal (P) e Emergência (E).
- **53 Gatilhos Clínicos**: Implementação 100% fiel ao PDF oficial IHI-GTT, com código, nome padronizado e descrição clínica.
- **9 Gravidades NCC MERP**: Categorias A (circunstância de risco) a I (óbito causado pelo evento), com rótulo, descrição e código de cor.
- **Catálogos Auditáveis (ADMIN)**: CRUD completo de módulos, gatilhos e gravidades com suporte a ativação/inativação.
- **Guias de Referência**: Visualização paginada dos catálogos com filtro por módulo e busca textual — acessível a todos os perfis.

### Fase 3 — Fluxo Acadêmico & Dashboards ✅

#### Gestão de Turmas (PROFESSOR)
- Criação de turmas com código único, período letivo (semestre/ano) e descrição.
- Matrícula e desmatrícula de estudantes por e-mail institucional.
- Encerramento de turma com bloqueio de novas submissões.

#### Atividades com Prontuários Simulados (PROFESSOR)
- Criação de atividades com prontuário simulado `JSONB` customizável (anamnese, evoluções, prescrições, exames laboratoriais, procedimentos).
- Configuração de prazo de entrega e título da atividade.
- Atribuição automática às turmas vinculadas.

#### Resolução de Auditoria GTT (STUDENT)
- Interface para revisão primária do prontuário com identificação de gatilhos por módulo.
- Registro de gatilhos identificados com código, nome e justificativa clínica.
- Preenchimento das ferramentas da qualidade: **Diagrama de Ishikawa 6M**, **5W2H**, **Matriz GUT** e **Ciclo PDCA**.
- Submissão final com confirmação e bloqueio de reedição pós-envio.

#### Correção com Nota (PROFESSOR)
- Revisão de Consenso: validação/invalidação dos gatilhos identificados pelo estudante.
- Atribuição de nota (0,0 a 10,0) com feedback textual.
- Histórico completo de todas as submissões por atividade.

#### Dashboards Epidemiológicos (PROFESSOR/ADMIN)
- **Taxa de EAs por 1.000 Pacientes-Dia**: `(Nº de EAs / Total de Pacientes-Dia) × 1.000`
- **Taxa de EAs por 100 Admissões**: `(Nº de EAs / Total de Admissões) × 100`
- **% de Pacientes com Dano**: `(Nº de Prontuários com ≥1 EA / Total de Prontuários Auditados) × 100`
- Distribuição de gatilhos por módulo e frequência de ocorrência.
- Desempenho pedagógico: média de nota por turma, distribuição de notas e taxa de conclusão.

#### Avaliação SUS (STUDENT/PROFESSOR)
- Formulário com as 10 questões validadas da *System Usability Scale* (Brooke, 1996).
- Cálculo automático do escore SUS (0–100) com classificação de usabilidade.
- Dashboard agregado por turma e visão geral do sistema.

---

## 🏥 Metodologia IHI-GTT Implementada

A metodologia GTT (*Global Trigger Tool*) foi desenvolvida pelo *Institute for Healthcare Improvement* (IHI) para detectar eventos adversos (EAs) em prontuários hospitalares através de "gatilhos" — sinais de alerta clínico que indicam possível ocorrência de dano.

### Os 6 Módulos e 53 Gatilhos

| Módulo | Código | Qtd. Gatilhos | Exemplos Representativos |
| :--- | :---: | :---: | :--- |
| **Cuidados Clínicos** | C | 15 | C1 — Transferência para UTI; C6 — Queda; C11 — Lesão de Decúbito |
| **Medicação** | M | 13 | M1 — Vitamina K; M4 — Hipoglicemia; M8 — Sedação excessiva |
| **Cirúrgico** | S | 11 | S1 — Reoperação; S3 — Mudança no procedimento; S10 — Complicação pós-operatória |
| **Intensivo** | I | 4 | I1 — Pneumonia associada à VM; I2 — TVP/TEP; I4 — Readmissão em UTI |
| **Perinatal** | P | 8 | P1 — Apgar < 7 no 5º minuto; P5 — Parto operatório; P8 — Lesão ao nascimento |
| **Emergência** | E | 2 | E1 — Readmissão em 48h; E2 — Admissão hospitalar subsequente |

### Taxonomia de Gravidade NCC MERP (A–I)

| Categoria | Descrição | Nível de Dano |
| :---: | :--- | :--- |
| **A** | Circunstância de risco — sem dano ao paciente | Sem dano |
| **B** | Erro ocorreu, não atingiu o paciente | Sem dano |
| **C** | Erro atingiu o paciente, sem necessidade de monitoramento | Sem dano |
| **D** | Erro atingiu o paciente, exigiu monitoramento adicional | Sem dano |
| **E** | Dano temporário, necessitou intervenção | **Dano leve** |
| **F** | Dano temporário, necessitou hospitalização ou prolongou internação | **Dano moderado** |
| **G** | Dano permanente | **Dano grave** |
| **H** | Dano que necessitou intervenção para sustentar a vida | **Dano grave/crítico** |
| **I** | Óbito do paciente causado ou contribuído pelo evento adverso | **Óbito** |

---

## 🛠️ Stack Tecnológica

### Backend

| Componente | Tecnologia | Versão | Função |
| :--- | :--- | :--- | :--- |
| **Runtime** | Java (OpenJDK Temurin) | 21 LTS | Linguagem principal |
| **Framework** | Spring Boot | 3.3.6 | Plataforma da aplicação |
| **Segurança** | Spring Security 6 | 6.3.x | JWT stateless + RBAC |
| **Persistência** | Spring Data JPA + Hibernate | 6.x | ORM e acesso a dados |
| **Banco de Dados** | PostgreSQL (driver) | 16 LTS | SGBD relacional |
| **Migrações** | Flyway Community | Latest | Versionamento de schema |
| **Validação** | Bean Validation (Jakarta) | 3.x | Validação de entrada |
| **Mapeamento** | MapStruct | 1.5.x | DTO ↔ Entity |
| **Boilerplate** | Lombok | 1.18.x | Redução de verbosidade |
| **Documentação API** | SpringDoc OpenAPI 3 | 2.x | Swagger UI automático |
| **Serialização JSONB** | Jackson | 2.17.x | JSON ↔ Objetos Java |
| **Build** | Maven (Wrapper) | 3.9.x | Gerenciamento do projeto |
| **Testes** | JUnit 5, Mockito, MockMvc, AssertJ | Latest | Testes unitários e web |
| **Cobertura** | JaCoCo | 0.8.x | 100% branches e linhas |

### Frontend

| Componente | Tecnologia | Versão | Função |
| :--- | :--- | :--- | :--- |
| **Framework** | Angular | 18.2.x | SPA principal |
| **Arquitetura** | Standalone Components | Angular 18+ | Módulos eliminados |
| **Reatividade** | Angular Signals | Angular 17+ | Estado reativo moderno |
| **Estilização** | TailwindCSS | 3.4.x | Design system utilitário |
| **Componentes UI** | Angular Material | 18.x | Primitivas de acessibilidade |
| **Formulários** | Reactive Forms | Angular 18+ | Formulários validados |
| **HTTP** | HttpClient com Interceptors | Angular 18+ | Requisições REST + JWT |
| **Roteamento** | Angular Router + Guards | Angular 18+ | Proteção de rotas |
| **Testes** | Jest + jest-preset-angular | 29.x | Testes unitários e de componente |
| **Documentação** | Compodoc | 1.1.x | Documentação de componentes |
| **Build** | Angular CLI | 18.x | Bundling e otimização |

### Infraestrutura

| Componente | Tecnologia | Versão | Função |
| :--- | :--- | :--- | :--- |
| **Container Local** | Podman / Docker | 4.x / 24.x | PostgreSQL 16 local |
| **Orquestração** | Compose (Podman/Docker) | V2 | Multi-container local |
| **Backend Cloud** | Render Web Service (Free) | — | API em produção |
| **Frontend Cloud** | Vercel (Free) | — | SPA em produção |
| **Banco Cloud** | Neon.tech (Free) | PostgreSQL 16 | BD serverless em produção |
| **CI/CD** | GitHub Actions | — | Quality Gate automático |
| **VCS** | Git + GitHub | — | Controle de versão |

---

## 🏗️ Arquitetura do Sistema

### Visão Geral (C4 — Contexto)

```
┌─────────────────────────────────────────────────────────────────┐
│                        SIGEA — Produção                         │
│                                                                 │
│  ┌─────────┐     HTTPS      ┌──────────────┐                   │
│  │ Browser │ ─────────────► │  Vercel SPA  │  (Angular 18)     │
│  │ Desktop │                │ sigea.vercel │                   │
│  │  User   │                │    .app      │                   │
│  └─────────┘                └──────┬───────┘                   │
│                                    │ /api/* Reverse Proxy       │
│                                    ▼                            │
│                       ┌────────────────────┐                   │
│                       │  Render Web Service │  (Spring Boot)    │
│                       │  sigea-backend-    │                   │
│                       │  vzq0.onrender.com │                   │
│                       └────────┬───────────┘                   │
│                                │ JDBC (SSL)                     │
│                                ▼                                │
│                       ┌────────────────────┐                   │
│                       │   Neon.tech Cloud  │  (PostgreSQL 16)  │
│                       │  sa-east-1 (BR)    │                   │
│                       └────────────────────┘                   │
└─────────────────────────────────────────────────────────────────┘
```

### Camadas da API (Backend)

```
HTTP Request
    │
    ├─► JwtAuthenticationFilter      ← Valida Bearer Token JWT
    │
    ├─► SecurityConfig (RBAC)        ← Autorização por perfil e rota
    │
    ├─► Controller (@RestController) ← Recebe requisição, valida DTO
    │       │
    │       ├─► @Valid DTOs          ← Bean Validation (@NotBlank, @UfsEmail, etc.)
    │       │
    │       └─► Service (@Service)   ← Regras de negócio e orquestração
    │               │
    │               ├─► Repository (@Repository/JPA) ← Queries PostgreSQL
    │               │
    │               └─► Mapper (MapStruct) ← Entity ↔ DTO
    │
    └─► GlobalExceptionHandler       ← RFC 7807 ProblemDetail
```

### Fluxo de Autenticação JWT

```
POST /api/auth/login
    │
    ├─► Valida credenciais (e-mail + senha BCrypt)
    ├─► Verifica must_change_password
    ├─► Gera JWT (HMAC-SHA256, 24h de expiração)
    └─► Retorna { token, mustChangePassword, role, name }

Requisições subsequentes:
    Authorization: Bearer <token>
    │
    └─► JwtAuthenticationFilter extrai e valida o token
        └─► Injeta UserDetails no SecurityContext
```

### Arquitetura Frontend (Feature-First)

```
frontend/src/app/
├── core/                    # Singleton — guards, interceptors, models, services
│   ├── guards/              # AuthGuard, FirstLoginGuard, RoleGuard
│   ├── interceptors/        # AuthInterceptor (inject JWT), ErrorInterceptor, LoadingInterceptor
│   ├── models/              # TypeScript interfaces (User, Activity, GttModule, etc.)
│   └── services/            # HTTP Services (AuthService, UserService, ActivityService, etc.)
│
├── features/                # Módulos de funcionalidade (lazy loading)
│   ├── auth/                # login/, first-login/
│   ├── users/               # user-list/, user-form/
│   ├── gtt/                 # modules/, triggers/, severities/
│   ├── academic/            # classes/, activities/, dashboard/
│   ├── sus/                 # sus-form/, sus-dashboard/
│   └── profile/             # profile/
│
└── shared/                  # Componentes reutilizáveis
    └── components/          # AppShell, DataTable, ConfirmDialog, LoadingSpinner, ToastContainer
```

---

## 🗄️ Modelo de Banco de Dados

O esquema relacional é definido em [`database/01_schema.sql`](database/01_schema.sql) e gerenciado via Flyway no backend.

### Tabelas Principais

| Tabela | Chave Primária | Descrição |
| :--- | :--- | :--- |
| `users` | UUID | Usuários do sistema (ADMIN, PROFESSOR, STUDENT) |
| `gtt_modules` | UUID | 6 módulos GTT (C, M, S, I, P, E) |
| `gtt_triggers` | UUID | 53 gatilhos clínicos padronizados |
| `harm_severities` | UUID | 9 categorias de gravidade NCC MERP (A–I) |
| `academic_classes` | UUID | Turmas acadêmicas com período letivo |
| `class_students` | (class_id, student_id) | Relação N:M turma ↔ estudante |
| `activities` | UUID | Atividades com prontuário simulado JSONB |
| `activity_submissions` | UUID | Submissões dos estudantes com ferramentas da qualidade |
| `sus_evaluations` | UUID | Avaliações SUS preenchidas pelos usuários |
| `clinical_case_templates` | UUID | Templates reutilizáveis de prontuários |

### Campos JSONB dos Prontuários Simulados

O campo `clinical_case_data` (JSONB) na tabela `activities` armazena:

```json
{
  "patientAge": 65,
  "patientGender": "MALE",
  "admissionDate": "2026-03-01",
  "mainDiagnosis": "Insuficiência Cardíaca Congestiva",
  "evolutionNotes": [...],
  "prescriptions": [...],
  "labExams": [...],
  "procedures": [...]
}
```

### Seeds Canônicos ([`database/02_seeds.sql`](database/02_seeds.sql))

- `2` usuários ADMIN iniciais (senha provisória, primeiro acesso pendente)
- `9` gravidades NCC MERP (A a I) com rótulos e cores padronizados
- `6` módulos GTT com código oficial (C, M, S, I, P, E)
- `53` gatilhos clínicos com código e descrição 100% fiéis ao PDF IHI-GTT

---

## 🚀 Executando o Projeto Localmente

### Pré-requisitos

| Ferramenta | Versão Mínima | Instalação Recomendada |
| :--- | :--- | :--- |
| Java (JDK) | 21 LTS | [SDKMAN](https://sdkman.io/) → `sdk env` (`.sdkmanrc` na raiz) |
| Node.js | 20.x | [Node.js Official](https://nodejs.org/) |
| npm | 10.x | Incluído com Node.js 20+ |
| Podman ou Docker | 4.x / 24.x | Com suporte a Compose V2 |

> **Dica**: Com SDKMAN instalado, basta executar `sdk env` na raiz do projeto para configurar automaticamente o Java 21.

---

### 1. Clone o Repositório

```bash
git clone https://github.com/matheus-araujo-pereira/SIGEA.git
cd SIGEA
```

---

### 2. Configure as Variáveis de Ambiente

Copie o arquivo de exemplo e ajuste as variáveis:

```bash
cp config/docker/.env.example config/docker/.env
# Edite config/docker/.env se necessário (as defaults funcionam para desenvolvimento local)
```

Variáveis principais:

```env
POSTGRES_USER=sigea_admin
POSTGRES_PASSWORD=sigea_dev_password
POSTGRES_DB=sigea
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/sigea
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_HOURS=24
CORS_ALLOWED_ORIGINS=http://localhost:4200
```

---

### 3. Inicie o Banco de Dados (PostgreSQL 16)

```bash
# Com Podman (recomendado no Fedora)
podman compose -f config/docker/docker-compose.yml up -d

# Com Docker
docker compose -f config/docker/docker-compose.yml up -d
```

O PostgreSQL será inicializado na porta `5432` com o banco **vazio**. O Flyway do backend (`db/migration/V1..V3`) cria o schema e os seeds automaticamente no primeiro start — o diretório `database/` **não** é montado no container para evitar dupla inicialização.

> **Atalho**: `./scripts/dev.sh up` executa os passos 3, 4 e 5 de uma vez. Veja [`docs/ambiente-local.md`](docs/ambiente-local.md).

Para verificar se o container está rodando:

```bash
podman ps   # ou docker ps
```

---

### 4. Inicie o Backend (Spring Boot 3.3.6)

```bash
cd backend
./mvnw spring-boot:run
```

Aguarde a mensagem `Started SigeaApplication`. O backend estará disponível em:

| Recurso | URL |
| :--- | :--- |
| **API Base** | `http://localhost:8080/api` |
| **Health Check** | `http://localhost:8080/api/public/ping` |
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |

---

### 5. Inicie o Frontend (Angular 18)

Em outro terminal:

```bash
cd frontend
npm install       # Apenas na primeira execução ou após atualizar dependências
npm start         # ng serve com proxy para http://localhost:8080
```

Acesse a aplicação em: **[http://localhost:4200](http://localhost:4200)**

O proxy está configurado em `frontend/proxy.conf.json` para redirecionar `/api/*` → `http://localhost:8080/api/*`, eliminando problemas de CORS em desenvolvimento.

---

### Credenciais Iniciais (Desenvolvimento Local)

| Perfil | E-mail | Senha Provisória | Obs. |
| :--- | :--- | :--- | :--- |
| `ADMIN` | `matheusaraujopereira@academico.ufs.br` | `SigeaUFS@2026` | Troca obrigatória no 1º login |
| `ADMIN` | `anawaleska@academico.ufs.br` | `SigeaUFS@2026` | Troca obrigatória no 1º login |

> Após a troca de senha, defina uma senha que atenda aos requisitos: mínimo 8 caracteres, letras maiúsculas, minúsculas, números e caracteres especiais.

---

## 🌐 Ambiente de Produção Homologado

O SIGEA está publicado e operacional nos seguintes endereços:

| Recurso | URL |
| :--- | :--- |
| **Frontend (SPA)** | [https://sigea.vercel.app](https://sigea.vercel.app) |
| **Backend API** | [https://sigea-backend-vzq0.onrender.com](https://sigea-backend-vzq0.onrender.com) |
| **Swagger UI (Produção)** | [https://sigea-backend-vzq0.onrender.com/swagger-ui/index.html](https://sigea-backend-vzq0.onrender.com/swagger-ui/index.html) |

### Arquitetura de Produção e Estratégia Anti-CORS

O `vercel.json` configura um **reverse proxy nativo** no Vercel que encaminha todas as requisições `/api/*` diretamente para o backend no Render, eliminando completamente os bloqueios de CORS sem necessidade de configurações adicionais no Spring Boot:

```json
{
  "rewrites": [
    { "source": "/api/:path*", "destination": "https://sigea-backend-vzq0.onrender.com/api/:path*" },
    { "source": "/(.*)",       "destination": "/index.html" }
  ]
}
```

### Infraestrutura Cloud (100% Gratuita)

| Serviço | Plano | Região | Recurso |
| :--- | :--- | :--- | :--- |
| **Vercel** | Hobby (Free) | Global CDN | Frontend Angular SPA |
| **Render** | Free Web Service | Ohio (us-east-2) | Backend Spring Boot (Docker) |
| **Neon.tech** | Free (0.5 GB) | sa-east-1 (Brasil) | PostgreSQL 16 Serverless |

> **Nota**: O Render no plano Free hiberna a instância após 15 minutos de inatividade. O primeiro acesso pode demorar ~30 segundos para "cold start".

---

## ✅ Quality Gate & Cobertura de Testes

O SIGEA mantém **100% de cobertura de testes** como requisito inegociável em ambas as camadas.

### Backend — JUnit 5 + JaCoCo

```bash
cd backend && ./mvnw clean verify
```

| Métrica | Meta | Status |
| :--- | :--- | :--- |
| **Linhas cobertas** | 100% | ✅ |
| **Branches cobertas** | 100% | ✅ |
| **Arquivos de teste** | 33 arquivos `.java` | ✅ |
| **Total de testes** | 342+ | ✅ |
| **Falhas** | 0 | ✅ |

Relatório gerado em: `backend/target/site/jacoco/index.html`

Estrutura dos testes do backend:

```
backend/src/test/
├── auth/          # AuthControllerTest, AuthServiceTest, JwtTokenServiceTest
├── user/          # UserControllerTest, UserServiceTest
├── gtt/           # GttModuleControllerTest, GttTriggerControllerTest, HarmSeverityControllerTest
├── academic/      # AcademicClassControllerTest, ActivityControllerTest, ActivitySubmissionControllerTest
│                  # ClassDashboardControllerTest, SusEvaluationControllerTest, ReportExportControllerTest
└── common/        # GlobalExceptionHandlerTest, HealthCheckControllerTest
```

### Frontend — Jest + jest-preset-angular

```bash
cd frontend && npm test
# Com relatório de cobertura:
cd frontend && npm test -- --coverage
```

| Métrica | Meta | Status |
| :--- | :--- | :--- |
| **Statements** | 100% | ✅ |
| **Branches** | 100% | ✅ |
| **Functions** | 100% | ✅ |
| **Lines** | 100% | ✅ |
| **Suítes de teste** | 81 arquivos `.spec.ts` | ✅ |
| **Total de testes** | 568 | ✅ |
| **Falhas** | 0 | ✅ |

Relatório gerado em: `frontend/coverage/`

### Documentação Técnica Automatizada

```bash
# Backend — Javadoc (100% das classes públicas)
cd backend && ./mvnw javadoc:javadoc

# Frontend — Compodoc
cd frontend && npm run docs:build
# Relatório em: frontend/documentation/index.html
```

---

## 🔄 CI/CD com GitHub Actions

O workflow em [`.github/workflows/quality-gate.yml`](.github/workflows/quality-gate.yml) executa automaticamente em cada `push` ou `pull_request` para `main`:

```yaml
Jobs em paralelo:
├── backend-quality-gate (ubuntu-latest, Java 21 Temurin, timeout: 15min)
│   ├── Checkout do código
│   ├── Setup Java 21 + cache Maven
│   ├── ./mvnw clean verify -B          # JUnit 5 + JaCoCo 100%
│   └── Upload do relatório JaCoCo como artefato (7 dias)
│
└── frontend-quality-gate (ubuntu-latest, Node 20, timeout: 15min)
    ├── Checkout do código
    ├── Setup Node.js 20 + cache npm
    ├── npm ci                           # Instalação determinística
    ├── npm run build                    # Build de produção Angular
    ├── npm test -- --coverage --ci      # Jest 100%
    ├── npm run docs:build               # Compodoc sem erros
    └── Upload do relatório Jest como artefato (7 dias)
```

---

## 📁 Estrutura do Repositório

```
SIGEA/
│
├── .agents/                          # Customizações e Agentes Especializados Antigravity IDE
│   ├── rules/                        # Regras inegociáveis always-on (stack, GTT, ergonomia, negócio)
│   ├── skills/                       # Runbooks operacionais (gtt-audit, quality-gate, deploy, latex)
│   ├── agents/                       # Agentes de domínio (backend, frontend, clinical, devops, academic)
│   └── plugins/                      # Plugin sigea-platform homologado
│
├── .github/
│   └── workflows/
│       └── quality-gate.yml          # CI/CD: JaCoCo 100% + Jest 100%
│
├── backend/                          # API RESTful Spring Boot 3.3.6 (Java 21 LTS)
│   ├── pom.xml                       # Gerenciamento de dependências Maven
│   ├── mvnw / mvnw.cmd               # Maven Wrapper (não requer instalação global)
│   ├── Dockerfile                    # Multi-stage build (usado pelo Render)
│   └── src/
│       ├── main/
│       │   ├── java/br/ufs/sigea/
│       │   │   ├── SigeaApplication.java
│       │   │   ├── auth/             # JWT, SecurityConfig, AuthController/Service
│       │   │   ├── user/             # UserController/Service/Repository/Entity
│       │   │   ├── gtt/              # module/, trigger/, severity/ (catálogos GTT)
│       │   │   ├── academic/         # clazz/, activity/, clinical/, dashboard/, sus/, report/
│       │   │   └── common/           # exceptions, validations, configs, utils
│       │   └── resources/
│       │       ├── application.yml   # Configuração do Spring Boot (HikariCP Neon + Actuator)
│       │       └── db/migration/     # Flyway V1 a V4 (schema, seeds, índices GIN e triggers)
│       └── test/java/br/ufs/sigea/   # 33 arquivos de teste, 342 casos (JaCoCo 100%)
│
├── frontend/                         # SPA Angular 18 (Signals + TailwindCSS)
│   ├── angular.json                  # Configuração do projeto Angular
│   ├── tailwind.config.js            # Design system clínico hospitalar
│   ├── jest.config.js                # Configuração do Jest
│   ├── proxy.conf.json               # Proxy /api/* → localhost:8080
│   ├── vercel.json                   # Reverse proxy + SPA fallback
│   └── src/
│       └── app/
│           ├── core/                 # Guards, Interceptors, Models, Services
│           ├── features/             # auth/, users/, gtt/, academic/, sus/, profile/
│           └── shared/               # AppShell, DataTable, ConfirmDialog, Toast
│
├── database/                         # Scripts PostgreSQL 16 (canônicos e DDL/DML)
│   ├── 01_schema.sql                 # DDL: 10 tabelas, índices GIN para JSONB, triggers updated_at
│   ├── 02_seeds.sql                  # DML: 6 módulos, 53 gatilhos, 9 gravidades, 2 admins
│   ├── 03_clinical_templates.sql     # Prontuários simulados curados (100% fictícios CEP/UFS)
│   ├── init_database.sql             # Orquestrador sequencial unificado
│   └── README.md                     # Documentação técnica do banco de dados
│
├── config/
│   ├── docker/
│   │   ├── docker-compose.yml        # PostgreSQL 16 local (Podman ou Docker)
│   │   └── .env.example              # Modelo de variáveis de ambiente
│   └── deploy/
│       └── render.yaml               # IaC declarativo para Render Web Service (com health check)
│
├── scripts/
│   └── dev.sh                        # Sobe/derruba DB + backend + frontend (up|down|status|logs|test)
│
├── docs/                             # Documentação técnica do projeto
│   ├── arquitetura.md                # Arquitetura sistêmica, modelo de dados, CORS
│   ├── desenvolvimento.md            # Guia de onboarding e desenvolvimento local
│   ├── ambiente-local.md             # Setup da máquina, Beekeeper, Bruno, troubleshooting
│   ├── bruno/                        # Coleção Bruno API Client (Local + Homologação)
│   ├── homologacao.md                # Deploy Render + Vercel + Neon.tech
│   └── referencias/
│       ├── metodologia_global_trigger_tool.pdf   # IHI GTT 2ª Edição (2019) — PDF OFICIAL
│       └── tema_trabalho_conclusao_de_curso.pdf  # Apresentação do tema (Profª Ana Waleska)
│
├── projeto_departamental/            # Monografia LaTeX (abnTeX2 DCOMP/UFS)
│   ├── projeto_departamental.tex     # Documento mestre
│   ├── projeto_departamental.pdf     # PDF compilado (homologado, ~32 páginas)
│   ├── Makefile                      # make / make clean / make distclean
│   ├── dcomp-abntex2.cls             # Classe LaTeX oficial DCOMP/UFS
│   ├── Conteudo/                     # 01_Introducao, 02_Objetivos, 03_Metodologia, 04_PlanoTrabalho
│   ├── Pre_Textual/                  # Resumo.tex, Abreviaturas.tex
│   ├── Imagens/                      # Brasões UFS e DCOMP (EPS + PDF)
│   └── Bibliografia.bib              # Referências BibTeX (ABNT)
│
├── Dockerfile                        # Multi-stage build raiz (compatibilidade Render)
├── .dockerignore                     # Exclusões Docker/Podman
├── .editorconfig                     # Padronização de editor
├── .gitignore                        # Exclusões Git
├── .sdkmanrc                         # java=21.x.x-tem (SDKMAN)
├── GEMINI.md                         # Norma executiva do workspace Antigravity IDE
└── README.md                         # Este arquivo
```

---

## 📚 Documentação Técnica

| Documento | Localização | Conteúdo |
| :--- | :--- | :--- |
| **Arquitetura do Sistema** | [`docs/arquitetura.md`](docs/arquitetura.md) | Topologia, fluxo JWT, modelo de dados, estratégia CORS |
| **Guia de Desenvolvimento** | [`docs/desenvolvimento.md`](docs/desenvolvimento.md) | Onboarding, setup local, convenções de código |
| **Ambiente Local (Fedora)** | [`docs/ambiente-local.md`](docs/ambiente-local.md) | Toolchain, `scripts/dev.sh`, conexão Beekeeper, coleção Bruno, troubleshooting |
| **Coleção Bruno** | [`docs/bruno/`](docs/bruno/) | Todos os endpoints da API (ambientes Local e Homologação) |
| **Guia de Homologação** | [`docs/homologacao.md`](docs/homologacao.md) | Deploy Render/Vercel/Neon, roteiro de testes clínicos |
| **Banco de Dados** | [`database/README.md`](database/README.md) | Esquema detalhado, tabelas, enums, seeds |
| **Swagger UI (Local)** | `http://localhost:8080/swagger-ui/index.html` | Todos os endpoints com exemplos interativos |
| **Swagger UI (Produção)** | [sigea-backend-vzq0.onrender.com/swagger-ui](https://sigea-backend-vzq0.onrender.com/swagger-ui/index.html) | API em produção |
| **Compodoc (Frontend)** | [`frontend/documentation/index.html`](frontend/documentation/index.html) | Componentes, serviços e rotas Angular |
| **PDF IHI-GTT** | [`docs/referencias/metodologia_global_trigger_tool.pdf`](docs/referencias/metodologia_global_trigger_tool.pdf) | Manual oficial IHI 2ª Edição (2019) |

---

## 📄 Projeto Departamental (Monografia)

O documento oficial de Projeto Departamental para o DCOMP/UFS, elaborado em **LaTeX + abnTeX2**, está disponível em [`projeto_departamental/`](projeto_departamental/):

- **PDF Compilado**: [`projeto_departamental.pdf`](projeto_departamental/projeto_departamental.pdf)
- **Estrutura**: Introdução → Objetivos → Metodologia → Plano de Trabalho (4 capítulos)

### Compilação da Monografia

```bash
# Pré-requisito: TeX Live com pdflatex e bibtex instalados
# Fedora: sudo dnf install texlive-scheme-full

cd projeto_departamental

make              # Compilação completa (pdflatex + bibtex + pdflatex × 2)
make clean        # Remove arquivos intermediários (preserva o .pdf)
make distclean    # Remove tudo, incluindo o .pdf
```

---

## 🔒 Segurança & Conformidade Ética

### Segurança da Aplicação

| Controle | Implementação |
| :--- | :--- |
| **Autenticação** | JWT HMAC-SHA256 stateless, sem sessões no servidor |
| **Senhas** | BCrypt com custo 12 (≈300ms por hash) |
| **Autorização** | RBAC por endpoint via `@PreAuthorize` e `SecurityConfig` |
| **Domínio E-mail** | `@UfsEmail` annotation + regex `^.*@academico\.ufs\.br$` em todas as camadas |
| **Validação de Entrada** | Bean Validation completo + tratamento RFC 7807 ProblemDetail |
| **CORS** | Configurado por origem (`CORS_ALLOWED_ORIGINS` via env var) |
| **Dados em Trânsito** | HTTPS obrigatório em produção (Vercel + Render) |

### Conformidade Ética (CEP/UFS)

Este projeto foi aprovado pelo **Comitê de Ética em Pesquisa da Universidade Federal de Sergipe** sob o Parecer CAAE nº **91836925.8.0000.5546** e pela **Gerência de Ensino e Pesquisa (GEP/EBSERH)** do Hospital Universitário da UFS.

**Princípio fundamental de sigilo absoluto**:
> Todos os prontuários, pacientes, diagnósticos, prescrições e demais dados clínicos armazenados e processados pelo SIGEA são **estritamente simulados e fictícios**. É terminantemente proibido inserir, importar ou processar dados reais identificáveis de pacientes, profissionais de saúde ou instituições hospitalares.

---

## ⚖️ Licença e Direitos Autorais

Desenvolvido no âmbito das atividades acadêmicas e de pesquisa da **Universidade Federal de Sergipe (UFS)** — DCOMP / Departamento de Enfermagem.

**© 2026 Matheus Araujo Pereira — Todos os direitos reservados.**

Uso autorizado exclusivamente para fins acadêmicos e de pesquisa vinculados à UFS, mediante citação dos autores e orientadores.
