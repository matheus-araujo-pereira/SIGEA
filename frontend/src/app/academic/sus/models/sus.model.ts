/**
 * @file sus.model.ts
 * @description Modelos de dados e constantes padronizadas da Escala de Usabilidade do Sistema (SUS).
 * Metodologia: Brooke (1996) e Bangor, Kortum & Miller (2008).
 * @module SusModel
 */

/**
 * Representação de uma afirmativa psicométrica da escala SUS.
 */
export interface SusQuestion {
  /** Identificador numérico da pergunta (1 a 10) */
  id: number;
  /** Enunciado da afirmativa em português */
  text: string;
  /** Indica se a questão possui polaridade positiva (ímpar) ou negativa (par) */
  isPositive: boolean;
}

/**
 * Coleção com os 10 itens canônicos validados da escala SUS em português brasileiro.
 */
export const SUS_QUESTIONS: SusQuestion[] = [
  { id: 1, text: 'Eu acho que gostaria de usar este sistema com frequência.', isPositive: true },
  { id: 2, text: 'Eu achei o sistema desnecessariamente complexo.', isPositive: false },
  { id: 3, text: 'Eu achei o sistema fácil de usar.', isPositive: true },
  { id: 4, text: 'Eu acho que precisaria de ajuda de uma pessoa com conhecimentos técnicos para usar este sistema.', isPositive: false },
  { id: 5, text: 'Eu achei que as várias funções deste sistema estavam bem integradas.', isPositive: true },
  { id: 6, text: 'Eu achei que havia muita inconsistência neste sistema.', isPositive: false },
  { id: 7, text: 'Eu imagino que a maioria das pessoas aprenderia a usar este sistema muito rapidamente.', isPositive: true },
  { id: 8, text: 'Eu achei o sistema muito incômodo ou complicado de usar.', isPositive: false },
  { id: 9, text: 'Eu me senti muito confiante usando o sistema.', isPositive: true },
  { id: 10, text: 'Eu precisei aprender muitas coisas novas antes de conseguir usar este sistema.', isPositive: false }
];

/**
 * Escala Likert padrão de 5 pontos (1 = Discordo Totalmente a 5 = Concordo Totalmente).
 */
export const LIKERT_OPTIONS = [
  { value: 1, label: 'Discordo Totalmente', shortLabel: '1' },
  { value: 2, label: 'Discordo', shortLabel: '2' },
  { value: 3, label: 'Neutro', shortLabel: '3' },
  { value: 4, label: 'Concordo', shortLabel: '4' },
  { value: 5, label: 'Concordo Totalmente', shortLabel: '5' }
];

/**
 * DTO para submissão de uma nova avaliação psicométrica SUS.
 */
export interface SusEvaluationCreateDTO {
  /** UUID opcional da turma associada à avaliação */
  academicClassId?: string | null;
  /** Resposta Likert (1-5) para a Questão 1 */
  q1: number;
  /** Resposta Likert (1-5) para a Questão 2 */
  q2: number;
  /** Resposta Likert (1-5) para a Questão 3 */
  q3: number;
  /** Resposta Likert (1-5) para a Questão 4 */
  q4: number;
  /** Resposta Likert (1-5) para a Questão 5 */
  q5: number;
  /** Resposta Likert (1-5) para a Questão 6 */
  q6: number;
  /** Resposta Likert (1-5) para a Questão 7 */
  q7: number;
  /** Resposta Likert (1-5) para a Questão 8 */
  q8: number;
  /** Resposta Likert (1-5) para a Questão 9 */
  q9: number;
  /** Resposta Likert (1-5) para a Questão 10 */
  q10: number;
  /** Comentários e sugestões qualitativas de melhoria */
  suggestions?: string;
}

/**
 * DTO de resposta detalhada para uma avaliação SUS persistida.
 */
export interface SusEvaluationResponseDTO {
  /** UUID da avaliação */
  id: string;
  /** UUID do estudante autor */
  studentId: string;
  /** Nome civil do estudante */
  studentName: string;
  /** E-mail acadêmico do estudante */
  studentEmail: string;
  /** Matrícula institucional */
  studentRegistration?: string;
  /** UUID da turma avaliada */
  academicClassId?: string;
  /** Nome formatado da turma */
  className?: string;
  /** Pontuação Likert da Questão 1 */
  q1: number;
  /** Pontuação Likert da Questão 2 */
  q2: number;
  /** Pontuação Likert da Questão 3 */
  q3: number;
  /** Pontuação Likert da Questão 4 */
  q4: number;
  /** Pontuação Likert da Questão 5 */
  q5: number;
  /** Pontuação Likert da Questão 6 */
  q6: number;
  /** Pontuação Likert da Questão 7 */
  q7: number;
  /** Pontuação Likert da Questão 8 */
  q8: number;
  /** Pontuação Likert da Questão 9 */
  q9: number;
  /** Pontuação Likert da Questão 10 */
  q10: number;
  /** Escore SUS final computado (0 a 100) */
  score: number;
  /** Classificação adjetiva (ex: 'Excelente', 'Bom') */
  adjectiveRating: string;
  /** Faixa de aceitabilidade (ex: 'Aceitável') */
  acceptability: string;
  /** Conceito escolar segundo Bangor (ex: 'A', 'B', 'C') */
  gradeLevel: string;
  /** Sugestões livres de usabilidade */
  suggestions?: string;
  /** Data e hora de envio da avaliação */
  createdAt: string;
}

/**
 * DTO com o sumário psicométrico consolidado da turma acadêmica.
 */
export interface SusClassSummaryDTO {
  /** UUID da turma avaliada */
  classId: string;
  /** Nome formatado da turma */
  className: string;
  /** Total de avaliações coletadas */
  totalEvaluations: number;
  /** Quantidade total de alunos matriculados */
  enrolledStudentsCount: number;
  /** Taxa de resposta percentual (0 a 100%) */
  responseRatePercentage: number;
  /** Média aritmética do escore SUS */
  averageScore: number;
  /** Classificação adjetiva média */
  adjectiveRating: string;
  /** Faixa de aceitabilidade geral */
  acceptability: string;
  /** Conceito escolar médio */
  gradeLevel: string;
  /** Frequência absoluta por classificação adjetiva */
  adjectiveDistribution: Record<string, number>;
  /** Média aritmética de cada uma das 10 questões (índices 0 a 9) */
  questionAverages: number[];
  /** Coleção detalhada das avaliações individuais */
  evaluations: SusEvaluationResponseDTO[];
}

/**
 * DTO com o sumário psicométrico geral institucional da plataforma.
 */
export interface SusGeneralSummaryDTO {
  /** Quantidade total de avaliações na plataforma */
  totalEvaluations: number;
  /** Média aritmética geral do escore SUS */
  averageScore: number;
  /** Classificação adjetiva global */
  adjectiveRating: string;
  /** Faixa de aceitabilidade global */
  acceptability: string;
  /** Conceito escolar global */
  gradeLevel: string;
  /** Distribuição global por classificação adjetiva */
  adjectiveDistribution: Record<string, number>;
  /** Médias globais de cada uma das 10 questões */
  questionAverages: number[];
}

/**
 * Prévia psicométrica calculada exclusivamente pela camada de serviço do backend.
 */
export interface SusEvaluationPreviewDTO {
  /** Escore SUS calculado (0 a 100) */
  score: number;
  /** Classificação adjetiva */
  adjectiveRating: string;
  /** Faixa de aceitabilidade */
  acceptability: string;
  /** Conceito escolar */
  gradeLevel: string;
}
