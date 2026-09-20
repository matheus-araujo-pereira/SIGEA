# Especificação da Fase 2 - Metodologia Global Trigger Tool (IHI-GTT)

## 1. Telas a Desenvolver (CRUD Admin + Visualização para Todos)
1. **Módulos do GTT**:
   - `Listagem de Módulos` (Admin): 10 itens por página, busca, ativação/inativação e exclusão com modal de confirmação.
   - `Cadastro/Edição de Módulo` (Admin): Código, Nome, Descrição.
   - `Visualização Geral de Módulos` (Todos os perfis): Tela estilo catálogo de referência rápida sobre a estrutura do IHI-GTT no HU/UFS.
2. **Gatilhos do GTT (Triggers)**:
   - `Listagem de Gatilhos` (Admin): Filtragem por Módulo, busca textual por código/termo, paginação de 10 itens, toggle de status e exclusão.
   - `Cadastro/Edição de Gatilhos` (Admin): Módulo vinculado (Select obrigatório), Código (ex: C1, M4, S10), Nome e Descrição detalhada.
   - `Visualização Geral de Gatilhos` (Todos os perfis): Tela educacional de consulta com expansores/accordions por módulo, exibindo as orientações clínicas de investigação para cada gatilho.
3. **Classificações de Gravidade do Dano**:
   - `Listagem de Gravidades` (Admin): Exibição de todas as categorias de A a I com indicação visual de dano físico (Categorias E a I com badge "Causa Dano Real").
   - `Cadastro/Edição de Gravidade` (Admin): Letra da categoria, Nome, Descrição e flag de Dano.
   - `Visualização Geral de Gravidades` (Todos os perfis): Guia interativo do índice NCC MERP adaptado para treinamento dos acadêmicos.