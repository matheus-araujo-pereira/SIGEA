# SIGEA-GTT: Sistema Inteligente de Gestão de Eventos Adversos - Global Trigger Tool
**Instituição**: Universidade Federal de Sergipe (UFS) — DCOMP / Departamento de Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**Orientadores**: Profª. Drª. Ana Waleska de Menezes Seixas Souza, Prof. Dr. Gilton José Ferreira da Silva  
**Aprovação Ética**: Comitê de Ética em Pesquisa (CEP/UFS) — Parecer CAAE nº 91836925.8.0000.5546  
**Ambiente de Desenvolvimento**: Fedora 44 Workstation (x86_64) | Antigravity IDE & AGY CLI  

---

## 1. Apresentação e Arquitetura do Sistema
O **SIGEA-GTT** é uma plataforma hospitalar e acadêmica de alta precisão concebida para operacionalizar a metodologia **Global Trigger Tool (GTT)** do *Institute for Healthcare Improvement (IHI)*. O sistema permite a detecção ativa, auditoria clínica em duas etapas, mensuração de taxas epidemiológicas de danos e gestão de eventos adversos em prontuários hospitalares simulados para ensino e pesquisa.

O repositório está estruturado em monorepo modular:
- `backend/`: API RESTful em Java 21 LTS com Spring Boot 3.3.x, Spring Security 6 (JWT stateless), Spring Data JPA, Hibernate, Bean Validation, MapStruct e Flyway.
- `frontend/`: Single-Page Application (SPA) em Angular 18+ com Standalone Components, Signals reativos, novos blocos de controle de fluxo (`@if`, `@for`), Angular Material e TailwindCSS.
- `database/`: Scripts de migração Flyway (`backend/src/main/resources/db/migration/`) e sementes de dados clínicos simulados para PostgreSQL 16 LTS.
- `docs/`: Documentação arquitetural, especificações de fases, relatórios de homologação e referências clínicas do IHI e do CEP/UFS.
- `projeto_departamental/`: Monografia e relatórios técnicos em LaTeX seguindo estritamente as normas do DCOMP/UFS (`dcomp-abntex2`).
- `.antigravity/` e `.agents/`: Base de conhecimento operacional, regras executáveis, agentes especializados e skills da plataforma Antigravity.

---

## 2. Quality Gate Inegociável (Critério de Aceite Estrito)
Nenhuma implementação, refatoração, fase ou funcionalidade será considerada concluída sem o cumprimento de **100%** dos seguintes critérios:

1. **100% de Implementação Funcional**:
   - Total aderência aos requisitos clínicos do protocolo IHI-GTT e regras de negócio da UFS.
   - Validações de entrada em todas as camadas (DTOs com Bean Validation, formulários reativos no Angular).
   - Tratamento universal de exceções com respostas RFC 7807 (`ProblemDetail`) padronizadas no backend.
2. **100% de Cobertura de Testes (Zero Regressões)**:
   - **Backend**: Testes unitários e de integração utilizando JUnit 5, Mockito e MockMvc. Cobertura de 100% de classes de serviço, controladores, validadores e repositórios verificada via JaCoCo (`mvn clean verify`).
   - **Frontend**: Testes unitários e de componentes utilizando Jest cobrindo 100% de branches, funções e linhas (`npm test -- --coverage`).
   - Nenhum teste ignorado (`@Disabled`, `xit`, `xdescribe` ou skip) é permitido na base principal.
3. **100% de Documentação de Código**:
   - **Backend**: Javadoc completo em 100% das classes, interfaces, métodos de serviço e endpoints, acompanhado de anotações SpringDoc OpenAPI 3 (`@Operation`, `@ApiResponse`, `@Schema`). Swagger UI acessível em `/swagger-ui.html`.
   - **Frontend**: Documentação técnica e comentários JSDoc em componentes, serviços e modelos, homologados para geração via Compodoc.

---

## 3. Ergonomia Visual e Resoluções Homologadas (Desktop Only)
O SIGEA-GTT é um software estritamente projetado para **estações de trabalho clínicas e desktops hospitalares**. O design prioriza **densidade de informação**, escaneabilidade imediata e redução do cansaço visual de auditores e enfermeiros durante jornadas prolongadas de análise de prontuários.

