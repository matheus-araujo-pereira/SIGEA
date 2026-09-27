/**
 * Modelos de dados e constantes padronizadas da Escala de Usabilidade do Sistema (SUS).
 * Metodologia: Brooke (1996) e Bangor, Kortum & Miller (2008).
 */

export interface SusQuestion {
  id: number;
  text: string;
  isPositive: boolean;
}

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

export const LIKERT_OPTIONS = [
  { value: 1, label: 'Discordo Totalmente', shortLabel: '1' },
  { value: 2, label: 'Discordo', shortLabel: '2' },
  { value: 3, label: 'Neutro', shortLabel: '3' },
  { value: 4, label: 'Concordo', shortLabel: '4' },
  { value: 5, label: 'Concordo Totalmente', shortLabel: '5' }
];

export interface SusEvaluationCreateDTO {
  academicClassId?: string | null;
  q1: number;
  q2: number;
  q3: number;
  q4: number;
  q5: number;
  q6: number;
  q7: number;
  q8: number;
  q9: number;
  q10: number;
  suggestions?: string;
}

export interface SusEvaluationResponseDTO {
  id: string;
  studentId: string;
  studentName: string;
  studentEmail: string;
  studentRegistration?: string;
  academicClassId?: string;
  className?: string;
  q1: number;
  q2: number;
  q3: number;
  q4: number;
  q5: number;
  q6: number;
  q7: number;
  q8: number;
  q9: number;
  q10: number;
  score: number;
  adjectiveRating: string;
  acceptability: string;
  gradeLevel: string;
  suggestions?: string;
  createdAt: string;
}

export interface SusClassSummaryDTO {
  classId: string;
  className: string;
  totalEvaluations: number;
  enrolledStudentsCount: number;
  responseRatePercentage: number;
  averageScore: number;
  adjectiveRating: string;
  acceptability: string;
  gradeLevel: string;
  adjectiveDistribution: Record<string, number>;
  questionAverages: number[];
  evaluations: SusEvaluationResponseDTO[];
}

export interface SusGeneralSummaryDTO {
  totalEvaluations: number;
  averageScore: number;
  adjectiveRating: string;
  acceptability: string;
  gradeLevel: string;
  adjectiveDistribution: Record<string, number>;
  questionAverages: number[];
}
