# SIGEA-GTT: Sistema Inteligente de Gestão de Eventos Adversos - Global Trigger Tool
**Instituição**: Universidade Federal de Sergipe (UFS) - DCOMP / Enfermagem  
**Autor Líder**: Matheus Araujo Pereira  
**Orientadores**: Profª. Drª. Ana Waleska, Prof. Dr. Gilton  
**Ambiente**: Fedora 44 Workstation (x86_64) | Antigravity IDE  

---

## 1. Diretrizes Absolutas de Engenharia de Software
1. **Stack Tecnológica Oficial**:
   - **Backend**: Java 21 LTS, Spring Boot 3.3.x (Spring Security 6 com JWT stateless, Spring Data JPA, Hibernate, Bean Validation, MapStruct, Lombok).
   - **Frontend**: Angular 18+ (Standalone Components, Signals, novos blocos de controle `@if`, `@for`, Reactive Forms, Angular Material + TailwindCSS).
   - **Banco de Dados**: PostgreSQL 16 LTS (executando nativamente ou via container Podman/Docker no Fedora).
   - **Migrations**: Flyway.
   - **Documentação de Código**: Javadoc completo no backend + SpringDoc OpenAPI 3 (Swagger UI); Compodoc no frontend Angular.
2. **Critério de Aceite Estrito (Quality Gate)**:
   - Uma fase só é considerada concluída e liberada para a próxima quando:
     1. **100% de Implementação Funcional** comprovada.
     2. **100% de Cobertura de Testes** (Backend com JUnit 5 + Mockito + Jacoco; Frontend com Karma/Jasmine ou Jest cobrindo 100% de branches e linhas).
     3. **100% de Documentação** de classes, métodos, DTOs e endpoints gerada sem erros.
3. **Resoluções de Tela Homologadas**:
   - O layout deve ser milimetricamente testado e ajustado para desktops e workstations:
     - **WUXGA**: 1920 × 1200
     - **HD+**: 1600 × 900
     - **HD**: 1280 × 720
     - **Ultrawide**: 2560 × 1080 e 3440 × 1440 (21:9)
   - **Nota**: O sistema não é mobile; foca em densidade de dados e ergonomia visual clínica para desktops.
4. **Padrão de Usabilidade & Design Hospitalar Moderno**:
   - Paleta de cores suave: Azuis clínicos profundos (`#1E3A8A`, `#2563EB`), verdes esmeralda hospitalares suaves para confirmações (`#059669`), cinzas de baixo cansaço visual (`#F8FAFC`, `#F1F5F9`, `#0F172A`), âmbar para alertas (`#D97706`) e carmim cirúrgico para gravidades H e I (`#DC2626`).
   - Padrão universal: Tabelas com exatamente 10 registros por página, ordenação por coluna, filtros de pesquisa com debounce, modais de confirmação para ações destrutivas/irreversíveis, feedback via Toast Notification e indicador global de carregamento (spinner sobreposto).
5. **Regras de Negócio Inegociáveis**:
   - Domínio de e-mail exclusivo: `@academico.ufs.br`. Qualquer outro e-mail deve ser rejeitado no backend e no frontend.
   - Perfis de Usuário: Apenas `ADMIN`, `PROFESSOR` e `STUDENT`.
   - Campo Matrícula: Obrigatório **somente** para `STUDENT`. Oculto e nulo para `ADMIN` e `PROFESSOR`.
   - Autenticação e Senhas: Senha provisória gerada automaticamente pelo sistema; no primeiro login, o sistema obriga o redirecionamento e a redefinição de senha. O próprio usuário pode alterar sua senha a qualquer instante no seu perfil.