1. **Resoluções de Tela Homologadas**:
   - **WUXGA**: 1920 × 1200 (Resolução padrão de referência)
   - **HD+**: 1600 × 900
   - **HD**: 1280 × 720 (Resolução mínima de conformidade)
   - **Ultrawide**: 2560 × 1080 e 3440 × 1440 (Proporção 21:9 para visualização clínica lado a lado)
   - **Proibição Estrita de Mobile**: Não implementar menus hambúrguer, barras inferiores de navegação mobile ou layouts estreitos de smartphone.
2. **Paleta Cromática Hospitalar e Semiótica Clínica**:
   - **Azul Clínico Profundo** (`#1E3A8A`, `#2563EB`): Identidade primária, cabeçalhos, ações principais e dados de auditoria institucional.
   - **Verde Esmeralda Cirúrgico** (`#059669`, `#10B981`): Confirmações de sucesso, ausência de dano (Categorias NCC MERP A–D) e finalização de auditorias.
   - **Cinzas Neutros de Baixo Cansaço** (`#F8FAFC`, `#F1F5F9`, `#E2E8F0`, `#0F172A`): Superfícies de tela, painéis de prontuário, bordas e tipografia com contraste WCAG AAA.
   - **Âmbar de Alerta Clínico** (`#D97706`, `#F59E0B`): Gatilhos detectados sob investigação preliminar e pendências de consenso.
   - **Carmim Cirúrgico de Alto Risco** (`#DC2626`, `#EF4444`): Eventos adversos com dano severo ou óbito (Categorias NCC MERP E a I).
3. **Padrões Universais de Componentes de Interface**:
   - **Tabelas de Dados**: Exatamente **10 registros por página**, paginação explícita com seletor de páginas, ordenação por cabeçalho de coluna e pesquisa com debounce de 300ms.
   - **Modais de Confirmação**: Obrigatórios para qualquer ação destrutiva, remoção, cancelamento ou conclusão irreversível de auditoria.
   - **Feedback Imediato**: Notificações via Toast Notification (sucesso, aviso, erro) com duração configurável e indicador de carregamento global (spinner sobreposto) para chamadas assíncronas.

---

## 4. Regras de Negócio e Segurança Institucional
1. **Domínio Exclusivo de E-mail**:
   - Apenas endereços sob o domínio `@academico.ufs.br` são aceitos pelo sistema. Qualquer outra terminação (`@ufs.br`, `@gmail.com`, etc.) deve ser rejeitada com validação rigorosa tanto no frontend quanto no backend (`Pattern` / Regex).
2. **Perfis de Acesso (RBAC)**:
   - Apenas 3 perfis existem no sistema: `ADMIN`, `PROFESSOR` e `STUDENT`.
3. **Campo Matrícula**:
   - Obrigatório e visível **somente** para o perfil `STUDENT` (formato numérico de matrícula discente da UFS).
   - Para os perfis `ADMIN` e `PROFESSOR`, o campo deve ser estritamente oculto no formulário e persistido como nulo (`null`) no banco de dados.
4. **Ciclo de Vida de Credenciais**:
   - O cadastro inicial de usuários gera uma senha provisória aleatória e define o indicador `mustChangePassword = true`.
   - Ao realizar o primeiro login, o sistema obrigatoriamente bloqueia a navegação geral e redireciona o usuário para a tela de redefinição obrigatória de senha.
   - Qualquer usuário autenticado pode atualizar sua senha a qualquer momento através do painel "Meu Perfil".
5. **Privacidade e Conformidade Ética**:
   - Todo o banco de dados opera com dados clínicos **100% fictícios e simulados**, respeitando o Parecer Consubstanciado do CEP/UFS (CAAE nº 91836925.8.0000.5546). Nenhuma informação real de paciente ou prontuário hospitalar verídico pode ser armazenada.

---

## 5. Protocolo Clínico Global Trigger Tool (IHI-GTT)
O SIGEA-GTT segue estritamente a metodologia preconizada pelo *Institute for Healthcare Improvement*:

