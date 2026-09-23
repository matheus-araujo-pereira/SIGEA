# SIGEA — Roteiro Completo de Testes e Homologação

**Projeto**: Sistema Inteligente de Gestão de Eventos Adversos — Global Trigger Tool (SIGEA)  
**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza, Prof. Dr. Gilton José Ferreira da Silva  
**Ambiente**: Fedora 44 Workstation | Stack: Spring Boot 3.3 (Java 21 LTS), Angular 18, PostgreSQL 16 LTS  
**URL do Frontend**: [http://localhost:4200](http://localhost:4200)  
**URL da API / Swagger**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)  

---

## 🔑 1. Tabela Unificada de Credenciais para Testes

Todos os usuários abaixo já estão cadastrados na base de dados com a senha padrão inicial indicada:

| Perfil | Nome de Exibição | E-mail Institucional (@academico.ufs.br) | Senha de Acesso | Matrícula UFS | Finalidade Principal nos Testes |
| :---: | :--- | :--- | :---: | :---: | :--- |
| **ADMIN** | **Matheus Araujo Pereira (EU)** | `matheusaraujopereira@academico.ufs.br` | `SigeaUFS@2026` | *Nulo* | Homologação de Usuários, Gestão GTT (Módulos/Gatilhos/Gravidades) e Turmas |
| **PROFESSOR** | **Profª. Drª. Ana Waleska de Menezes Seixas Souza** | `anawaleska@academico.ufs.br` | `SigeaUFS@2026` | *Nulo* | Turmas 2025.2 e 2026.1, Criação de Atividades, Fila de Correção e Dashboards |
| **PROFESSOR** | **Prof. Dr. Gilton José Ferreira da Silva** | `gilton@academico.ufs.br` | `SigeaUFS@2026` | *Nulo* | Turmas 2025.2 e 2026.1, Avaliação de Submissões, Atribuição de Notas e Feedback |
| **STUDENT** | **Lucas Gabriel Fontes** | `lucas.fontes@academico.ufs.br` | `SigeaUFS@2026` | `20260001001` | **Aluno Teste Principal**: Matrícula ativa, resolução interativa de caso clínico |
| **STUDENT** | **Mariana Barreto Santana** | `primeiro.acesso@academico.ufs.br` | `SigeaUFS@2026` | `20260001030` | **Teste de 1º Acesso**: Obriga troca imediata de senha no primeiro login |

---

## 🛡️ 2. TUTORIAL DE TESTES: PERFIL ADMINISTRADOR (ADM)

O perfil Administrador tem acesso irrestrito a todos os módulos do sistema, controle de usuários, taxonomia IHI-GTT e governança acadêmica.

