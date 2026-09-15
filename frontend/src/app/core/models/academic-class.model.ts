import { ActivityResponseDTO } from './activity.model';

export interface ClassStudentSummaryDTO {
  id: string;
  fullName: string;
  email: string;
  registrationNumber?: string;
  role?: string;
  active?: boolean;
  mustChangePassword?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface AcademicClassResponseDTO {
  id: string;
  subjectName: string;
  classCode: string;
  academicPeriod: string;
  formattedName: string;
  professorId: string;
  professorName: string;
  professorEmail: string;
  isClosed: boolean;
  studentCount: number;
  activityCount: number;
  createdAt: string;
}

export interface AcademicClassDetailDTO {
  id: string;
  subjectName: string;
  classCode: string;
  academicPeriod: string;
  formattedName: string;
  professorId: string;
  professorName: string;
  professorEmail: string;
  isClosed: boolean;
  studentCount: number;
  activityCount?: number;
  createdAt?: string;
  students: ClassStudentSummaryDTO[];
  activities?: ActivityResponseDTO[];
}

export interface AcademicClassCreateDTO {
  subjectName: string;
  classCode: string;
  academicPeriod: string;
  professorId: string;
  studentIds?: string[];
}

export interface AcademicClassUpdateDTO {
  subjectName: string;
  classCode: string;
  academicPeriod: string;
  professorId: string;
  studentIds?: string[];
}

export interface AcademicClassCloseDTO {
  isClosed: boolean;
}