1. **Os 6 Módulos Padronizados de Gatilhos**:
   - **C - Cuidados Gerais (Care)**: 19 gatilhos (ex.: parada cardiorrespiratória, queda do leito, readmissão em 30 dias).
   - **M - Medicamentos (Medication)**: 13 gatilhos (ex.: uso de Naloxona, Flumazenil, Vitamina K, glicemia < 50 mg/dL).
   - **S - Cirúrgico (Surgical)**: 11 gatilhos (ex.: retorno inesperado ao centro cirúrgico, troca de procedimento, lesão de órgão intraoperatório).
   - **I - Terapia Intensiva (Intensive Care)**: 4 gatilhos (ex.: reintubação traqueal, transferência não planejada para UTI).
   - **P - Perinatal**: 4 gatilhos (ex.: índice de Apgar < 6 no 5º minuto, trauma de parto).
   - **E - Pronto-Socorro / Emergência**: 2 gatilhos (ex.: retorno ao pronto-socorro em até 48 horas).
   - **Total**: **53 gatilhos clínicos padronizados**.
2. **Classificação de Gravidade do Dano (NCC MERP)**:
   - **Sem Dano**: Categorias A, B, C, D (quase-erros, circunstâncias com potencial de dano ou eventos que não causaram prejuízo ao paciente).
   - **Com Dano (Eventos Adversos GTT)**:
     - **Categoria E**: Dano temporário com necessidade de intervenção.
     - **Categoria F**: Dano temporário com prolongamento do período de internação.
     - **Categoria G**: Dano permanente.
     - **Categoria H**: Intervenção necessária para manter a vida.
     - **Categoria I**: Óbito do paciente relacionado ao evento adverso.
3. **Métricas Epidemiológicas Obrigatórias**:
   - Taxa de Eventos Adversos por 1.000 dias-paciente: `(Total de EA / Total de Dias de Internação da Amostra) * 1000`.
   - Taxa de Eventos Adversos por 100 admissões: `(Total de EA / Total de Prontuários Auditados) * 100`.
   - Percentual de Admissões com Dano: `(Prontuários com pelo menos 1 EA / Total de Prontuários Auditados) * 100`.
4. **Fluxo de Auditoria em Duas Fases**:
   - **Fase 1 (Revisão Primária)**: Executada por estudantes ou enfermeiros auditores (tempo limite recomendado: 20 minutos por prontuário). Marcação de gatilhos positivos e apontamento preliminar de dano.
   - **Fase 2 (Revisão de Consenso)**: Executada por professor orientador ou médico/enfermeiro sênior. Confirmação, recategorização ou descarte do dano, atribuindo a categoria final do NCC MERP.

---

## 6. Agentes Especializados do Sistema
O desenvolvimento e a manutenção do SIGEA-GTT são orquestrados por 5 agentes especializados, cujas diretrizes operacionais estão documentadas em `.antigravity/agents/` e `.agents/agents/`:

| Agente | Especialidade Principal | Escopo de Atuação |
| :--- | :--- | :--- |
| **`clinical-gtt-auditor`** | Metodologia IHI-GTT & Bioética | Validação clínica dos 53 gatilhos, regras NCC MERP, cálculo de métricas e verificação ética do CEP/UFS. |
| **`backend-engineer`** | Java 21, Spring Boot 3.3.x, PostgreSQL | Arquitetura RESTful, Spring Security 6 JWT, JPA, migrations Flyway, JUnit 5 e Jacoco 100%. |
| **`frontend-engineer`** | Angular 18+, Signals, TailwindCSS | SPA reativa, ergonomia desktop de alta densidade, acessibilidade, testes unitários Jest 100%. |
| **`dcomp-academic`** | LaTeX DCOMP-UFS (`dcomp-abntex2`) | Monografia, relatórios de qualificação, alinhamento com ABNT e consistência do cronograma. |
| **`devops-homologation`** | Docker, Neon, Render, Vercel | Ambientes de execução, containers Fedora, automação de CI/CD e estabilidade do deploy. |

---

## 7. Comandos Operacionais Homologados
- **Compilação e Testes do Backend**:
  ```bash
  cd backend && ./mvnw clean verify
  ```
- **Compilação e Testes do Frontend**:
  ```bash
  cd frontend && npm run build && npm test
  ```
- **Execução Integrada via Containers**:
  ```bash
  docker compose -f config/docker/docker-compose.yml up --build
  ```
- **Compilação da Monografia Acadêmica**:
  ```bash
  cd projeto_departamental && make clean && make
  ```

---
*Este documento é a norma regulatória máxima do repositório SIGEA-GTT no ambiente Antigravity IDE e deve ser rigorosamente respeitado por qualquer interação humana ou agente autônomo.*