### Caso de Teste ADM-01: Autenticação e Visão do AppShell
1. Acesse [http://localhost:4200/login](http://localhost:4200/login).
2. Insira o e-mail: `matheusaraujopereira@academico.ufs.br` e a senha: `SigeaUFS@2026`.
3. Clique em **Entrar no Sistema**.
4. **Resultado Esperado**:
   - Redirecionamento instantâneo para a tela inicial (`/gtt/modules`).
   - Barra lateral (Sidebar) retrátil exibindo logotipo com as cores clínicas do projeto (`#1E3A8A`).
   - Nome `Matheus Araujo Pereira` e badge estilizada `ADMIN` no rodapé da Sidebar.
   - Presença dos menus: **Metodologia GTT**, **Módulo Acadêmico**, **Gestão GTT**, **Gestão de Usuários** e **Conta**.
   - Teste de responsividade: clique no botão de recolher menu (ícone de setas no topo da sidebar) para alternar entre modo expandido (256px) e modo compacto (80px).

### Caso de Teste ADM-02: Gestão de Usuários do Sistema
1. Na Sidebar, clique em **Gestão de Usuários** (`/users`).
2. **Validação de Densidade e Paginação**:
   - Verifique que a tabela exibe **exatamente 10 registros por página**.
   - Use o componente de paginação no rodapé para navegar entre as páginas (123 usuários cadastrados = 13 páginas).
3. **Filtros com Debounce**:
   - Digite `Waleska` no campo de busca: a tabela deve filtrar em tempo real para a professora.
   - Alterne o filtro de Perfil para `PROFESSOR`, `ADMIN` e `STUDENT` para conferir a filtragem imediata.
4. **Cadastro de Novo Usuário (Regras de Negócio UFS)**:
   - Clique no botão **Novo Usuário**.
   - Teste de Validação de E-mail: tente preencher um e-mail comum (ex: `teste@gmail.com`). O sistema deve exibir erro e rejeitar, aceitando **exclusivamente** `@academico.ufs.br`.
   - Teste de Matrícula Condicional:
     - Selecione o perfil `STUDENT`: observe que o campo **Matrícula** torna-se obrigatório.
     - Selecione o perfil `PROFESSOR` ou `ADMIN`: observe que o campo **Matrícula** desaparece ou torna-se desabilitado/nulo.
   - Cadastre um usuário de teste:
     - Nome: `Estudante Homologação UFS`
     - E-mail: `homologacao.aluno@academico.ufs.br`
     - Perfil: `STUDENT` | Matrícula: `20269999001`
   - Clique em **Cadastrar Usuário**. Confirme a notificação Toast de sucesso e a presença do novo usuário na lista.
5. **Ações no Usuário**:
   - Clique no ícone de chave (**Redefinir Senha**): confirme a geração de nova senha provisória e a flag de primeiro acesso ativada.
   - Clique no ícone de lápis (**Editar**): altere o nome e salve.
   - Clique no botão de alternância (**Status Ativo/Inativo**): confirme no modal e valide a alteração do badge de status.

### Caso de Teste ADM-03: Gestão de Módulos GTT
1. Na Sidebar, clique em **Gerenciar Módulos** (`/admin/gtt/modules`).
2. Valide a listagem dos **6 módulos canônicos**:
   - `CUIDADOS`, `MEDICACAO`, `CIRURGICO`, `UTI`, `PERINATAL`, `URGENCIA`.
3. Clique em **Novo Módulo**:
   - Código: `AMBULAT` | Nome: `Ambulatório Geral` | Descrição: `Gatilhos em consultas externas e pós-alta`.
   - Salve e confira o registro.
4. Clique em **Editar** no módulo criado e depois desative-o via modal de confirmação.

### Caso de Teste ADM-04: Gestão de Gatilhos Clínicos IHI-GTT
1. Na Sidebar, clique em **Gerenciar Gatilhos** (`/admin/gtt/triggers`).
2. Valide a presença dos **53 gatilhos oficiais** divididos em 10 itens por página.
3. Utilize o filtro por módulo para inspecionar:
   - Módulo Medicação: verifique os gatilhos `M1` a `M13` (ex: `M3` - RNI > 6, `M4` - Glicemia < 50 mg/dL, `M9` - Naloxona).
   - Módulo Cirúrgico: verifique os gatilhos `S1` a `S11`.
4. Clique em **Novo Gatilho**:
   - Módulo: `CUIDADOS` | Código: `C16` | Nome: `Flebite em Acesso Venoso Periférico` | Descrição: `Surgimento de sinais flogísticos de flebite grau III/IV em punção venosa periférica`.
   - Salve e valide na tabela.

### Caso de Teste ADM-05: Gestão de Gravidades NCC MERP
1. Na Sidebar, clique em **Gerenciar Gravidades** (`/admin/gtt/severities`).
2. Verifique as 9 categorias padronizadas de **A** a **I**:
   - Categorias A a D com badge cinza/verde claro: *Sem dano ao paciente*.
   - Categorias E a I com badge âmbar/carmim: *Dano Real ao paciente (Eventos Adversos)*.
3. Clique em **Editar** na Categoria H (Necessidade de intervenção para manter a vida) para validar a modal de edição.

### Caso de Teste ADM-06: Visão e Governança de Turmas Acadêmicas
1. Na Sidebar, clique em **Turmas Acadêmicas** (`/academic/classes`).
2. Observe a exibição das **4 turmas da UFS**:
   - `Enfermagem na Atenção à Saúde do Adulto e do Idoso I - T01 - 2025.2` (Concluída)
   - `Gestão da Qualidade e Segurança do Paciente - T01 - 2025.2` (Concluída)
   - `Enfermagem em Terapia Intensiva e Cuidados Críticos - T01 - 2026.1` (Ativa)
   - `Auditoria Clínica e Metodologia Global Trigger Tool - T02 - 2026.1` (Ativa)
3. Clique em **Detalhes da Turma** (ícone de olho) em uma das turmas:
   - Verifique a lista completa dos **30 alunos matriculados**.
   - Verifique as **3 atividades avaliativas vinculadas**.
4. Clique no botão **Dashboard** de uma turma ativa para inspecionar os indicadores analíticos em tempo real.

---

## 👩‍🏫 3. TUTORIAL DE TESTES: PERFIL PROFESSOR (PROF)

O perfil Docente gerencia o ciclo pedagógico: turmas sob sua docência, acompanhamento dos 30 alunos, elaboração de casos clínicos simulados com prontuários reais, correção de submissões e análise de indicadores no Dashboard.

### Caso de Teste PROF-01: Autenticação Docente
1. Acesse [http://localhost:4200/login](http://localhost:4200/login).
2. Entre com a credencial da Profª Ana Waleska:
   - E-mail: `anawaleska@academico.ufs.br` | Senha: `SigeaUFS@2026`
   *(Ou teste com o Prof. Gilton: `gilton@academico.ufs.br` | Senha: `SigeaUFS@2026`)*.
3. **Resultado Esperado**:
   - Acesso ao sistema com badge `PROFESSOR`.
   - Os menus restritos de administração (`Gestão GTT` e `Gestão de Usuários`) ficam ocultos de forma segura.
   - Acesso pleno a **Turmas Acadêmicas**, **Catálogos GTT** e **Meu Perfil**.

### Caso de Teste PROF-02: Minhas Turmas e Alunos
1. Clique em **Turmas Acadêmicas** (`/academic/classes`).
2. Note o título dinâmico: **Minhas Turmas** — exibindo exclusivamente as turmas ministradas pela docente:
   - `Enfermagem na Atenção à Saúde do Adulto e do Idoso I - T01 - 2025.2`
   - `Enfermagem em Terapia Intensiva e Cuidados Críticos - T01 - 2026.1`
3. Clique em **Visualizar Detalhes** na turma `2026.1`:
   - Confira os 30 alunos matriculados com nomes e matrículas oficiais.
   - Teste de Inclusão: utilize o campo **Adicionar Aluno** para vincular um discente adicional.

### Caso de Teste PROF-03: Dashboard Clínico e Métricas IHI-GTT
1. Na lista de turmas, clique no ícone de gráfico (**Dashboard**) da turma de 2025.2 ou 2026.1 (`/academic/classes/:id/dashboard`).
2. **Indicadores Clínicos em Tempo Real**:
   - **Total de Pacientes-Dia Auditados**.
   - **Taxa de Eventos Adversos por 1.000 Pacientes-Dia** (cálculo metodológico oficial do IHI).
   - **Percentual de Pacientes com Dano**.
   - **Distribuição de Gatilhos por Módulo**: Gráfico com proporção em Cuidados, Medicação, UTI e Cirúrgico.
   - **Matriz de Gravidade NCC MERP**: Distribuição visual das categorias E, F, G e H.

### Caso de Teste PROF-04: Elaboração de Atividade com Prontuário Simulado
1. Na tela de detalhes da turma ou no cabeçalho, clique em **Nova Atividade** (`/academic/activities/new`).
2. Preencha o cabeçalho pedagógico:
   - Título: `Estudo de Caso: Intoxicação por Sedativos e Reversão com Flumazenil`
   - Descrição: `Análise de sedação excessiva em enfermaria cirúrgica pós-administração de Diazepam`.
   - Prazo de Entrega: Selecione uma data futura.
3. Preencha o Prontuário Simulado Estruturado:
   - **Dados do Paciente**: Nome `Sebastião Correia`, 72 anos, Leito `203-B`, Dias de internação: `4`.
   - **Notas de Evolução**: Adicione evolução com horário, papel profissional (`Enfermeiro Assistencial`) e relato clínico.
   - **Prescrições**: Adicione medicamento (`Diazepam 10mg EV`), posologia e checagem.
   - **Exames Laboratoriais**: Adicione exames relevantes com valores basais e alterados.
4. Clique em **Salvar Atividade** e valide a inserção imediata na turma.

### Caso de Teste PROF-05: Fila de Correção e Avaliação de Submissões
1. Acesse os detalhes da turma `Enfermagem em Terapia Intensiva e Cuidados Críticos - T01 - 2026.1`.
2. Localize o **Caso Clínico 07: Pneumonia Associada à Ventilação Mecânica (PAV) e Sepse na UTI Adulto**.
3. Clique em **Corrigir Atividade** (`/academic/activities/:id/grading`).
4. **Visão da Fila**:
   - Observe o contador de entregas: alunos que entregaram vs pendentes.
   - Filtre por **Pendentes de Correção**.
5. Clique em **Avaliar Submissão** em um dos alunos pendentes (`/academic/submissions/:id/grade`):
   - **Revisão Clínica**: Inspecione o prontuário no painel esquerdo e os gatilhos marcados pelo aluno no painel direito (`I1` - PAV, gravidade `F`, justificativa do aluno).
   - **Ferramentas da Qualidade**: Abra as abas de Ishikawa (6M), 5W2H e Ciclo PDCA preenchidos pelo aluno.
   - **Lançamento de Avaliação**:
     - Atribua a nota (ex: `9.50`).
     - Insira o parecer pedagógico: *"Excelente análise de causa-raiz no diagrama de Ishikawa, correlacionando a aspiração traqueal e a higiene oral deficiente ao desenvolvimento da infecção."*
   - Clique em **Salvar Avaliação**.
6. Valide o retorno à fila de correção com o status atualizado para **Avaliado** e a nota sincronizada no Dashboard da turma.

---

## 👨‍🎓 4. TUTORIAL DE TESTES: PERFIL ESTUDANTE / ALUNO (STUDENT)

O discente acessa o catálogo clínico formativo, visualiza suas atividades pendentes, analisa o prontuário do paciente, identifica gatilhos GTT, classifica danos e resolve as ferramentas de gestão da qualidade.

### Caso de Teste ALUNO-01: Troca Obrigatória de Senha no 1º Acesso
1. Acesse [http://localhost:4200/login](http://localhost:4200/login).
2. Entre com o usuário configurado para primeiro acesso:
   - E-mail: `primeiro.acesso@academico.ufs.br` | Senha inicial: `SigeaUFS@2026`
3. **Resultado Esperado**:
   - O sistema detecta `must_change_password: true` e bloqueia a navegação regular.
   - Redirecionamento forçado para `/first-login`.
   - Mensagem de orientação: *"Primeiro Acesso ao SIGEA — Por motivos de segurança institucional, defina sua senha pessoal definitiva."*
4. Digite a senha atual (`SigeaUFS@2026`), a nova senha (ex: `UfsAluno@2026`) e a confirmação.
5. Clique em **Atualizar Senha e Continuar**.
6. Valide o redirecionamento para a tela inicial do sistema.

### Caso de Teste ALUNO-02: Consulta ao Guia Clínico GTT
1. Efetue login com o discente principal:
   - E-mail: `lucas.fontes@academico.ufs.br` | Senha: `SigeaUFS@2026`
   - Matrícula exibida: `20260001001`.
2. Acesse **Gatilhos Clínicos** (`/gtt/triggers`):
   - Busque pelo gatilho `M9` (Naloxona) ou `I4` (Reintubação).
   - Leia a definição operacional e as situações clínicas indicativas de dano.
3. Acesse **Gravidades (A-I)** (`/gtt/severities`):
   - Estude a diferença entre erros potenciais (A-D) e eventos adversos com necessidade de intervenção (E-I).

### Caso de Teste ALUNO-03: Portal Minhas Atividades
1. Na Sidebar, clique em **Minhas Atividades** (`/academic/student/activities`).
2. Observe a organização em abas/filtros:
   - **Pendentes**: Atividades abertas aguardando resolução do estudante (ex: *Caso Clínico 09: Insuficiência Renal Aguda por Vancomicina* e *Caso Clínico 12: Queda com Dano em Idoso*).
   - **Enviadas**: Atividades já submetidas aguardando avaliação docente.
   - **Avaliadas**: Atividades corrigidas com nota e feedback disponíveis.

### Caso de Teste ALUNO-04: Resolução Completa de Caso Clínico Simulado
1. Na aba **Pendentes**, localize o **Caso Clínico 09: Insuficiência Renal Aguda Nefrotóxica por Vancomicina e Diálise de Urgência**.
2. Clique no botão **Resolver Atividade** (`/academic/activities/:id/resolve`).
3. **Exploração do Prontuário do Paciente**:
   - Navegue pela aba **Dados Gerais**: Paciente `Givaldo Bispo Menezes`, 63 anos, Leito 10 UTI.
   - Navegue pela aba **Evoluções**: Identifique o relato do médico nefrologista sobre a oligúria e o salto da creatinina.
   - Navegue pela aba **Exames**: Note a Creatinina basal de `1.0 mg/dL` subindo para `4.6 mg/dL` (elevação > 4x) e a vancocinemia em `48 mcg/mL`.
   - Navegue pela aba **Prescrições**: Verifique o uso de Vancomicina e a suspensão.
   - Navegue pela aba **Procedimentos**: Note a passagem de cateter e sessão de diálise de urgência.
4. **Identificação e Seleção de Gatilhos GTT**:
   - Clique em **Adicionar Gatilho Identificado**.
   - Selecione o gatilho `M5` (*Elevação de ureia ou creatinina sérica para valor duas vezes superior ao basal*).
   - Marque a opção: **Houve Dano ao Paciente (Evento Adverso)** = `Sim`.
   - Selecione a Gravidade NCC MERP: **Categoria F** (*Dano temporário com necessidade de iniciar ou prolongar hospitalização*).
   - Escreva a Justificativa Clínica: *"A creatinina sérica quadruplicou em relação ao basal e a vancocinemia atingiu níveis tóxicos de 48 mcg/mL, desencadeando perda funcional renal aguda e diálise de urgência."*
   - Adicione o segundo gatilho: `C3` (*Diálise aguda*), gravidade `F`.
5. **Aplicação das Ferramentas da Qualidade**:
   - **Diagrama de Ishikawa (6M)**:
     - Problema Central: `Injúria Renal Aguda por toxicidade de Vancomicina`.
     - Preencha causas em Método (*Ausência de protocolo de dosagem da vancocinemia de vale no 3º dia*), Medida (*Demora na solicitação de função renal diária*) e Mão de Obra (*Falta de farmacêutico clínico na UTI no fim de semana*).
   - **Matriz GUT**:
     - Problema: `Falta de monitorização terapêutica de antimicrobianos nefrotóxicos`.
     - Gravidade: `5` | Urgência: `5` | Tendência: `4` (Score calculado: `100`).
   - **Plano de Ação 5W2H**:
     - O quê: `Implantar protocolo de monitoramento sérico obrigatório de vancomicina`.
     - Por quê: `Prevenir nefrotoxicidade e necessidade de terapia renal substitutiva`.
     - Quem: `Comissão de Farmácia e Terapêutica`.
     - Onde: `Unidades de Internação e UTI do HU-UFS`.
   - **Ciclo PDCA**:
     - Preencha as fases de Planejar, Executar, Checar e Padronizar.
6. **Submissão**:
   - Clique em **Enviar Resolução da Atividade**.
   - Confirme no modal de envio definitivo.
   - Verifique o Toast de confirmação e a migração automática da atividade para a aba **Enviadas**.

### Caso de Teste ALUNO-05: Consulta de Nota e Feedback do Professor
1. No menu **Minhas Atividades**, clique na aba **Avaliadas**.
2. Localize uma atividade já avaliada (ex: *Caso Clínico 07*).
3. Clique em **Ver Avaliação / Feedback** (`/academic/submissions/:id/feedback`).
4. Verifique:
   - Badge com a nota oficial (ex: `9.50 / 10.00`).
   - Card com o parecer textual da Profª Ana Waleska.
   - Confronto entre as respostas enviadas pelo aluno e os dados clínicos do caso.

### Caso de Teste ALUNO-06: Meu Perfil e Alteração Voluntária de Senha
1. Clique em **Meu Perfil** (`/profile`).
2. Valide as informações discentes:
   - Nome: `Lucas Gabriel Fontes`
   - E-mail institucional: `lucas.fontes@academico.ufs.br`
   - Perfil: `STUDENT` | Matrícula Acadêmica: `20260001001`.
3. No card de Segurança da Conta, preencha a senha atual e a nova senha desejada para testar a alteração voluntária com sucesso.

---

## 💻 5. Comandos para Executar e Parar os Serviços

| Componente | Comando para Execução | Porta |
| :--- | :--- | :---: |
| **Banco de Dados (PostgreSQL 16)** | `podman start sigea-postgres` | `5432` |
| **Backend (Spring Boot 3.3)** | `cd backend && mvn spring-boot:run` | `8080` |
| **Frontend (Angular 18)** | `cd frontend && npm start` | `4200` |

*Os três serviços já se encontram em execução em sua máquina para teste imediato.*
