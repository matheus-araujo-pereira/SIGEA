# Guia Completo de Deploy em Ambiente de Homologação Gratuito (Zero Custo)
## SIGEA — Sistema Inteligente de Gestão de Eventos Adversos

Este guia orienta o processo de publicação do **SIGEA** em um ambiente de nuvem **100% gratuito**, sem necessidade de cartão de crédito, preparado para homologação e validação completa pela **Profª. Drª. Ana Waleska** e pelo **Prof. Dr. Gilton**.

---

## 1. Arquitetura de Homologação Selecionada

Para garantir estabilidade, isolamento e zero custo permanente:

| Camada | Provedor Gratuito | Especificação & Benefícios |
| :--- | :--- | :--- |
| **Banco de Dados** | **[Neon.tech](https://neon.tech)** | PostgreSQL 16 nativo gerenciado, Serverless, gratuito vitalício (não expira em 30 dias), sem necessidade de cartão. |
| **Backend API** | **[Render.com](https://render.com)** | Web Service Docker gratuito (512MB RAM, Java 21 LTS + Spring Boot 3.3). Build multi-stage otimizado com SerialGC. |
| **Frontend SPA** | **[Vercel](https://vercel.com)** | Edge Hosting gratuito, CDN global ultrarrápida, SSL automático, com **Reverse Proxy** `/api/*` apontando para o Render (eliminando problemas de CORS no navegador). |

---

## 2. Carga de Dados Inicial (Flyway V1 - Baseline Canônico)

O banco de dados é populado **automaticamente** pelo Spring Boot / Flyway (`V1__initial_schema_and_baseline.sql`) assim que o backend sobe pela primeira vez. Não é necessário executar scripts manuais via terminal SQL.

### Resumo dos Dados Provisionados:
- **2 Administradores Oficiais**: Matheus Araujo Pereira e Profª. Drª. Ana Waleska de Menezes Seixas Souza (ambos com `ADMIN` e `must_change_password = true`).
- **Catálogo IHI-GTT Completo**: 6 módulos clínicos e 53 gatilhos padronizados ativos.
- **Taxonomia NCC MERP**: 9 níveis de gravidade de dano (Categorias A a I) ativos.
- **Base Limpa**: 0 alunos, 0 turmas e 0 atividades (ambiente pronto para início do ciclo letivo oficial).

---

## 3. Credenciais de Acesso Inicial (Primeiro Login Pendente)

| Perfil | Nome de Exibição | E-mail Institucional (@academico.ufs.br) | Senha Provisória | Status Inicial |
| :---: | :--- | :--- | :---: | :--- |
| **ADMIN** | **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `SigeaUFS@2026` | Troca Obrigatória Pendente |
| **ADMIN** | **Profª. Drª. Ana Waleska** | `anawaleska@academico.ufs.br` | `SigeaUFS@2026` | Troca Obrigatória Pendente |

*(Ambos os usuários possuem `mustChangePassword: true`, exigindo a definição de senha pessoal definitiva no primeiro login).*

---

## 4. Passo a Passo do Deploy

### Passo 1: Criar o Banco de Dados no Neon.tech (Tempo estimado: 2 min)

1. Acesse **[neon.tech](https://neon.tech)** e faça login com sua conta GitHub.
2. Clique em **"Create Project"**.
   - **Project name**: `sigea-db`
   - **Postgres version**: `16`
   - **Region**: Selecione a mais próxima (ex: `US East (Ohio)` ou `US East (N. Virginia)`).
3. No Dashboard do projeto, localize a caixa **"Connection Details"**:
   - Mude o dropdown de exibição de `psql` para **`JDBC`**.
   - A URL exibida terá o seguinte formato:
     ```text
     jdbc:postgresql://ep-xyz.us-east-2.aws.neon.tech/neondb?sslmode=require
     ```
   - Guarde os dados:
     - **Host / URL JDBC**
     - **Database**: `neondb` (ou o nome criado)
     - **User**: (ex: `neondb_owner`)
     - **Password**: (exibida no painel)

---

### Passo 2: Subir o Backend no Render.com (Tempo estimado: 5 min)

1. Acesse **[render.com](https://render.com)** e faça login com seu GitHub.
2. Clique em **"New +"** no canto superior direito e selecione **"Web Service"**.
3. Escolha **"Build and deploy from a Git repository"** e selecione o repositório `SIGEA`.
4. Configure as opções básicas:
   - **Name**: `sigea-backend`
   - **Region**: A mesma escolhida no Neon (ex: `Ohio (US East)`).
   - **Branch**: `main`
   - **Root Directory**: Deixe em branco (o Dockerfile está localizado em `backend/Dockerfile`).
   - **Runtime**: Selecione **`Docker`**.
   - **Dockerfile Path**: `backend/Dockerfile`
   - **Instance Type**: Selecione **`Free`** (512 MB RAM, 0.1 CPU).
5. Role a página até **"Environment Variables"** e adicione as seguintes variáveis:

| Key | Value | Descrição |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://ep-misty-queen-ac9yrnse-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require` | URL JDBC do Neon (`sa-east-1` São Paulo) |
| `SPRING_DATASOURCE_USERNAME` | `neondb_owner` | Usuário do banco Neon |
| `SPRING_DATASOURCE_PASSWORD` | `npg_mAIlaQOHi56z` | Senha do banco Neon |
| `JWT_SECRET` | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` | Chave secreta HMAC-SHA de 256 bits |
| `CORS_ALLOWED_ORIGINS` | `*` | Permite conexões CORS universais em homologação |

6. Clique em **"Create Web Service"**.
7. O Render iniciará a compilação do container Docker:
   - O Maven compilará a aplicação Java 21 (`mvn clean package -DskipTests`).
   - O binário JRE Alpine será iniciado.
   - O Flyway executará as migrações `V1`, `V2`, `V3` e `V4` populando todo o banco de dados.
8. Ao finalizar o deploy (mensagem `Started SigeaBackendApplication`), copie a URL pública gerada pelo Render:
   - Exemplo: `https://sigea-backend.onrender.com`

---

### Passo 3: Subir o Frontend na Vercel (Tempo estimado: 3 min)

1. Acesse **[vercel.com](https://vercel.com)** e faça login com seu GitHub.
2. Clique em **"Add New..."** -> **"Project"**.
3. Importe o repositório `SIGEA`.
4. Na tela de configuração:
   - **Project Name**: `sigea`
   - **Framework Preset**: Selecione **`Angular`**.
   - **Root Directory**: Clique em *Edit* e selecione a pasta **`frontend`**.
   - **Build and Output Settings**:
     - *Build Command*: `npm run build`
     - *Output Directory*: `dist/frontend/browser`
     - *Install Command*: `npm install`
5. **Configuração do Proxy do Backend**:
   - No arquivo [vercel.json](file:///home/matheus/Projetos/SIGEA/frontend/vercel.json) já configurado no repositório, o tráfego `/api/*` é roteado diretamente para a URL primária do Render:
     ```json
     {
       "source": "/api/:path*",
       "destination": "https://sigea-backend-vzq0.onrender.com/api/:path*"
     }
     ```
   - *Vantagem do Proxy*: O navegador chama `https://sigea.vercel.app/api/auth/login` diretamente; a Vercel encaminha a chamada para o Render no backend sem bloqueios de CORS.
6. Clique em **"Deploy"**.
7. O frontend estará publicado em `https://sigea.vercel.app`.

---

## 5. Prevenção de Sleep Mode no Render (Keep-Alive Gratuito)

- No plano gratuito do Render, o backend entra em hibernação após 15 minutos sem tráfego.
- Para manter o **SIGEA permanentemente ativo e responsivo**, utilize o serviço gratuito **[cron-job.org](https://cron-job.org)**:
  - **URL**: `https://sigea-backend-vzq0.onrender.com/api/public/ping`
  - **Agendamento**: a cada 10 minutos (`*/10 * * * *`)
  - **Status de retorno**: `200 OK` (endpoint ultraleve que não consulta o banco de dados e mantém a JVM aquecida).
