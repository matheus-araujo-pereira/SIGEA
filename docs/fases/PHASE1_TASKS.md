# Especificação da Fase 1 - Configuração do Projeto e Gestão de Usuários

## 1. Telas a Desenvolver
1. **Tela de Login**:
   - Campos: E-mail Institucional e Senha.
   - Validação em tempo real: O campo de e-mail deve validar o sufixo `@academico.ufs.br`. Se inválido, desabilitar botão e alertar: "Apenas e-mails institucionais @academico.ufs.br são permitidos".
   - Autenticação JWT com armazenamento seguro de token.
2. **Tela de Primeiro Login (Alteração Obrigatória)**:
   - Acionada se o usuário autenticado possuir `must_change_password = true`.
   - Bloqueio de navegação para qualquer outra rota sem concluir este passo.
   - Campos: Nova Senha e Confirmação de Senha.
3. **Tela de Meu Perfil & Alteração de Senha**:
   - Exibição dos dados do usuário (Nome, E-mail, Perfil e Matrícula se aluno).
   - Aba/Modal para alteração voluntária da senha atual.
4. **Tela de Listagem de Usuários (Apenas Administrador)**:
   - Tabela com paginação padrão de 10 itens por página.
   - Colunas: Nome Completo, E-mail, Nível de Perfil (Tag visual), Matrícula, Status (Ativo/Inativo), Ações.
   - Ações: Editar, Ativar/Inativar (toggle instantâneo) e Excluir.
   - Botão para "Novo Usuário".
5. **Tela de Cadastro/Edição de Usuários (Apenas Administrador)**:
   - Formulário reativo: Nome Completo, E-mail (`@academico.ufs.br`), Nível de Perfil (`ADMIN`, `PROFESSOR`, `STUDENT`).
   - Campo "Matrícula": Renderizado condicionalmente com validação obrigatória **apenas** quando o perfil selecionado for `STUDENT`. Oculto para `ADMIN` e `PROFESSOR`.
   - Ao salvar novo usuário, gerar senha aleatória automática e marcar `must_change_password = true`.

## 2. Componentes Estruturais Globais
- **Menu Lateral Retrátil**: Navegação ergonômica que minimiza para modo compacto (somente ícones), com perfil do usuário logado na base e botão de Logout.
- **Modal de Confirmação**: Componente reutilizável para confirmação de ações irreversíveis (Exclusão e Inativação).
- **Toast Notifications**: Notificações automáticas de sucesso, erro e alerta.
- **Loading Overlay**: Indicador de carregamento visual sobre chamadas de API.
- **Resoluções**: Testes de CSS e flexibilidade para 1280x720, 1600x900, 1920x1200 e Ultrawide.