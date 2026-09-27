/**
 * Métricas epidemiológicas da metodologia IHI Global Trigger Tool (GTT).
 * Todos os percentuais e taxas são calculados com autoridade pela camada de serviço do backend.
 */
export interface GttMetricsDTO {
  /** Eventos adversos por 1.000 dias-paciente */
  adverseEventsPer1000PatientDays: number;
  /** Eventos adversos por 100 admissões */
  adverseEventsPer100Admissions: number;
  /** Percentual de admissões com pelo menos um evento adverso */
  percentAdmissionsWithAdverseEvents: number;
  /** Total consolidado de dias-paciente na amostra */
  totalPatientDays: number;
  /** Total consolidado de admissões analisadas */
  totalAdmissions: number;
  /** Total absoluto de eventos adversos identificados */
  totalAdverseEvents: number;
  /** Total de internações com ocorrência de evento adverso */
  admissionsWithAdverseEvents: number;
  /** Distribuição quantitativa de danos por gravidade NCC MERP (E a I) */
  harmDistribution: { [letter: string]: number };
  /** Percentual de danos por categoria de gravidade */
  harmPercentages?: { [letter: string]: number };
}

/**
 * Frequência de ocorrência de gatilho clínico nos prontuários da turma.
 */
export interface TriggerOccurrenceDTO {
  /** Código oficial do gatilho clínico (ex: C1, M3, S2) */
  triggerCode: string;
  /** Nome ou descrição sintética do gatilho */
  triggerName: string;
  /** Quantidade absoluta de ocorrências detectadas */
  count: number;
}

/**
 * Métricas pedagógicas e acadêmicas de desempenho da turma.
 */
export interface PedagogicalMetricsDTO {
  /** Média aritmética das notas dos estudantes na turma (0 a 10) */
  classAverageGrade: number;
  /** Total de discentes regularmente matriculados */
  totalEnrolledStudents: number;
  /** Total de atividades de auditoria criadas */
  totalActivities: number;
  /** Total de prontuários submetidos pelos alunos */
  totalSubmissions: number;
  /** Quantidade de submissões já corrigidas pelo docente */
  gradedSubmissions: number;
  /** Quantidade de submissões aguardando correção */
  pendingGradingSubmissions: number;
  /** Lista dos gatilhos mais frequentemente identificados na turma */
  topIdentifiedTriggers: TriggerOccurrenceDTO[];
}

/**
 * DTO consolidado do painel analítico da turma acadêmica.
 */
export interface ClassDashboardDTO {
  /** Identificador único da turma (UUID) */
  classId: string;
  /** Nome da disciplina / turma */
  className: string;
  /** Nome do docente responsável */
  professorName: string;
  /** Período letivo no padrão YYYY.S */
  academicPeriod: string;
  /** Flag indicando se o período da turma foi encerrado */
  isClosed: boolean;
  /** Métricas epidemiológicas IHI-GTT */
  gttMetrics: GttMetricsDTO;
  /** Métricas pedagógicas e acadêmicas */
  pedagogicalMetrics: PedagogicalMetricsDTO;
}
