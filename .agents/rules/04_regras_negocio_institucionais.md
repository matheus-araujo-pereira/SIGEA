# Regra 04: Regras de Negócio Institucionais e Segurança

## 1. Domínio Institucional Obrigatório
- Todo usuário registrado no sistema deve possuir e-mail com sufixo estrito:
  `@academico.ufs.br`
- Qualquer outro domínio (mesmo que `@ufs.br` geral ou provedores públicos) deve ser sumariamente bloqueado na camada de validação do formulário Angular e no backend através de `@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@academico\\.ufs\\.br$")`.

---

## 2. Perfis de Usuário e Papéis (RBAC)
O sistema opera com apenas 3 perfis:
1. **`ADMIN`**:
   - Gestão completa de usuários (criação, inativação, redefinição de senhas).
   - Configuração de parâmetros do sistema e auditoria geral de logs.
   - Não possui campo matrícula (deve ser sempre nulo no banco).
2. **`PROFESSOR`**:
   - Criação e gerenciamento de turmas/prontuários simulados.
   - Execução da Revisão de Consenso (segunda etapa da auditoria GTT).
   - Extração de relatórios estatísticos e epidemiológicos.
   - Não possui campo matrícula (deve ser sempre nulo no banco).
3. **`STUDENT`**:
   - Acesso restrito a prontuários atribuídos pelo professor.
   - Execução da Revisão Primária de prontuários (identificação de gatilhos).
   - **Campo Matrícula**: Obrigatório no cadastro e na edição, validado numericamente.

---

## 3. Gestão de Credenciais e Primeiro Acesso
1. **Criação de Usuário**:
   - A senha provisória é gerada aleatoriamente com alta entropia pelo backend e enviada por e-mail ou informada pelo administrador.
   - A flag booleana `must_change_password` é definida como `true`.
2. **Primeiro Acesso**:
   - No login bem-sucedido com `must_change_password == true`, a resposta do endpoint de autenticação sinaliza a exigência.
   - O frontend redireciona imediatamente para `/auth/change-password` e bloqueia a barra de navegação principal.
   - Após a definição da nova senha forte, `must_change_password` passa para `false` e o acesso ao painel principal é liberado.
3. **Alteração Voluntária de Senha**:
   - Disponível em qualquer momento na tela de perfil do usuário. Requer validação da senha atual antes de gravar a nova.

---

## 4. Conformidade Ética com Pesquisa (CEP/UFS)
- Parecer do Comitê de Ética em Pesquisa da Universidade Federal de Sergipe: **CAAE nº 91836925.8.0000.5546**.
- **Regra de Sigilo Absoluto**: Todos os dados cadastrados no banco de dados para testes, demonstrações ou avaliações de estudantes são prontuários **estritamente simulados e fictícios**.
- É terminantemente proibido o upload ou inserção de identificadores reais de pacientes, médicos ou números de prontuários verídicos de qualquer instituição de saúde.
