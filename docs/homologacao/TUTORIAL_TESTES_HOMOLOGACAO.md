# SIGEA — Roteiro Completo de Testes e Homologação

**Projeto**: Sistema Inteligente de Gestão de Eventos Adversos (SIGEA)  
**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza, Prof. Dr. Gilton José Ferreira da Silva  
**Ambiente**: Fedora 44 Workstation | Stack: Spring Boot 3.3 (Java 21 LTS), Angular 18, PostgreSQL 16 LTS  
**URL do Frontend Local**: [http://localhost:4200](http://localhost:4200)  
**URL da API / Swagger Local**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  
**URL de Produção Cloud**: [https://sigea.vercel.app](https://sigea.vercel.app) | API: [https://sigea-backend-vzq0.onrender.com](https://sigea-backend-vzq0.onrender.com)  

---

## 🔑 1. Tabela de Credenciais Iniciais (Baseline)

O banco de dados de homologação é provisionado no estado limpo inicial (Flyway `V1__initial_schema_and_baseline.sql`), contendo **exclusivamente os dois administradores institucionais**, com primeiro acesso obrigatório pendente (`must_change_password = true`):

| Perfil | Nome de Exibição | E-mail Institucional (@academico.ufs.br) | Senha Inicial | Matrícula UFS | Status 1º Acesso |
| :---: | :--- | :--- | :---: | :---: | :---: |
| **ADMIN** | **Matheus Araujo Pereira** | `matheusaraujopereira@academico.ufs.br` | `SigeaUFS@2026` | *Nulo* | Pendente (`true`) |
| **ADMIN** | **Profª. Drª. Ana Waleska de Menezes Seixas Souza** | `anawaleska@academico.ufs.br` | `SigeaUFS@2026` | *Nulo* | Pendente (`true`) |

> [!NOTE]
> Os 6 módulos, 53 gatilhos e 9 gravidades NCC MERP já vêm 100% pré-configurados e ativos.  
> Turmas, atividades e estudantes adicionais devem ser criados a partir dos fluxos abaixo para validar o ciclo completo.

---

## 🛡️ 2. TUTORIAL DE TESTES: PERFIL ADMINISTRADOR (ADM)

O perfil Administrador possui governança completa: gestão de usuários, taxonomia clínica IHI-GTT e estrutura acadêmica.

### Caso de Teste ADM-01: Primeiro Acesso e Troca Obrigatória de Senha
1. Acesse a tela de login (`/login`).
2. Insira o e-mail: `matheusaraujopereira@academico.ufs.br` e senha inicial: `SigeaUFS@2026`.
3. Clique em **Entrar no Sistema**.
4. **Resultado Esperado**:
   - O sistema detecta `must_change_password = true` e redireciona automaticamente para `/first-login`.
   - A navegação pelas demais rotas permanece bloqueada até que a senha seja redefinida.
   - Digite a senha atual (`SigeaUFS@2026`), a nova senha pessoal e a confirmação.
   - Clique em **Atualizar Senha e Continuar**.
   - Redirecionamento com sucesso para a tela inicial (`/gtt/modules`), com a flag `must_change_password` atualizada para `false`.

### Caso de Teste ADM-02: Visão do AppShell e Ergonomia Desktop
1. Verifique a barra lateral (Sidebar) com a identidade clínica institucional (`#1E3A8A`).
2. Confirme o badge `ADMIN` e o nome do usuário logado.
3. Teste o recolhimento da sidebar: alterne entre o modo expandido (256px) e compacto (80px).
4. Verifique a presença de todos os menus administrativos:
   - **Metodologia GTT**, **Módulo Acadêmico**, **Gestão GTT**, **Gestão de Usuários** e **Meu Perfil**.

### Caso de Teste ADM-03: Gestão e Cadastro de Usuários (Regras Institucionais UFS)
1. Na Sidebar, clique em **Gestão de Usuários** (`/users`).
2. Verifique a exibição inicial dos dois administradores.
3. **Validação de Regra de E-mail Institucional**:
   - Clique em **Novo Usuário**.
   - Tente inserir um e-mail pessoal como `usuario@gmail.com`.
   - Confirme a rejeição imediata do formulário: apenas `@academico.ufs.br` é aceito.
4. **Cadastro de Docente**:
   - Nome: `Prof. Dr. Gilton José Ferreira da Silva`
   - E-mail: `gilton@academico.ufs.br`
   - Perfil: `PROFESSOR`
   - Observe que o campo **Matrícula** permanece desabilitado/oculto para docentes.
   - Clique em **Salvar**. Confirme o toast de sucesso e senha provisória gerada.
5. **Cadastro de Estudante**:
   - Clique em **Novo Usuário**.
   - Nome: `Lucas Gabriel Fontes`
   - E-mail: `lucas.fontes@academico.ufs.br`
   - Perfil: `STUDENT`
   - Observe que o campo **Matrícula** torna-se estritamente **obrigatório**.
   - Matrícula: `20260001001`
   - Clique em **Salvar**.
6. **Ações no Usuário**:
   - Teste a busca rápida com debounce digitando `Gilton`.
   - Teste a alternância de status ativo/inativo via modal de confirmação.
   - Teste a redefinição de senha com envio de nova credencial temporária.

### Caso de Teste ADM-04: Governança do Catálogo IHI-GTT
1. **Módulos GTT** (`/admin/gtt/modules`):
   - Verifique os 6 módulos padronizados: `CUIDADOS`, `MEDICACAO`, `CIRURGICO`, `UTI`, `PERINATAL`, `URGENCIA`.
   - Teste a edição de um módulo ou criação de módulo complementar.
2. **Gatilhos Clínicos** (`/admin/gtt/triggers`):
   - Confirme a listagem paginada dos **53 gatilhos clínicos oficiais** (10 por página).
   - Filtre por módulo (ex: `MEDICACAO`) e verifique `M1` a `M13`.
   - Teste o filtro por busca textual (ex: `Naloxona`, `Glicemia`).
3. **Gravidades NCC MERP** (`/admin/gtt/severities`):
   - Verifique as 9 categorias (A até I).
   - Categorias A–D: Sem dano (`is_harm = false`).
   - Categorias E–I: Com dano / Evento Adverso (`is_harm = true`).

### Caso de Teste ADM-05: Criação de Turma Acadêmica
1. Na Sidebar, acesse **Turmas Acadêmicas** (`/academic/classes`).
2. Clique em **Nova Turma**.
3. Preencha:
   - Nome da Disciplina: `Auditoria Clínica e Metodologia Global Trigger Tool`
   - Código: `ENF0052`
   - Semestre: `2026.1`
   - Professor Responsável: Selecione `Prof. Dr. Gilton José Ferreira da Silva` ou a própria Profª Ana.
4. Salve e valide a exibição do card da turma.
5. Acesse os detalhes da turma e vincule o estudante cadastrado (`Lucas Gabriel Fontes`).

---

## 👩‍🏫 3. TUTORIAL DE TESTES: PERFIL PROFESSOR (PROF)

O docente planeja atividades, insere prontuários simulados estruturados, acompanha turmas e avalia submissões.

### Caso de Teste PROF-01: Autenticação Docente
1. Acesse `/login` e insira as credenciais do docente cadastrado.
2. Caso seja o primeiro acesso, complete a redefinição de senha obrigatória.
3. Observe que o menu do Professor oculta com segurança as opções administrativas de governança.

### Caso de Teste PROF-02: Criação de Atividade com Prontuário Simulado
1. Acesse **Turmas Acadêmicas** e selecione a turma criada.
2. Clique em **Nova Atividade** (`/academic/activities/new`).
3. Preencha os dados pedagógicos:
   - Título: `Estudo de Caso 01: Nefrotoxicidade por Antimicrobiano e Diálise`
   - Descrição: `Investigação de evento adverso associado a uso de Vancomicina na UTI Adulto`.
   - Data Limite de Entrega: data futura.
4. Estruture o Prontuário Clínico Simulado:
   - **Paciente**: `Givaldo Bispo Menezes`, 63 anos, Leito 10 UTI.
   - **Evoluções**: Inserção de relato clínico descrevendo oligúria e aumento expressivo de escórias renais.
   - **Prescrições**: `Vancomicina 1g EV a cada 12h`.
   - **Exames**: Creatinina basal `1.0 mg/dL` subindo para `4.6 mg/dL`; Dosagem sérica de Vancomicina `48 mcg/mL`.
5. Clique em **Salvar Atividade**.

### Caso de Teste PROF-03: Fila de Correção e Avaliação
1. Acesse a atividade na visão docente e clique em **Corrigir Submissões**.
2. Filtre por **Pendentes de Correção**.
3. Clique em **Avaliar Submissão** de um aluno que já submeteu a resolução:
   - Analise os gatilhos apontados (`M5`, `C3`) e as gravidades atribuídas (`Categoria F`).
   - Avalie as ferramentas da qualidade preenchidas (Ishikawa, 5W2H, GUT, PDCA).
   - Atribua a nota (0 a 10) e digite o parecer pedagógico fundamentado.
   - Clique em **Salvar Avaliação**.
4. Valide a atualização instantânea do status para **Avaliado** e o recálculo dos indicadores do Dashboard.

---

## 👨‍🎓 4. TUTORIAL DE TESTES: PERFIL ESTUDANTE (STUDENT)

O discente acessa materiais formativos, audita prontuários simulados, identifica gatilhos GTT, classifica danos e aplica ferramentas de gestão da qualidade.

### Caso de Teste ALUNO-01: Primeiro Acesso e Validação Cadastral
1. Acesse `/login` com o estudante (`lucas.fontes@academico.ufs.br`).
2. Realize a troca obrigatória de senha inicial.
3. Acesse **Meu Perfil** (`/profile`) e valide a exibição da **Matrícula UFS** (`20260001001`).

### Caso de Teste ALUNO-02: Resolução de Caso Clínico Simulado
1. Na Sidebar, clique em **Minhas Atividades** (`/academic/student/activities`).
2. Na aba **Pendentes**, localize a atividade disponível e clique em **Resolver Atividade**.
3. **Auditoria Clínica do Prontuário**:
   - Inspecione as abas de Evolução, Prescrição, Exames e Procedimentos.
4. **Marcação de Gatilhos GTT**:
   - Clique em **Adicionar Gatilho**.
   - Selecione `M5` (*Elevação de ureia ou creatinina sérica para valor duas vezes superior ao basal*).
   - Sinalize: **Houve Dano ao Paciente?** = `Sim` (Evento Adverso).
   - Selecione a gravidade NCC MERP: **Categoria F** (*Dano temporário com prolongamento de internação*).
   - Insira a justificativa clínica baseada nos achados laboratoriais.
5. **Aplicação das Ferramentas da Qualidade**:
   - Preencha o **Diagrama de Ishikawa (6M)** (Método, Mão de Obra, Medida, etc.).
   - Preencha a **Matriz GUT** (Gravidade, Urgência, Tendência).
   - Estruture o plano de ação **5W2H** e o ciclo **PDCA**.
6. **Submissão**:
   - Clique em **Enviar Resolução** e confirme no modal.
   - Valide a migração automática para a aba **Enviadas**.

### Caso de Teste ALUNO-03: Consulta de Feedback e Nota
1. Acesse a aba **Avaliadas** em **Minhas Atividades**.
2. Abra a submissão avaliada pelo professor.
3. Verifique a nota atribuída e o parecer detalhado do docente.

---

## 💻 5. Comandos de Inicialização e Testes Automatizados

### Execução Local:
```bash
# 1. Banco de Dados PostgreSQL
podman start sigea-postgres || docker compose -f config/docker/docker-compose.yml up -d

# 2. Backend Spring Boot (Java 21)
cd backend && ./mvnw spring-boot:run

# 3. Frontend Angular (Porta 4200)
cd frontend && npm start
```

### Validação do Quality Gate (100% de Cobertura):
```bash
# Testes do Backend (JUnit 5 + MockMvc + JaCoCo)
cd backend && ./mvnw clean verify

# Testes do Frontend (Jest + Coverage)
cd frontend && npm test -- --coverage
```
