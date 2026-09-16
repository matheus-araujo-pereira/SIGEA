# Guia Completo de Deploy em Ambiente de Homologação Gratuito (Zero Custo)
## SIGEA-GTT — Sistema Inteligente de Gestão de Eventos Adversos

Este guia orienta o processo de publicação do **SIGEA-GTT** em um ambiente de nuvem **100% gratuito**, sem necessidade de cartão de crédito, preparado para homologação e validação completa pela **Profª. Drª. Ana Waleska** e pelo **Prof. Dr. Gilton**.

---

## 1. Arquitetura de Homologação Selecionada

Para garantir estabilidade, isolamento e zero custo permanente:

| Camada | Provedor Gratuito | Especificação & Benefícios |
| :--- | :--- | :--- |
| **Banco de Dados** | **[Neon.tech](https://neon.tech)** | PostgreSQL 16 nativo gerenciado, Serverless, gratuito vitalício (não expira em 30 dias), sem necessidade de cartão. |
| **Backend API** | **[Render.com](https://render.com)** | Web Service Docker gratuito (512MB RAM, Java 21 LTS + Spring Boot 3.3). Build multi-stage otimizado com SerialGC. |
| **Frontend SPA** | **[Vercel](https://vercel.com)** | Edge Hosting gratuito, CDN global ultrarrápida, SSL automático, com **Reverse Proxy** `/api/*` apontando para o Render (eliminando problemas de CORS no navegador). |

---

## 2. Carga de Dados Pré-Configurada (Flyway V4)

O banco de dados será populado **automaticamente** pelo Spring Boot / Flyway (`V4__seed_homologation_complete.sql`) assim que o backend subir pela primeira vez. Não é necessário executar scripts manuais via terminal SQL.

### Resumo dos Dados Provisionados:
- **1 Administrador**: Matheus Araujo Pereira
- **2 Professores**: Profª. Drª. Ana Waleska e Prof. Dr. Gilton
- **30 Alunos reais**: Matriculados igualmente em **todas as 4 turmas**, encabeçados pelo aluno modelo Lucas Gabriel Fontes
- **4 Turmas Acadêmicas**:
  1. `TURMA-2026.1-ANA`: **2026.1 (Encerrada)** — Ministrada por Ana Waleska (30 alunos, 3 atividades 100% avaliadas).
  2. `TURMA-2026.1-GIL`: **2026.1 (Encerrada)** — Ministrada por Gilton (30 alunos, 3 atividades 100% avaliadas).
  3. `TURMA-2026.2-ANA`: **2026.2 (Ativa)** — Ministrada por Ana Waleska (30 alunos, 1 atividade avaliada, 2 atividades abertas/pendentes até 31/12/2026).
  4. `TURMA-2026.2-GIL`: **2026.2 (Ativa)** — Ministrada por Gilton (30 alunos, 1 atividade avaliada, 2 atividades abertas/pendentes até 31/12/2026).
- **12 Atividades**: 3 em cada turma, contemplando os módulos de Medicamentos, Cuidados Gerais, Cirúrgico, Perinatal, UTI e Urgência.
- **240 Submissões Avaliadas**: Com notas de 7.0 a 10.0 e feedbacks pedagógicos detalhados já cadastrados.

---

## 3. Credenciais de Acesso para Homologação

Todas as contas foram configuradas com `must_change_password = FALSE`, permitindo login direto sem bloqueio de primeiro acesso:

### 3.1. Administrador
| Nome | E-mail Acadêmico | Senha de Teste | Perfil |
| :--- | :--- | :--- | :--- |
| **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `SigeaUFS@2026` | `ADMIN` |

### 3.2. Professores
| Nome | E-mail Acadêmico | Senha de Teste | Perfil |
| :--- | :--- | :--- | :--- |
| **Profª. Drª. Ana Waleska** | `anawaleska@academico.ufs.br` | `SigeaUFS@2026` | `PROFESSOR` |
| **Prof. Dr. Gilton** | `gilton@academico.ufs.br` | `SigeaUFS@2026` | `PROFESSOR` |

### 3.3. Aluno Modelo (Para simulação do fluxo discente)
| Nome | Matrícula | E-mail Acadêmico | Senha de Teste | Perfil |
| :--- | :--- | :--- | :--- | :--- |
| **Lucas Gabriel Fontes** | `2026000101` | `lucas.fontes@academico.ufs.br` | `SigeaUFS@2026` | `STUDENT` |

*(Os demais 29 alunos seguem o padrão de e-mail `nome.sobrenome@academico.ufs.br` e senha `SigeaUFS@2026`).*

---

## 4. Passo a Passo do Deploy

### Passo 1: Criar o Banco de Dados no Neon.tech (Tempo estimado: 2 min)

1. Acesse **[neon.tech](https://neon.tech)** e faça login com sua conta GitHub.
2. Clique em **"Create Project"**.
   - **Project name**: `sigea-gtt-db`
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
3. Escolha **"Build and deploy from a Git repository"** e selecione o repositório `SIGEA-GTT`.
4. Configure as opções básicas:
   - **Name**: `sigea-gtt-backend`
   - **Region**: A mesma escolhida no Neon (ex: `Ohio (US East)`).
   - **Branch**: `main`
   - **Root Directory**: Deixe em branco (o Dockerfile está localizado em `backend/Dockerfile`).
   - **Runtime**: Selecione **`Docker`**.
   - **Dockerfile Path**: `backend/Dockerfile`
   - **Instance Type**: Selecione **`Free`** (512 MB RAM, 0.1 CPU).
5. Role a página até **"Environment Variables"** e adicione as seguintes variáveis:

| Key | Value | Descrição |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://<HOST_NEON>/neondb?sslmode=require` | URL JDBC completa copiada do Neon |
| `SPRING_DATASOURCE_USERNAME` | `<USUARIO_NEON>` | Usuário gerado pelo Neon |
| `SPRING_DATASOURCE_PASSWORD` | `<SENHA_NEON>` | Senha gerada pelo Neon |
| `JWT_SECRET` | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` | Chave secreta HMAC-SHA de 256 bits |
| `CORS_ALLOWED_ORIGINS` | `*` | Permite conexões CORS universais em homologação |

6. Clique em **"Create Web Service"**.
7. O Render iniciará a compilação do container Docker:
   - O Maven compilará a aplicação Java 21 (`mvn clean package -DskipTests`).
   - O binário JRE Alpine será iniciado.
   - O Flyway executará as migrações `V1`, `V2`, `V3` e `V4` populando todo o banco de dados.
8. Ao finalizar o deploy (mensagem `Started SigeaBackendApplication`), copie a URL pública gerada pelo Render:
   - Exemplo: `https://sigea-gtt-backend.onrender.com`

---

### Passo 3: Subir o Frontend na Vercel (Tempo estimado: 3 min)

1. Acesse **[vercel.com](https://vercel.com)** e faça login com seu GitHub.
2. Clique em **"Add New..."** -> **"Project"**.
3. Importe o repositório `SIGEA-GTT`.
4. Na tela de configuração:
   - **Project Name**: `sigea-gtt`
   - **Framework Preset**: Selecione **`Angular`**.
   - **Root Directory**: Clique em *Edit* e selecione a pasta **`frontend`**.
   - **Build and Output Settings**:
     - *Build Command*: `npm run build`
     - *Output Directory*: `dist/frontend/browser`
     - *Install Command*: `npm install`
5. **Configuração do Proxy do Backend**:
   - No arquivo [vercel.json](file:///home/matheus/Projetos/SIGEA-GTT/frontend/vercel.json) que já está no repositório, caso sua URL do Render tenha um nome diferente de `sigea-gtt-backend.onrender.com`, basta atualizar o destino do proxy:
     ```json
     {
       "source": "/api/:path*",
       "destination": "https://SEU-APP-NOVO.onrender.com/api/:path*"
     }
     ```
   - *Vantagem do Proxy*: O navegador chama `https://sua-url-vercel.app/api/auth/login` diretamente; a Vercel encaminha a chamada para o Render no backend sem que o navegador sofra bloqueios de cookies de terceiros ou CORS.
6. Clique em **"Deploy"**.
7. Em aproximadamente 1 minuto, o frontend estará publicado e com link `https://sigea-gtt.vercel.app`.

---

## 5. Roteiro de Testes para a Profª Ana Waleska

Após o deploy, compartilhe a URL do sistema (`https://sigea-gtt.vercel.app`) com a Profª Ana Waleska acompanhada do seguinte roteiro sugerido:

1. **Login como Professora**:
   - Acessar com `anawaleska@academico.ufs.br` / `SigeaUFS@2026`.
   - Navegar para **"Minhas Turmas"**:
     - Constatar 2 turmas: uma de **2026.1 (Encerrada)** e uma de **2026.2 (Ativa)**.
     - Abrir a turma de **2026.1**: conferir as 3 atividades avaliadas com o histórico completo de notas dos 30 alunos.
     - Abrir a turma de **2026.2**: conferir 1 atividade já avaliada e 2 atividades pendentes de entrega (prazo até 31/12/2026).
     - Criar uma nova atividade avaliativa e testar a vinculação com os módulos GTT.
2. **Login como Administrador**:
   - Acessar com `matheusaraujopereira@academico.ufs.br` / `SigeaUFS@2026`.
   - Navegar para **"Gestão de Usuários"**: visualizar a listagem completa com os 33 usuários (filtro por perfil, paginação de 10 em 10).
   - Navegar para **"Catálogo de Módulos GTT"**: visualizar os 6 módulos GTT, gatilhos associados e regras de aplicação.
3. **Login como Aluno**:
   - Acessar com `lucas.fontes@academico.ufs.br` / `SigeaUFS@2026`.
   - Navegar para **"Minhas Atividades"**:
     - Visualizar histórico de 8 atividades avaliadas com feedbacks e notas atribuídas pelos professores.
     - Visualizar as 4 atividades pendentes (2 da turma da Profª Ana e 2 da turma do Profº Gilton).
     - Abrir uma atividade pendente e realizar uma submissão de teste para avaliação pela professora.

---

## 6. Observação Importante sobre o Plano Gratuito (Render Cold Start)

- No plano gratuito do Render, se o backend passar mais de 15 minutos sem receber requisições, ele entra em modo de hibernação (sleep mode).
- Ao realizar o **primeiro acesso**, o backend pode demorar cerca de 40 a 50 segundos para "acordar". Após isso, o sistema responde com alta velocidade.
- Se desejar evitar a hibernação durante o período de avaliação da banca, você pode utilizar um serviço gratuito de monitoramento como o **[UptimeRobot](https://uptimerobot.com)** configurado para fazer um ping na rota pública `/actuator/health` ou `/swagger-ui/index.html` a cada 10 minutos.
