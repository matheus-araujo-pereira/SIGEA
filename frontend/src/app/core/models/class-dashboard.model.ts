export interface GttMetricsDTO {
  adverseEventsPer1000PatientDays: number;
  adverseEventsPer100Admissions: number;
  percentAdmissionsWithAdverseEvents: number;
  totalPatientDays: number;
  totalAdmissions: number;
  totalAdverseEvents: number;
  admissionsWithAdverseEvents: number;
  harmDistribution: { [letter: string]: number };
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
