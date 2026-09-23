# Diretrizes para Elaboração dos Documentos Acadêmicos e Técnicos (DCOMP-UFS)
**Sistema**: SIGEA: Sistema Inteligente de Gestão de Eventos Adversos – Global Trigger Tool  
**Discente**: Matheus Araujo Pereira (Matrícula: 202100114080)  
**Curso**: Engenharia de Computação  
**Departamento**: Departamento de Computação (DCOMP) – Centro de Ciências Exatas e Tecnologia (CCET)  
**Universidade**: Universidade Federal de Sergipe (UFS) – São Cristóvão, SE – 2026  
**Orientador**: Prof. Dr. Gilton José Ferreira da Silva  
**Coorientadora**: Prof.ª Dr.ª Ana Waleska de Menezes Seixas Souza  
**Template Base**: `abntex2-DCOMP-UFS` (branch master / tag oficial)

---

## 1. Estrutura dos Arquivos no Template LaTeX
O agente deve descompactar e integrar o conteúdo técnico nos seguintes arquivos-alvo da pasta do template:

1. `Modelo-TCC-DCOMP.tex`:
   - Configurar dados institucionais:
     - `\titulo{SIGEA: Sistema Inteligente de Gestão de Eventos Adversos – Global Trigger Tool}`
     - `\autor{Matheus Araujo Pereira}`
     - `\orientador{Prof. Dr. Gilton José Ferreira da Silva}`
     - `\coorientador{Prof.ª Dr.ª Ana Waleska de Menezes Seixas Souza}`
     - `\local{São Cristóvão – SE}`
     - `\data{2026}`
2. `Pre_Textual/Resumo.tex` e `Pre_Textual/Abstract.tex`:
   - Síntese da proposta: ferramenta de monitoramento simulado de eventos adversos (EAs) baseada na metodologia IHI-GTT para estudantes de saúde da UFS, integrando ferramentas de melhoria contínua (Ishikawa, PDCA, 5W2H, GUT, SWOT, Brainstorming).
3. `Pre_Textual/Abreviaturas.tex`:
   - GTT, IHI, EA, HU, UFS, DCOMP, NCC MERP, DER, UML, API, JWT, SPA, PDCA, GUT, SWOT, 5W2H.
4. `Conteudo/01_Introducao.tex` (Projeto Departamental - Seção 1 e 2):
   - Contextualização: subnotificação voluntária (apenas 5% a 10% de EAs relatados), histórico do IHI-GTT (padrão-ouro retrospectivo).
   - Justificativa acadêmica: capacitação dos discentes de enfermagem em ambiente simulado e seguro no HU/UFS.
   - Objetivos:
     - Geral: Construir o SIGEA como ferramenta educacional no ensino de segurança do paciente.
     - Específicos: Criação do ambiente de treino simulado, dashboards com indicadores IHI, relatórios analíticos e integração com ferramentas de melhoria da qualidade.
5. `Conteudo/02_Metodologia.tex` (Projeto Departamental - Seção 3):
   - Metodologia IHI-GTT: 6 módulos (Cuidados, Medicação, Cirúrgico, UTI, Perinatal e Urgência), 53 gatilhos oficiais e classificação de dano (A a I do NCC MERP).
   - Metodologia de Desenvolvimento: Engenharia de Software baseada em arquitetura em camadas, Spring Boot 3.3 LTS, Angular 18+ e PostgreSQL 16 LTS.
6. `Conteudo/03_EngenhariaRequisitos.tex`:
   - Requisitos Funcionais (RF01 a RF25): Domínio exclusivo `@academico.ufs.br`, alteração obrigatória de senha no 1º login, papéis de usuário (ADMIN, PROFESSOR, STUDENT), matrícula exclusiva para discentes, gerenciamento de turmas (`Matéria - Turma - Período`), prontuário simulado com cronômetro de 20 minutos, resolução individual de atividades, integração das 6 ferramentas da qualidade, correção com nota 0 a 10 e dashboards de desempenho.
   - Requisitos Não Funcionais (RNF01 a RNF10): Desempenho (<200ms de latência média de API), Segurança (JWT stateless e BCrypt), Responsividade focada estritamente em desktops e workstations (HD 1280x720, HD+ 1600x900, WUXGA 1920x1200 e Ultrawide 21:9), Integridade de dados e ergonomia hospitalar.
7. `Conteudo/04_DesignUX.tex`:
   - Personas: Mariana Resende (Discente de Enfermagem), Prof.ª Dr.ª Waleska (Docente de Saúde) e Prof. Dr. Gilton (Docente de Computação/Orientador).
   - Mapas de Empatia completos para cada persona (O que pensa/sente, O que escuta, O que vê, O que fala/faz, Dores e Ganhos).
8. `Conteudo/05_ModelagemDiagramas.tex`:
   - Diagrama Entidade-Relacionamento (DER) em sintaxe TikZ (`tikzpicture`).
   - Diagrama de Casos de Uso (UML) em TikZ/PGF.
   - Diagrama de Classes de Domínio (UML) em TikZ.
   - Arquitetura 4+1 de Philippe Kruchten:
     - Visão Lógica (Controllers, Services, Repositories, DTOs, Angular Services e Guards).
     - Visão de Desenvolvimento/Implementação (Estrutura de pacotes Maven e módulos Angular Standalone).
     - Visão de Processos (Stateless JWT, concorrência no JVM, sessões de avaliação).
     - Visão Física/Implantação (Browser do Acadêmico -> Reverse Proxy Nginx -> Spring Boot JVM -> PostgreSQL Server).
     - Visão de Casos de Uso (+1) (Cenário crítico: análise de prontuário, identificação de gatilho C1/M4, categorização de dano F e geração do Diagrama de Ishikawa).
9. `Conteudo/06_PlanoTrabalho.tex` (Projeto Departamental - Seção 4):
   - Estrutura de 12 meses dividida em 2 marcos obrigatórios:
     - Marco 1 (6 meses - Relatório Parcial): Configuração do ecossistema, Fases 1 e 2 implementadas, banco de dados seed populado, entrega da revisão teórica e especificação técnica.
     - Marco 2 (12 meses - Relatório Final): Fase 3 implementada (Módulo Educacional e Ferramentas da Qualidade), validação com estudantes de enfermagem da UFS, geração de métricas comparativas, redação final e submissão de patente/artigo científico.
   - Cronograma mensal em gráfico Gantt (`pgfgantt`).
   - Próxima reunião ordinária: 29 de setembro de 2026.
10. `Bibliografia.bib`:
    - Entradas BibTeX canônicas completas no padrão ABNT (IHI, OMS, NCC MERP, autores da área de enfermagem e engenharia de software).