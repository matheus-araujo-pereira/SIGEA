# Arquitetura e Design System do SIGEA

## 1. Arquitetura do Backend (Spring Boot 3.3 / Java 21)
- **Estrutura de Pacotes**:
  - `br.ufs.sigea.auth`: Configuração de segurança JWT, filtros de autenticação e validação `@academico.ufs.br`.
  - `br.ufs.sigea.user`: Gestão de usuários, alteração de senha e perfis (ADMIN, PROFESSOR, STUDENT).
  - `br.ufs.sigea.methodology`: Entidades e regras dos Módulos GTT, Gatilhos GTT e Classificações de Gravidade (A-I).
  - `br.ufs.sigea.educational`: Gestão de Turmas, Atividades, Casos Clínicos Simulados, Submissões e Correções.
  - `br.ufs.sigea.quality`: Modelos para armazenamento das ferramentas de qualidade integradas (Ishikawa, PDCA, GUT, SWOT, 5W2H, Brainstorming).
  - `br.ufs.sigea.dashboard`: Cálculos agregados de indicadores (EAs/1.000 pacientes-dia, EAs/100 admissões, % admissões com dano, distribuição A-I).
  - `br.ufs.sigea.common`: Tratamento global de exceções (`@ControllerAdvice`), paginação padronizada (Pageable default 10) e DTOs de resposta.

## 2. Arquitetura do Frontend (Angular 18+)
- **Estrutura Modular Standalone**:
  - `core/`: Serviços de Autenticação (`auth.service.ts`), Interceptores JWT (`jwt.interceptor.ts`, `error.interceptor.ts`), Guarda de Rotas (`auth.guard.ts`, `role.guard.ts`, `first-login.guard.ts`).
  - `shared/`: Componentes universais:
    - `confirm-dialog`: Modal de confirmação reversível/irreversível.
    - `data-table`: Tabela padrão de 10 itens com paginação e ordenação integrada.
    - `loading-spinner`: Overlay global reativo com Signals.
    - `toast-container`: Notificações acessíveis e não intrusivas.
    - `app-shell`: Layout com menu lateral retrátil ergonômico, cabeçalho com perfil, breadcrumb e botão de logout.
  - `features/`:
    - `auth/`: Login, Redefinição Obrigatória de Primeiro Acesso.
    - `profile/`: Perfil do Usuário e Alteração de Senha.
    - `users/`: Gestão de Usuários (Admin).
    - `gtt-config/`: Módulos, Gatilhos e Gravidades (CRUD Admin e Visualização Read-Only para todos).
    - `classes/`: Gestão de Turmas e Matrículas.
    - `activities/`: Criação, Resposta (com simulador de prontuário e ferramentas da qualidade), Correção e Histórico.
    - `dashboards/`: Visão analítica de desempenho da turma e métricas IHI-GTT.

## 3. Ergonomia Visual e Resoluções
- Grid fluido otimizado para larguras: 1280px, 1600px, 1920px e Ultrawide (>2560px).
- Em monitores Ultrawide, o conteúdo central possui limitação de largura máxima ergonômica (`max-w-7xl` ou `max-w-[1800px]`) para não dispersar o foco visual do avaliador hospitalar.
- Menu lateral retrátil com colapso para ícones, salvando estado no `localStorage`.