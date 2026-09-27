/**
 * @file academic-class.model.ts
 * @description Modelos de dados e contratos DTO para Gestão de Turmas Acadêmicas (br.ufs.sigea.academic.clazz).
 * @module AcademicClassModel
 */

import { ActivityResponseDTO } from '../../activity/models/activity.model';

/**
 * Resumo cadastral simplificado de um estudante matriculado na turma.
 */
export interface ClassStudentSummaryDTO {
  /** UUID do estudante */
  id: string;
  /** Nome civil completo */
  fullName: string;
  /** E-mail acadêmico institucional @academico.ufs.br */
  email: string;
  /** Matrícula acadêmica UFS */
  registrationNumber?: string;
  /** Papel do usuário */
  role?: string;
  /** Status do cadastro */
  active?: boolean;
  /** Flag de primeiro acesso */
  mustChangePassword?: boolean;
  /** Data de cadastro */
  createdAt?: string;
  /** Data da última alteração */
  updatedAt?: string;
}

/**
 * DTO resumido para listagem de Turmas Acadêmicas.
 */
export interface AcademicClassResponseDTO {
  /** Identificador único UUID da turma */
  id: string;
  /** Nome da disciplina (ex: 'Enfermagem Hospitalar') */
  subjectName: string;
  /** Código identificador da turma (ex: 'T01') */
  classCode: string;
  /** Período letivo no formato YYYY.S (ex: '2026.1') */
  academicPeriod: string;
  /** Nome formatado padrão institucional (ex: 'Enfermagem Hospitalar (T01 - 2026.1)') */
  formattedName: string;
  /** UUID do docente titular */
  professorId: string;
  /** Nome do docente titular */
  professorName: string;
  /** E-mail institucional do docente */
  professorEmail: string;
  /** Indica se o período letivo da turma foi encerrado */
  isClosed: boolean;
  /** Quantidade total de alunos matriculados */
  studentCount: number;
  /** Quantidade total de atividades pedagógicas cadastradas */
  activityCount: number;
  /** Data e hora ISO de cadastro */
  createdAt: string;
}

/**
 * DTO detalhado com lista completa de estudantes matriculados e atividades vinculadas.
 */
export interface AcademicClassDetailDTO {
  /** UUID da turma */
  id: string;
  /** Nome da disciplina */
  subjectName: string;
  /** Código da turma */
  classCode: string;
  /** Período letivo */
  academicPeriod: string;
  /** Nome formatado da turma */
  formattedName: string;
  /** UUID do docente */
  professorId: string;
  /** Nome do docente */
  professorName: string;
  /** E-mail do docente */
  professorEmail: string;
  /** Status de encerramento */
  isClosed: boolean;
  /** Quantidade de estudantes */
  studentCount: number;
  /** Quantidade de atividades */
  activityCount?: number;
  /** Data de criação */
  createdAt?: string;
  /** Relação de estudantes matriculados */
  students: ClassStudentSummaryDTO[];
  /** Relação de atividades pedagógicas */
  activities?: ActivityResponseDTO[];
}

/**
 * DTO para cadastro de nova turma acadêmica.
 */
export interface AcademicClassCreateDTO {
  /** Nome da disciplina */
  subjectName: string;
  /** Código da turma */
  classCode: string;
  /** Período letivo no formato YYYY.S */
  academicPeriod: string;
  /** UUID do professor titular responsável */
  professorId: string;
  /** Lista opcional de UUIDs dos estudantes a serem matriculados */
  studentIds?: string[];
}

/**
 * DTO para atualização de turma acadêmica existente.
 */
export interface AcademicClassUpdateDTO {
  /** Nome da disciplina */
  subjectName: string;
  /** Código da turma */
  classCode: string;
  /** Período letivo */
  academicPeriod: string;
  /** UUID do professor titular */
  professorId: string;
  /** Lista atualizada de UUIDs dos alunos matriculados */
  studentIds?: string[];
}

/**
 * DTO para fechamento ou reabertura de turma acadêmica.
 */
export interface AcademicClassCloseDTO {
  /** Novo status de encerramento */
  isClosed: boolean;
}

/**
 * Alias de compatibilidade DTO para resumo de estudante em turma acadêmica.
 */
export type AcademicStudentSummaryDTO = ClassStudentSummaryDTO;

