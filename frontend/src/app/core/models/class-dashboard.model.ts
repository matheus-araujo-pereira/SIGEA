/**
 * Métricas epidemiológicas da metodologia IHI Global Trigger Tool (GTT).
 * Todos os percentuais e taxas são calculados com autoridade pela camada de serviço do backend.
 */
export interface GttMetricsDTO {
  adverseEventsPer1000PatientDays: number;
  adverseEventsPer100Admissions: number;
  percentAdmissionsWithAdverseEvents: number;
  totalPatientDays: number;
  totalAdmissions: number;
  totalAdverseEvents: number;
  admissionsWithAdverseEvents: number;
  harmDistribution: { [letter: string]: number };
  harmPercentages?: { [letter: string]: number };
}

export interface TriggerOccurrenceDTO {
  triggerCode: string;
  triggerName: string;
  count: number;
}

export interface PedagogicalMetricsDTO {
  classAverageGrade: number;
  totalEnrolledStudents: number;
  totalActivities: number;
  totalSubmissions: number;
  gradedSubmissions: number;
  pendingGradingSubmissions: number;
  topIdentifiedTriggers: TriggerOccurrenceDTO[];
}

export interface ClassDashboardDTO {
  classId: string;
  className: string;
  professorName: string;
  academicPeriod: string;
  isClosed: boolean;
  gttMetrics: GttMetricsDTO;
  pedagogicalMetrics: PedagogicalMetricsDTO;
}
