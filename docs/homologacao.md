# SIGEA — Guia de Homologação em Nuvem e Roteiro de Testes Clínicos

**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Departamento de Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza, Prof. Dr. Gilton José Ferreira da Silva  
**Aprovação Ética**: Comitê de Ética em Pesquisa (CEP/UFS) — Parecer CAAE nº 91836925.8.0000.5546  

Este documento reúne o passo a passo completo para deploy em ambiente de nuvem gratuito (zero custo permanente) e o roteiro detalhado de homologação clínica dos três perfis de usuário (**ADMIN**, **PROFESSOR** e **STUDENT**).

---

## ☁️ PARTE 1: ARQUITETURA E DEPLOY EM NUVEM (ZERO CUSTO)

A infraestrutura de homologação foi desenhada para operar com estabilidade, isolamento e zero custo, sem necessidade de cartão de crédito:

| Camada | Provedor Gratuito | Especificação & Benefícios |
| :--- | :--- | :--- |
| **Banco de Dados** | **[Neon.tech](https://neon.tech)** | PostgreSQL 16 LTS nativo, Serverless, gratuito vitalício (não expira em 30 dias), alta disponibilidade. |
| **Backend API** | **[Render.com](https://render.com)** | Web Service Docker gratuito (512MB RAM, Java 21 LTS + Spring Boot 3.3). Build multi-stage otimizado com SerialGC. |
| **Frontend SPA** | **[Vercel](https://vercel.com)** | Edge Hosting gratuito, CDN global ultrarrápida, SSL automático, com **Reverse Proxy** `/api/*` apontando para o Render (eliminando problemas de CORS no navegador). |

---

### Passo 1: Provisionamento do Banco de Dados no Neon.tech

1. Acesse **[neon.tech](https://neon.tech)** e faça login com sua conta GitHub.
2. Crie um novo projeto:
   * **Project name**: `sigea-db`
   * **Postgres version**: `16`
   * **Region**: Selecione a mais próxima (ex.: `US East (Ohio)` ou `US East (N. Virginia)`).
3. No painel principal, selecione a aba **Connection Details** e altere o modo para **JDBC**:
   * O formato da URL será:
     ```text
     jdbc:postgresql://ep-xyz.us-east-2.aws.neon.tech/neondb?sslmode=require
     ```
   * Copie o **Host**, **Database** (`neondb`), **Username** e **Password**.

> [!NOTE]
> Não é necessário rodar scripts SQL manualmente. Assim que o backend iniciar pela primeira vez conectado ao Neon, o **Flyway** executará automaticamente a migração `V1__initial_schema_and_baseline.sql`, criando as 10 tabelas e inserindo os 6 módulos, 53 gatilhos, 9 gravidades e os 2 administradores oficiais.

---

### Passo 2: Deploy do Backend no Render.com

1. Acesse **[render.com](https://render.com)** e faça login com sua conta GitHub.
2. Clique em **New +** > **Web Service**.
3. Selecione o repositório `SIGEA` conectado.
4. Configure os parâmetros do Web Service:
   * **Name**: `sigea-backend`
   * **Region**: Mesma região selecionada no Neon (ex.: `Ohio (US East)`).
   * **Branch**: `main`
   * **Root Directory**: `backend`
   * **Runtime**: `Docker`
   * **Instance Type**: `Free`
5. Adicione as **Variáveis de Ambiente (Environment Variables)**:
   * `SPRING_PROFILES_ACTIVE`: `prod`
   * `DB_HOST`: Host do Neon (ex.: `ep-xyz.us-east-2.aws.neon.tech`)
   * `DB_PORT`: `5432`
   * `DB_NAME`: `neondb`
   * `DB_USER`: Usuário do Neon
   * `DB_PASSWORD`: Senha do Neon
   * `JWT_SECRET`: Chave secreta de pelo menos 32 caracteres (ex.: `sigea-secret-key-ufs-computacao-enfermagem-2026-very-secure-key-32chars`)
   * `JAVA_TOOL_OPTIONS`: `-XX:+UseSerialGC -Xss512k -XX:MaxRAMPercentage=75.0` (otimização para o limite de 512MB do plano gratuito).
6. Clique em **Create Web Service**. Aguarde a conclusão do build Docker (cerca de 3 a 5 minutos).
7. Copie a URL pública gerada pelo Render (ex.: `https://sigea-backend-xyz.onrender.com`).

---

### Passo 3: Deploy do Frontend na Vercel

1. Acesse **[vercel.com](https://vercel.com)** e faça login com sua conta GitHub.
2. Clique em **Add New...** > **Project** e importe o repositório `SIGEA`.
3. Configure o projeto:
   * **Framework Preset**: `Angular`
   * **Root Directory**: `frontend`
4. Verifique o arquivo `frontend/vercel.json`, que atua como **Proxy Reverso** transparente:
   ```json
   {
     "rewrites": [
       { "source": "/api/(.*)", "destination": "https://sigea-backend-xyz.onrender.com/api/$1" },
       { "source": "/(.*)", "destination": "/index.html" }
     ]
   }
   ```
   *Certifique-se de que a URL de destino corresponde à URL gerada para o seu backend no Render.*
5. Clique em **Deploy**. A Vercel compilará a SPA em menos de 1 minuto e fornecerá a URL pública (ex.: `https://sigea.vercel.app`).

---

## 🧪 PARTE 2: ROTEIRO COMPLETO DE TESTES E HOMOLOGAÇÃO CLÍNICA

### 🔑 Credenciais Iniciais de Homologação

| Perfil | Nome de Exibição | E-mail Institucional (@academico.ufs.br) | Senha Inicial | Matrícula UFS | 1º Acesso |
| :---: | :--- | :--- | :---: | :---: | :---: |
| **ADMIN** | **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `SigeaUFS@2026` | *Nulo* | Pendente (`true`) |
| **ADMIN** | **Profª. Drª. Ana Waleska** | `anawaleska@academico.ufs.br` | `SigeaUFS@2026` | *Nulo* | Pendente (`true`) |

---

### 🛡️ Roteiro 1: Perfil ADMINISTRADOR (ADM)

#### Caso de Teste ADM-01: Primeiro Acesso e Troca Obrigatória de Senha
1. Acesse a tela de login (`/login`).
2. Digite o e-mail: `matheusaraujopereira@academico.ufs.br` e a senha: `SigeaUFS@2026`.
3. Clique em **Entrar no Sistema**.
4. **Comportamento Esperado**: O sistema identifica `must_change_password = true` e redireciona automaticamente para `/first-login`. Rotas externas permanecem bloqueadas.
5. Digite a senha atual e a nova senha pessoal. Clique em **Atualizar Senha e Continuar**.
6. **Comportamento Esperado**: Redirecionamento bem-sucedido para a tela inicial, com acesso liberado a todos os módulos.

#### Caso de Teste ADM-02: Gestão de Usuários (RBAC Institucional)
1. No menu lateral, acerte em **Gestão de Usuários** (`/users`).
2. Clique em **Novo Usuário**.
3. **Validação de E-mail**: Tente cadastrar `teste@gmail.com`. Confirme a rejeição imediata (somente `@academico.ufs.br` é permitido).
4. **Cadastro de Docente**:
   * Nome: `Prof. Dr. Gilton José Ferreira da Silva`
   * E-mail: `gilton@academico.ufs.br`
   * Perfil: `PROFESSOR`
   * Confirme que o campo **Matrícula** permanece oculto. Clique em Salvar.
5. **Cadastro de Discente**:
   * Nome: `Mariana Resende da Silva`
   * E-mail: `mariana.resende@academico.ufs.br`
   * Perfil: `STUDENT`
   * Confirme que o campo **Matrícula** torna-se visível e obrigatório. Insira `202600123456`. Clique em Salvar.

#### Caso de Teste ADM-03: Auditoria dos Módulos, Gatilhos e Gravidades IHI-GTT
1. Acesse **Gestão GTT** > **Módulos** (`/gtt/modules/manage`):
   * Confirme a presença dos 6 módulos oficiais: Cuidados, Medicação, Cirúrgico, UTI, Perinatal e Urgência.
2. Acesse **Gatilhos** (`/gtt/triggers/manage`):
   * Confirme a listagem exata dos 53 gatilhos clínicos padronizados com códigos C1–C15, M1–M13, S1–S11, I1–I4, P1–P8 e E1–E2.
   * Filtre por módulo e valide que as descrições conferem 100% com o manual oficial do IHI.
3. Acesse **Gravidades** (`/gtt/severities/manage`):
   * Confirme a exibição das Categorias A a I da NCC MERP, com badge visual destacando dano real para Categorias E a I.

#### Caso de Teste ADM-04: Criação de Turma Acadêmica
1. Acesse **Módulo Acadêmico** > **Turmas** (`/academic/classes`).
2. Clique em **Nova Turma**.
3. Preencha os campos estruturais:
   * Disciplina: `Enfermagem em Cuidados Críticos`
   * Código da Turma: `T01`
   * Período Acadêmico: `2026.1`
   * Professor Responsável: Selecione `Prof. Dr. Gilton José Ferreira da Silva`
   * Alunos Matriculados: Marque a aluna `Mariana Resende da Silva`.
4. Salve e valide a exibição da turma no formato institucional UFS: `Enfermagem em Cuidados Críticos - T01 - 2026.1`.

---

### 👨‍🏫 Roteiro 2: Perfil PROFESSOR (DOCENTE)

#### Caso de Teste DOC-01: Formulação de Atividade com Caso Clínico Simulado
1. Faça login com a conta do docente (`gilton@academico.ufs.br`). Realize o primeiro acesso se solicitado.
2. Acesse **Turmas** e clique na turma vinculada `2026.1`.
3. Na aba de Atividades, clique em **Nova Atividade**.
4. Defina o Título (*"Auditoria Clínica Simulado 01 - Farmacovigilância e Nefrotoxicidade"*), Prazo de Entrega e insira o prontuário eletrônico fictício:
   * Identificação: Paciente masculino, 63 anos, Leito 10 - UTI.
   * Histórico de admissão e evolução clínica.
   * Prescrição com uso de Vancomicina sem dosagem sérica de vale e surgimento de Injúria Renal Aguda (Creatinina quadruplicada, diálise de urgência).
5. Salve a atividade. Confirme que ela fica disponível para os discentes matriculados.

#### Caso de Teste DOC-02: Correção de Submissões e Feedback Qualitativo
1. Após a submissão do discente, acerte na atividade e abra a lista de resoluções (`/academic/activities/{id}/grading`).
2. Clique em **Avaliar Submissão** da aluna Mariana Resende.
3. Analise as respostas:
   * Gatilhos identificados pelo discente (ex.: `M5` - Elevação de ureia/creatinina e `C3` - Diálise aguda).
   * Categoria de dano atribuída (NCC MERP F).
   * Preenchimento do Diagrama de Ishikawa, 5W2H e Ciclo PDCA.
4. Atribua nota de 0 a 10 (ex.: `9.5`) e redija o feedback pedagógico circunstanciado. Salve a avaliação.

#### Caso de Teste DOC-03: Análise do Dashboard Epidemiológico da Turma
1. Na tela da turma, abra a aba **Dashboard Analítico** (`/academic/classes/{id}/dashboard`).
2. Verifique os indicadores calculados automaticamente pelo SIGEA:
   * **Taxa de EAs por 1.000 pacientes-dia**.
   * **Taxa de EAs por 100 admissões**.
   * **Percentual de Admissões com Dano**.
   * Gráficos de distribuição dos eventos por Categoria de Dano (E a I) e Módulos GTT.
3. Teste o botão de **Exportar Relatório em CSV**.

---

### 👩‍🎓 Roteiro 3: Perfil STUDENT (DISCENTE)

#### Caso de Teste DIS-01: Resolução de Atividade de Auditoria Prática
1. Faça login como aluna (`mariana.resende@academico.ufs.br`).
2. Acesse **Minhas Atividades** (`/academic/activities/my`).
3. Abra a atividade pendente *"Auditoria Clínica Simulado 01"*.
4. **Ambiente de Auditoria Clínica**:
   * Observe o cronômetro orientativo de **20 minutos** no canto superior, estimulando a agilidade diagnóstica preconizada pelo IHI.
   * Leia a evolução médica, anotações de enfermagem e exames laboratoriais.
5. **Apontamento de Gatilhos e Gravidade**:
   * Na seção de triggers, marque `M5 - Elevação de ureia ou creatinina para valor 2x superior ao basal` e `C3 - Diálise aguda`.
   * Classifique a gravidade do dano na Categoria `F` (Dano temporário com necessidade de prolongar a hospitalização).
6. **Ferramentas da Qualidade Integradas**:
   * **Ishikawa (6M)**: Insira causas prováveis nos eixos (Método: ausência de protocolo de vancocinemia sérica; Medida: atraso na checagem da creatinina basal).
   * **5W2H / PDCA**: Elabore um plano de ação para monitorização terapêutica de antimicrobianos.
7. Submeta a atividade.

#### Caso de Teste DIS-02: Instrumento de Usabilidade SUS (System Usability Scale)
1. Concluída a atividade prática, acerte no menu **Avaliação do Sistema (SUS)** (`/sus`).
2. Responda às 10 afirmações padronizadas de John Brooke em escala Likert de 1 (*Discordo Totalmente*) a 5 (*Concordo Totalmente*).
3. Adicione sugestões qualitativas no campo de texto livre e envie.
4. **Comportamento Esperado**: O sistema calcula instantaneamente o escore SUS normalizado (0 a 100), classificação de adjetivo (ex.: *Excelente*) e nível de aceitabilidade (*Aceitável*).
