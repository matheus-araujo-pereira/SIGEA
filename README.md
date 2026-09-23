# SIGEA: Sistema Inteligente de Gestão de Eventos Adversos
### Metodologia Global Trigger Tool (IHI-GTT) Aplicada ao Ensino de Segurança do Paciente

[![Java](https://img.shields.io/badge/Java-21_LTS-orange.svg?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.x-brightgreen.svg?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-18+-red.svg?style=flat-square&logo=angular)](https://angular.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16_LTS-blue.svg?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Quality Gate](https://img.shields.io/badge/Quality_Gate-100%25_Coverage-emerald.svg?style=flat-square)](https://github.com/matheus-araujo-pereira/SIGEA)
[![Instituição](https://img.shields.io/badge/UFS-DCOMP%20%2F%20Enfermagem-1E3A8A.svg?style=flat-square)](https://www.ufs.br/)

O **SIGEA** é uma plataforma educacional e de auditoria clínica baseada na metodologia canônica **Global Trigger Tool (GTT)** do *Institute for Healthcare Improvement* (IHI). O sistema foi concebido para o ensino prático de segurança do paciente e gestão de riscos hospitalares para acadêmicos da graduação em enfermagem, integrando prontuários simulados hiper-realistas, detecção ativa de gatilhos clínicos, classificação de gravidade de dano (índice NCC MERP) e ferramentas consagradas de melhoria contínua da qualidade (Ishikawa 6M, Ciclo PDCA, Matriz GUT, Matriz SWOT, Método 5W2H/5W3H e Brainstorming).

---

## 🏛️ Contexto Institucional & Equipe

- **Instituição**: Universidade Federal de Sergipe (UFS) — São Cristóvão/SE
- **Unidades**: Departamento de Computação (DCOMP / CCET) e Departamento de Enfermagem (CCBS)
- **Autor Líder / Desenvolvedor**: Matheus Araujo Pereira (`matheusaraujopereira@academico.ufs.br`)
- **Orientador (Computação)**: Prof. Dr. Gilton José Ferreira da Silva (`gilton@academico.ufs.br`)
- **Coorientadora (Enfermagem)**: Profª. Drª. Ana Waleska de Menezes Seixas Souza (`anawaleska@academico.ufs.br`)
- **Conformidade Ética**: Aprovado pelo Comitê de Ética em Pesquisa (CEP/UFS) sob o CAAE nº **91836925.8.0000.5546** e parecer favorável da Gerência de Ensino e Pesquisa (GEP/EBSERH) do Hospital Universitário da UFS. Operação **100% simulada**, sem contato com prontuários ou bases reais de pacientes.

---

## 📁 Arquitetura e Organização do Repositório

O repositório foi organizado com separação estrita de responsabilidades, arquitetura limpa e pastas temáticas de fácil navegação:

```text
SIGEA/
├── backend/                      # API RESTful Spring Boot 3.3 (Java 21 LTS)
│   ├── pom.xml                   # Gestão de dependências Maven
│   ├── mvnw, mvnw.cmd            # Maven Wrapper
│   └── src/                      # Código-fonte (java) e Migrações Flyway V1 a V4 (resources)
│
├── frontend/                     # SPA Angular 18 (Standalone Components + TailwindCSS)
│   ├── package.json, angular.json # Configuração e scripts do ecossistema Angular
│   └── src/                      # Componentes visuais, rotas, services e interceptors
│
├── database/                     # Scripts de Banco de Dados PostgreSQL 16
│   ├── ddl/                      # Esquema relacional estruturado (01_schema.sql)
│   ├── dml/                      # Carga GTT baseline, usuários e homologação
│   ├── generate_seed.py          # Script Python gerador da massa hiper-realista
│   ├── init_database.sql         # Script orquestrador de inicialização de containers
│   └── README.md                 # Documentação detalhada de banco de dados
│
├── config/                       # Configurações centralizadas de infraestrutura e deploy
│   ├── docker/                   # Arquivos de containerização
│   │   ├── docker-compose.yml    # Orquestração local do banco PostgreSQL 16
│   │   ├── Dockerfile            # Imagem de produção multi-stage Java 21
│   │   ├── .dockerignore         # Exclusões de contexto Docker
│   │   └── .env.example          # Modelo documentado de variáveis de ambiente
│   └── deploy/                   # Arquivos declarativos de deploy em nuvem
│       ├── render.yaml           # Infraestrutura como código (IaC) para Render Web Service
│       └── vercel.json           # Reverse proxy (/api/*) e SPA routing para Vercel
│
├── docs/                         # Documentação técnica e acadêmica estruturada
│   ├── arquitetura/              # Arquitetura sistêmica e diretrizes acadêmicas
│   │   ├── ARCHITECTURE.md       # Diagramas, stack e fluxos da plataforma
│   │   └── ACADEMIC_DOCUMENTATION.md # Mapeamento institucional UFS
│   ├── fases/                    # Planejamento e checklist das etapas do projeto
│   │   ├── PHASE1_TASKS.md       # Fase 1: Arquitetura base, usuários, layout desktop
│   │   ├── PHASE2_TASKS.md       # Fase 2: Metodologia GTT e ferramentas da qualidade
│   │   └── PHASE3_TASKS.md       # Fase 3: Módulo educacional, turmas e dashboards
│   ├── homologacao/              # Guias para homologação, testes e bancas
│   │   ├── TUTORIAL_TESTES_HOMOLOGACAO.md # Roteiro de testes com professores e alunos
│   │   ├── GUIA_DEPLOY_HOMOLOGACAO.md     # Guia passo a passo de deploy gratuito
│   │   └── ALUNOS_HOMOLOGACAO.md          # Credenciais completas para testes
│   └── referencias/              # Documentos canônicos do IHI e apresentações
│       ├── GTT_SEED_DATA.md      # Dicionário de dados dos 53 gatilhos clínicos
│       ├── metodologia_global_trigger_tool.pdf # Manual IHI GTT 2ª Edição (2019)
│       └── tema_trabalho_conclusao_de_curso.pdf # Apresentação da Profª Ana Waleska
│
├── projeto_departamental/        # Proposta Oficial de TCC para o DCOMP/UFS (LaTeX abnTeX2)
│   ├── Conteudo/                 # Capítulos textuais (01 a 04)
│   ├── Pre_Textual/              # Pré-textuais (Resumo em Português, Abreviaturas)
│   ├── Imagens/                  # Brasões oficiais (UFS, DCOMP)
│   ├── Bibliografia.bib          # Referências canônicas rigorosamente alinhadas
│   ├── projeto_departamental.tex # Documento mestre LaTeX (abnTeX2 / DCOMP)
│   ├── projeto_departamental.pdf # PDF oficial homologado (32 páginas)
│   └── Makefile                  # Automação de compilação (make, make clean, make distclean)
│
├── docker-compose.yml            # Orquestração do PostgreSQL local (raiz para conveniência)
├── Dockerfile                    # Multi-stage build (raiz para compatibilidade Render)
├── .dockerignore                 # Exclusões Docker
├── vercel.json                   # Configuração de roteamento (raiz para compatibilidade Vercel)
├── .gitignore                    # Regras de exclusão do repositório Git
├── .sdkmanrc                     # Controle de versão do Java 21 LTS
└── GEMINI.md                     # Diretrizes de engenharia e regras de negócio inegociáveis
```

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia | Detalhes & Bibliotecas |
| :--- | :--- | :--- |
| **Backend** | **Java 21 LTS** / **Spring Boot 3.3.x** | Spring Security 6 (JWT stateless), Spring Data JPA, Hibernate, Bean Validation, MapStruct, Lombok, OpenAPI 3 (Swagger UI). |
| **Frontend** | **Angular 18+** | Standalone Components, Signals, novos blocos `@if`/`@for`, Reactive Forms, Angular Material, TailwindCSS. |
| **Banco de Dados** | **PostgreSQL 16 LTS** | Flyway Migrations (V1 a V4), Tipos JSONB para prontuários simulados. |
| **Testes & Quality** | **JUnit 5**, **Mockito**, **Jest** | Quality Gate de 100% de cobertura de código em testes unitários e de integração. |
| **Documentação** | **SpringDoc**, **Compodoc**, **LaTeX** | Javadoc + Swagger UI interativo; Compodoc no Angular; abnTeX2 no Projeto Departamental. |
| **Cloud Homologação** | **Render** + **Vercel** + **Neon.tech** | Infraestrutura 100% gratuita, com reverse proxy `/api/*` nativo eliminando bloqueios de CORS. |

---

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
- **Java 21 LTS** (OpenJDK recomendado via SDKMAN: `sdk env`)
- **Node.js 20+** e **npm 10+**
- **Docker** ou **Podman** com suporte a compose

### 1. Iniciar o Banco de Dados (PostgreSQL 16)
Na raiz do projeto, execute:
```bash
docker compose up -d
```
O PostgreSQL será inicializado na porta `5432` com as credenciais padrão (`sigea_admin` / `sigea_dev_password` / banco `sigea_gtt`).

### 2. Iniciar o Backend (Spring Boot 3.3)
Em um terminal, execute:
```bash
cd backend
./mvnw spring-boot:run
```
O backend compilará o código, executará as migrações automáticas do Flyway (`V1` a `V4`) e subirá na porta `8080`.
- **API Base**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

### 3. Iniciar o Frontend (Angular 18)
Em outro terminal, execute:
```bash
cd frontend
npm install
npm start
```
Acesse a aplicação em seu navegador: **`http://localhost:4200`**.

---

## 🌐 Ambiente de Homologação em Produção

O SIGEA encontra-se publicado e homologado nos seguintes endereços:

- **Frontend SPA**: [https://sigea.vercel.app](https://sigea.vercel.app)
- **Backend API**: [https://sigea-backend.onrender.com](https://sigea-backend.onrender.com)
- **Documentação Swagger**: [https://sigea-backend.onrender.com/swagger-ui/index.html](https://sigea-backend.onrender.com/swagger-ui/index.html)

### Credenciais Iniciais de Administrador (Primeiro Acesso)
| Perfil | Nome | E-mail Institucional | Senha Provisória | Status Inicial |
| :--- | :--- | :--- | :--- | :--- |
| `ADMIN` | Matheus Araujo Pereira | `matheusaraujopereira@academico.ufs.br` | `sigea123` | Troca Obrigatória Pendente |
| `ADMIN` | Profª. Drª. Ana Waleska de Menezes Seixas Souza | `anawaleska@academico.ufs.br` | `sigea123` | Troca Obrigatória Pendente |

*(Ambos os usuários possuem `mustChangePassword: true`, exigindo a definição de senha pessoal definitiva no primeiro login).*

---

## 📜 Documentação Oficial (Projeto Departamental)

O documento oficial de Projeto Departamental para o DCOMP/UFS está disponível na pasta `projeto_departamental/`:
- **Documento Compilado**: [projeto_departamental.pdf](projeto_departamental/projeto_departamental.pdf) (32 páginas em conformidade ABNT/DCOMP)
- **Compilação**:
  ```bash
  cd projeto_departamental
  make        # Compilação completa via pdflatex e bibtex
  make clean  # Remove arquivos intermediários preservando o PDF
  ```

---

## ⚖️ Licença e Direitos Autorais

Desenvolvido no âmbito das atividades acadêmicas e de pesquisa da Universidade Federal de Sergipe (UFS). Todos os direitos reservados.
