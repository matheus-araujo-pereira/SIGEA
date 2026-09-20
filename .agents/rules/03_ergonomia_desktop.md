# Regra 03: Ergonomia Visual Clínica e Resoluções de Tela

## 1. Princípio Fundamental: Desktop Exclusivo e Densidade Clínica
O SIGEA-GTT é uma ferramenta hospitalar e analítica voltada para computadores de mesa, workstations de enfermagem e postos de auditoria médica. O sistema **não é mobile** e não deve sacrificar a densidade informacional por padrões de tela pequena.

---

## 2. Resoluções de Tela Homologadas
O layout do frontend deve ser verificado e apresentar usabilidade fluida nas seguintes resoluções:
1. **WUXGA (1920 × 1200)**: Proporção 16:10. Espaço vertical ideal para leitura de prontuários clínicos e listas de checagem simultâneas.
2. **HD+ (1600 × 900)**: Proporção 16:9. Resolução intermediária comum em estações corporativas hospitalares.
3. **HD (1280 × 720)**: Proporção 16:9. Resolução mínima garantida sem quebras de layout ou sobreposição de componentes.
4. **Ultrawide (2560 × 1080 e 3440 × 1440)**: Proporção 21:9. Excelente para visão panorâmica: lista de prontuários à esquerda, visualizador de evolução clínica ao centro e painel de gatilhos à direita.

---

## 3. Paleta Cromática e Semiótica Hospitalar
- **Azul Primário Clínico**: `#1E3A8A` (Dark Blue 900) e `#2563EB` (Blue 600).
  - Usado em: Cabeçalho superior, botões de ação primária, abas ativas e gráficos institucionais.
- **Verde Hospitalar Suave**: `#059669` (Emerald 600) e `#10B981` (Emerald 500).
  - Usado em: Badges de status "Concluído", categorias sem dano (A-D), mensagens de sucesso e toasts afirmativos.
- **Cinzas Neutros de Baixo Cansaço**:
  - Background principal: `#F8FAFC` (Slate 50) ou `#F1F5F9` (Slate 100).
  - Superfícies de cartões e tabelas: `#FFFFFF` com bordas sutis em `#E2E8F0` (Slate 200).
  - Texto principal: `#0F172A` (Slate 900) para máxima legibilidade (WCAG AAA).
  - Texto secundário/metadados: `#64748B` (Slate 500).
- **Âmbar de Alerta**: `#D97706` (Amber 600) e `#F59E0B` (Amber 500).
  - Usado em: Gatilho identificado pendente de consenso, avisos de tempo limite e status intermediários.
- **Carmim Cirúrgico de Alto Risco**: `#DC2626` (Red 600) e `#EF4444` (Red 500).
  - Usado em: Dano confirmado (Categorias E a I), eventos sentinela, óbito e botões de exclusão/cancelamento definitivo.

---

## 4. Padrões de Componentes de Interface
1. **Tabelas de Dados**:
   - Exatamente **10 registros por página** (padrão universal homologado).
   - Cabeçalhos clicáveis para ordenação ascendente/descendente com indicador visual (seta).
   - Campo de busca textual com filtro por múltiplos campos e debounce de 300ms.
   - Linhas com efeito hover suave (`hover:bg-slate-50`) e seleção de linha evidente.
2. **Modais de Confirmação**:
   - Toda ação destrutiva (excluir usuário, apagar prontuário simulado) ou de transição irreversível (concluir auditoria de consenso) requer modal com:
     - Título claro em carmim ou âmbar.
     - Descrição explícita do impacto da ação.
     - Dois botões: "Cancelar" (neutro) e ação afirmativa (destrutiva/carmim ou confirmação/azul).
3. **Indicadores de Carregamento e Feedback**:
   - Spinner global sobreposto (`overlay spinner`) durante requisições assíncronas críticas para evitar cliques duplicados.
   - Skeletons em tabelas e cards durante o carregamento de dados.
   - Toast Notifications fixadas no canto superior direito para confirmação imediata de ações.
