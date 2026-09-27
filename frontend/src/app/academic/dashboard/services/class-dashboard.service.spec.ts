import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ClassDashboardService } from './class-dashboard.service';
import { ClassDashboardDTO } from '../models/class-dashboard.model';

describe('ClassDashboardService', () => {
  let service: ClassDashboardService;
  let httpMock: HttpTestingController;

  const mockDashboard: ClassDashboardDTO = {
    classId: 'class-1',
    className: 'Enfermagem - T01 - 2026.2',
    professorName: 'Prof. Waleska',
    academicPeriod: '2026.2',
    isClosed: false,
    gttMetrics: {
      adverseEventsPer1000PatientDays: 15.2,
      adverseEventsPer100Admissions: 25.0,
      percentAdmissionsWithAdverseEvents: 20.0,
      totalPatientDays: 100,
      totalAdmissions: 20,
      totalAdverseEvents: 5,
      admissionsWithAdverseEvents: 4,
      harmDistribution: { E: 3, F: 2, G: 0, H: 0, I: 0 },
    },
    pedagogicalMetrics: {
      classAverageGrade: 8.5,
      totalEnrolledStudents: 15,
      totalActivities: 3,
      totalSubmissions: 20,
      gradedSubmissions: 18,
      pendingGradingSubmissions: 2,
      topIdentifiedTriggers: [{ triggerCode: 'C1', triggerName: 'Queda', count: 8 }],
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ClassDashboardService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(ClassDashboardService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve obter painel da turma por classId', () => {
    service.getClassDashboard('class-1').subscribe((res) => {
      expect(res.data.classId).toBe('class-1');
      expect(res.data.gttMetrics.adverseEventsPer1000PatientDays).toBe(15.2);
    });

    const req = httpMock.expectOne('/api/academic/classes/class-1/dashboard');
    expect(req.request.method).toBe('GET');
    req.flush({ success: true, data: mockDashboard });
  });
});
