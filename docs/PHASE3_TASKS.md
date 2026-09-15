# Especificação da Fase 3 - Módulo Educacional e Ferramentas da Qualidade

## 1. Gestão de Turmas (Admin)
- **Tela de Listagem e Detalhes de Turmas**:
  - Dados obrigatórios: Nome da Matéria + Código da Turma + Período UFS (Exemplo: "Enfermagem Hospitalar - T01 - 2026.2").
  - Professor responsável: Exatamente 1 (obrigatório, com função de substituição sem nunca deixar a turma sem docente).
  - Alunos vinculados: Seleção múltipla com busca por matrícula ou nome.
  - Regra de Encerramento: O administrador só pode encerrar a turma quando não houver nenhuma atividade com correção pendente.

## 2. Atividades e Casos Clínicos Simulados (Professor & Alunos)
- **Criação de Atividade (Professor / Admin)**:
  - Título, Descrição, Data/Hora limite de entrega.
  - Dados do Caso Clínico Simulado: Prontuário eletrônico fictício contendo:
    - Identificação do paciente e tempo de internação.
    - Notas de admissão e evolução médica e de enfermagem.
    - Prescrição de medicamentos e notas de administração.
    - Exames laboratoriais e laudos de imagem.
    - Descrições de procedimentos cirúrgicos ou invasivos (se aplicável).
- **Tela de Resolução da Atividade (Aluno)**:
  - Ambiente de treinamento prático:
    1. Visualizador do prontuário simulado com regra dos 20 minutos (timer de apoio educacional).
    2. Identificação de Gatilhos Positivos: O aluno seleciona os gatilhos identificados no caso.
    3. Confirmação do Evento Adverso e Classificação de Gravidade (Categorias E a I).
    4. **Ferramentas da Qualidade Integradas (Preenchimento interativo)**:
       - **Ishikawa**: Diagrama interativo de 6M para o evento adverso encontrado.
       - **5W2H**: Matriz de plano de ação de prevenção.
       - **Matriz GUT**: Pontuação de Gravidade, Urgência e Tendência.
       - **PDCA**: Proposta de ciclo de melhoria contínua.
       - **Brainstorming & SWOT**: Análise contextualizada da segurança do paciente.
  - Submissão individual da atividade.
- **Tela de Correção de Atividades (Professor)**:
  - Listagem dos alunos da turma e status de envio (Enviado, Pendente, Corrigido).
  - Comparativo entre o gabarito do caso clínico e a resposta do aluno.
  - Atribuição de nota de 0.0 a 10.0 e campo para feedback pedagógico detalhado.
- **Histórico Completo do Aluno e Professor**:
  - Consulta permanente a todas as turmas concluídas ou ativas, notas, feedbacks e resoluções.

## 3. Dashboards Pedagógicos e Indicadores GTT
- **Dashboard da Turma (Professor e Administrador)**:
  - Indicadores GTT simulados da turma:
    - Eventos Adversos por 1.000 pacientes-dia.
    - Eventos Adversos por 100 admissões.
    - % de admissões com pelo menos um evento adverso.
    - Gráfico de pizza/barras com distribuição de danos por gravidade (E, F, G, H, I).
  - Indicadores de Aprendizado:
    - Média de notas da turma.
    - Gatilhos mais omitidos ou incorretamente associados pelos alunos.
    - Taxa de assertividade na identificação da causa-raiz no Diagrama de Ishikawa.