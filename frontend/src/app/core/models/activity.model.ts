export interface EvolutionNoteData {
  dateTime: string;
  professionalRole: string;
  note: string;
}

export interface PrescriptionData {
  medication: string;
  dosage: string;
  route: string;
  frequency: string;
  administrationCheck?: string;
}

export interface LabExamData {
  examName: string;
  result: string;
  referenceValue?: string;
  date?: string;
}

export interface ProcedureData {
  procedureName: string;
  description: string;
  date?: string;
}

export interface ClinicalCaseData {
  patientName: string;
  age?: number;
  gender?: string;
  bed?: string;
  admissionDate?: string;
  patientDays?: number;
  admissionNotes?: string;
  evolutionNotes?: EvolutionNoteData[];
  prescriptions?: PrescriptionData[];
  labExams?: LabExamData[];
  procedures?: ProcedureData[];
}

export interface IdentifiedTriggerData {
  triggerId?: string;
  triggerCode: string;
  triggerName?: string;
  moduleCode?: string;
  moduleName?: string;
  notes?: string;
  clinicalJustification?: string;
  rationale?: string;
  isHarm?: boolean;
  harmSeverityLetter?: string;
  harmCategory?: string;
  harmSeverityDescription?: string;
}

export interface IshikawaData {
  centralProblem?: string;
  problem?: string;
  method?: string;
  manpower?: string;
  material?: string;
  machine?: string;
  environment?: string;
  measurement?: string;
  methodCauses?: string[];
  manpowerCauses?: string[];
  materialCauses?: string[];
  machineCauses?: string[];
  environmentCauses?: string[];
  measurementCauses?: string[];
}

export interface GutItemData {
  problem: string;
  gravity: number; // 1 to 5
  urgency: number; // 1 to 5
  trend: number;   // 1 to 5
  tendency?: number;
  score?: number;
}

export interface FiveWTwoHItemData {
  what: string;
  why: string;
  where: string;
  when: string;
  who: string;
  how: string;
  howMuch: string;
}

export interface PdcaData {
  plan?: string;
  doPhase?: string;
  doAction?: string;
  checkPhase?: string;
  checkAction?: string;
  actPhase?: string;
  act?: string;
}

export interface SwotData {
  strengths?: string[];
  weaknesses?: string[];
  opportunities?: string[];
  threats?: string[];
}

export interface QualityToolsData {
  ishikawa?: IshikawaData;
  gutItems?: GutItemData[];
  fiveWTwoHItems?: FiveWTwoHItemData[];
  pdca?: PdcaData;
  swot?: SwotData;
  brainstormingNotes?: string[];
}

export interface SubmissionResponseDTO {
  id: string;
  activityId: string;
  activityTitle: string;
  studentId: string;
  studentName: string;
  studentEmail: string;
  studentRegistrationNumber?: string;
  identifiedTriggers: IdentifiedTriggerData[];
  qualityToolsData?: QualityToolsData;
  qualityTools?: QualityToolsData;
  submissionDate: string;
  grade?: number | null;
  professorFeedback?: string;
  pedagogicalFeedback?: string | null;
  gradedAt?: string;
  isGraded: boolean;
}

export interface SubmissionCreateDTO {
  identifiedTriggers: IdentifiedTriggerData[];
  qualityToolsData: QualityToolsData;
}

export interface SubmissionGradeDTO {
  grade: number;
  professorFeedback?: string;
  pedagogicalFeedback?: string;
}

export interface ActivityResponseDTO {
  id: string;
  classId: string;
  academicClassId?: string;
  className: string;
  academicClassName?: string;
  title: string;
  description: string;
  deadline: string;
  isExpired: boolean;
  submissionCount: number;
  createdAt?: string;
}

export interface ActivityDetailDTO {
  id: string;
  classId: string;
  academicClassId?: string;
  className: string;
  academicClassName?: string;
  title: string;
  description: string;
  clinicalCaseData: ClinicalCaseData;
  deadline: string;
  isExpired: boolean;
  submissionCount: number;
  createdAt: string;
  studentSubmission?: SubmissionResponseDTO;
}

export interface ActivityCreateDTO {
  classId: string;
  title: string;
  description: string;
  clinicalCaseData: ClinicalCaseData;
  deadline: string;
}

export interface ActivityUpdateDTO {
  title: string;
  description: string;
  clinicalCaseData: ClinicalCaseData;
  deadline: string;
}
