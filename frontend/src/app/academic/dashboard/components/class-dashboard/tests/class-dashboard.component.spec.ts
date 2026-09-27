import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClassDashboardComponent } from '../class-dashboard.component';
import { ClassDashboardService } from '../../../services/class-dashboard.service';
import { ToastService } from '../../../../../common/services/toast.service';
import { ReportService } from '../../../../report/services/report.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ClassDashboardDTO } from '../../../models/class-dashboard.model';

describe('ClassDashboardComponent', () => {
  let component: ClassDashboardComponent;
  let fixture: ComponentFixture<ClassDashboardComponent>;
  let dashboardServiceSpy: jest.Mocked<ClassDashboardService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;
  let reportServiceSpy: jest.Mocked<ReportService>;

  const mockDashboard: ClassDashboardDTO = {
    classId: 'c1',
    className: 'Física 3 - T06 - 2026.2',
    professorName: 'Prof. Gilton',
    academicPeriod: '2026.2',
    isClosed: false,
    gttMetrics: {
      adverseEventsPer1000PatientDays: 12.5,
      adverseEventsPer100Admissions: 25.0,
      percentAdmissionsWithAdverseEvents: 20.0,
      totalPatientDays: 160,
      totalAdmissions: 8,
      totalAdverseEvents: 2,
      admissionsWithAdverseEvents: 2,
      harmDistribution: {
        E: 1,
        F: 1,
        G: 0,
        H: 0,
        I: 0,
      },
      harmPercentages: {
        E: 50,
        F: 50,
        G: 0,
        H: 0,
        I: 0,
      },
    },
    pedagogicalMetrics: {
      classAverageGrade: 8.5,
      totalEnrolledStudents: 15,
      totalActivities: 2,
      totalSubmissions: 4,
      gradedSubmissions: 3,
      pendingGradingSubmissions: 1,
      topIdentifiedTriggers: [
        { triggerCode: 'M1', triggerName: 'Naloxona', count: 3 }
      ],
    },
  };

  beforeEach(async () => {
    dashboardServiceSpy = {
      getClassDashboard: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockDashboard })),
    } as unknown as jest.Mocked<ClassDashboardService>;

    toastSpy = {
      success: jest.fn(),
      error: jest.fn(),
      info: jest.fn(),
      warning: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    routerSpy = {
      navigate: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    reportServiceSpy = {
      downloadClassBulletinPdf: jest.fn().mockReturnValue(of(new Blob())),
      downloadClassResearchCsv: jest.fn().mockReturnValue(of(new Blob())),
      downloadSubmissionPdf: jest.fn().mockReturnValue(of(new Blob())),
      saveBlob: jest.fn(),
    } as unknown as jest.Mocked<ReportService>;

    await TestBed.configureTestingModule({
      imports: [ClassDashboardComponent],
      providers: [
        { provide: ClassDashboardService, useValue: dashboardServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ReportService, useValue: reportServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('c1'),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassDashboardComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar e carregar os dados analíticos da turma', () => {
    component.ngOnInit();
    expect(dashboardServiceSpy.getClassDashboard).toHaveBeenCalledWith('c1');
    expect(component.dashboard()).toEqual(mockDashboard);
    expect(component.isLoading()).toBe(false);
  });

  it('deve redirecionar caso o parâmetro classId esteja ausente', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(toastSpy.error).toHaveBeenCalledWith('Turma não identificada.');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });

  it('deve tratar erro ao carregar dashboard com mensagem do backend e fallback genérico', () => {
    dashboardServiceSpy.getClassDashboard.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao obter métricas' } })));
    component.loadDashboard();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao obter métricas');
    expect(component.isLoading()).toBe(false);

    dashboardServiceSpy.getClassDashboard.mockReturnValue(throwError(() => ({})));
    component.loadDashboard();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar indicadores da turma.');
  });

  it('deve navegar de volta para o detalhe da turma', () => {
    component.classId.set('c1');
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1']);
  });

  describe('Relatórios e Navegação SUS', () => {
    it('deve chamar downloadClassBulletinPdf quando classId estiver preenchido', () => {
      component.classId.set('c1');
      component.downloadBulletinPdf();
      expect(reportServiceSpy.downloadClassBulletinPdf).toHaveBeenCalledWith('c1');
    });

    it('não deve chamar downloadClassBulletinPdf quando classId for vazio', () => {
      component.classId.set('');
      component.downloadBulletinPdf();
      expect(reportServiceSpy.downloadClassBulletinPdf).not.toHaveBeenCalled();
    });

    it('deve chamar downloadClassResearchCsv quando classId estiver preenchido', () => {
      component.classId.set('c1');
      component.downloadResearchCsv();
      expect(reportServiceSpy.downloadClassResearchCsv).toHaveBeenCalledWith('c1');
    });

    it('não deve chamar downloadClassResearchCsv quando classId for vazio', () => {
      component.classId.set('');
      component.downloadResearchCsv();
      expect(reportServiceSpy.downloadClassResearchCsv).not.toHaveBeenCalled();
    });

    it('deve navegar para relatório SUS quando classId estiver preenchido', () => {
      component.classId.set('c1');
      component.goToSusReport();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1', 'sus']);
    });

    it('não deve navegar para relatório SUS quando classId for vazio', () => {
      component.classId.set('');
      component.goToSusReport();
      expect(routerSpy.navigate).not.toHaveBeenCalled();
    });
  });
});
