import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClassDetailComponent } from '../class-detail.component';
import { AcademicClassService } from '../../../services/academic-class.service';
import { ActivityService } from '../../../../activity/services/activity.service';
import { ToastService } from '../../../../../common/services/toast.service';
import { AuthService } from '../../../../../auth/services/auth.service';
import { ReportService } from '../../../../report/services/report.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AcademicClassDetailDTO } from '../../../models/academic-class.model';
import { ActivityResponseDTO } from '../../../../activity/models/activity.model';

describe('ClassDetailComponent', () => {
  let component: ClassDetailComponent;
  let fixture: ComponentFixture<ClassDetailComponent>;
  let classServiceSpy: jest.Mocked<AcademicClassService>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let toastSpy: jest.Mocked<ToastService>;
  let authSpy: jest.Mocked<AuthService>;
  let routerSpy: jest.Mocked<Router>;
  let reportServiceSpy: jest.Mocked<ReportService>;

  const mockClassData: AcademicClassDetailDTO = {
    id: 'c1',
    subjectName: 'Física 3',
    classCode: 'T06',
    academicPeriod: '2026.2',
    formattedName: 'Física 3 - T06 - 2026.2',
    professorId: 'p1',
    professorName: 'Prof. Gilton',
    professorEmail: 'gilton@academico.ufs.br',
    isClosed: false,
    studentCount: 1,
    students: [
      { id: 's1', fullName: 'Matheus Araujo', email: 'matheus@academico.ufs.br', role: 'STUDENT', registrationNumber: '2026001', active: true, mustChangePassword: false, createdAt: '2026-01-01', updatedAt: '2026-01-01' }
    ],
    activities: [],
    activityCount: 0,
    createdAt: '2026-09-01T00:00:00Z',
  };

  const mockActivities: ActivityResponseDTO[] = [
    {
      id: 'a1',
      classId: 'c1',
      className: 'Física 3 - T06 - 2026.2',
      title: 'Caso Clínico 1 - UTI',
      description: 'Estudo de choque séptico',
      deadline: '2026-12-31T23:59:00',
      isExpired: false,
      submissionCount: 2,
      createdAt: '2026-09-01T00:00:00Z',
    },
    {
      id: 'a2',
      classId: 'c1',
      className: 'Física 3 - T06 - 2026.2',
      title: 'Caso Clínico 2 - Farmaco',
      description: 'Evento adverso medicamentoso',
      deadline: '2026-01-01T23:59:00',
      isExpired: true,
      submissionCount: 10,
      createdAt: '2026-09-01T00:00:00Z',
    }
  ];

  beforeEach(async () => {
    classServiceSpy = {
      getClassById: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockClassData })),
    } as unknown as jest.Mocked<AcademicClassService>;

    activityServiceSpy = {
      listActivities: jest.fn().mockReturnValue(of({
        success: true,
        message: 'OK',
        data: { content: mockActivities, totalElements: 2, totalPages: 1, size: 50, number: 0 },
        timestamp: '2026-09-14T00:00:00Z'
      } as any)),
      deleteActivity: jest.fn(),
    } as unknown as jest.Mocked<ActivityService>;

    toastSpy = {
      success: jest.fn(),
      error: jest.fn(),
      info: jest.fn(),
      warning: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    authSpy = {
      hasRole: jest.fn().mockImplementation((roles: string[]) => roles.includes('ADMIN')),
    } as unknown as jest.Mocked<AuthService>;

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
      imports: [ClassDetailComponent],
      providers: [
        { provide: AcademicClassService, useValue: classServiceSpy },
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: AuthService, useValue: authSpy },
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

    fixture = TestBed.createComponent(ClassDetailComponent);
    component = fixture.componentInstance;
  });

  it('should initialize and load class details and activities', () => {
    component.ngOnInit();
    expect(classServiceSpy.getClassById).toHaveBeenCalledWith('c1');
    expect(activityServiceSpy.listActivities).toHaveBeenCalledWith('c1', 0, 50);
    expect(component.classData()).toEqual(mockClassData);
    expect(component.activities().length).toBe(2);
  });

  it('should not load if id param is not provided', () => {
    const route = TestBed.inject(ActivatedRoute);
    jest.spyOn(route.snapshot.paramMap, 'get').mockReturnValue(null);

    component.ngOnInit();
    expect(component.classId).toBeNull();
  });

  it('should handle error when loading class details', () => {
    classServiceSpy.getClassById.mockReturnValue(throwError(() => new Error('Error')));
    component.loadClassDetail('c1');
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar detalhes da turma.');
  });

  it('should handle error when loading activities', () => {
    activityServiceSpy.listActivities.mockReturnValue(throwError(() => new Error('Error')));
    component.loadActivities('c1');
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar atividades da turma.');
  });

  it('should check roles correctly', () => {
    expect(component.isAdmin).toBe(true);

    authSpy.hasRole.mockImplementation((roles: string[]) => roles.includes('PROFESSOR'));
    expect(component.isProfessor).toBe(true);

    authSpy.hasRole.mockImplementation((roles: string[]) => roles.includes('STUDENT'));
    expect(component.isStudent).toBe(true);
  });

  it('should navigate back to classes', () => {
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });

  it('should navigate to dashboard', () => {
    component.classId = 'c1';
    component.viewDashboard();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1', 'dashboard']);

    component.classId = null;
    component.viewDashboard();
    // should not call navigate when classId is null
  });

  it('should navigate to create activity', () => {
    component.classId = 'c1';
    component.createNewActivity();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/activities/new'], { queryParams: { classId: 'c1' } });

    component.classId = null;
    component.createNewActivity();
    // should not call navigate when classId is null
  });

  it('should navigate to resolve activity', () => {
    component.resolveActivity('a1');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/activities', 'a1', 'resolve']);
  });

  it('should navigate to submissions for grading', () => {
    component.viewSubmissions('a1');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/activities', 'a1', 'grading']);
  });

  it('should navigate to edit activity', () => {
    component.editActivity('a1');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/activities', 'a1', 'edit']);
  });

  it('should confirm delete activity and execute successfully', () => {
    component.classId = 'c1';
    component.confirmDeleteActivity(mockActivities[0]);
    expect(component.isConfirmDialogOpen()).toBe(true);
    expect(component.confirmDialogTitle()).toBe('Excluir Atividade');

    activityServiceSpy.deleteActivity.mockReturnValue(of({ success: true, message: 'OK', data: undefined, timestamp: '2026-09-14T00:00:00Z' } as any));
    component.executeDeleteActivity();

    expect(activityServiceSpy.deleteActivity).toHaveBeenCalledWith('a1');
    expect(toastSpy.success).toHaveBeenCalledWith('Atividade excluída com sucesso!');
    expect(activityServiceSpy.listActivities).toHaveBeenCalledWith('c1', 0, 50);
  });

  it('should handle error when delete activity fails with and without error message', () => {
    component.confirmDeleteActivity(mockActivities[0]);
    activityServiceSpy.deleteActivity.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao excluir' } })));
    component.executeDeleteActivity();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao excluir');

    component.confirmDeleteActivity(mockActivities[0]);
    activityServiceSpy.deleteActivity.mockReturnValue(throwError(() => ({})));
    component.executeDeleteActivity();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao excluir atividade.');
  });

  it('should execute delete activity when classId is null', () => {
    component.classId = null;
    component.confirmDeleteActivity(mockActivities[0]);
    activityServiceSpy.deleteActivity.mockReturnValue(of({ success: true, message: 'OK', data: undefined, timestamp: '2026-09-14T00:00:00Z' } as any));
    component.executeDeleteActivity();
    expect(toastSpy.success).toHaveBeenCalledWith('Atividade excluída com sucesso!');
  });

  it('should close confirm dialog and reset activityToDeleteId', () => {
    component.confirmDeleteActivity(mockActivities[0]);
    component.closeConfirmDialog();
    expect(component.isConfirmDialogOpen()).toBe(false);

    component.executeDeleteActivity();
    expect(activityServiceSpy.deleteActivity).not.toHaveBeenCalled();
  });

  describe('Relatórios e Exportação', () => {
    it('deve chamar downloadClassBulletinPdf quando classData estiver disponível', () => {
      component.classData.set(mockClassData);
      component.downloadBulletinPdf();
      expect(reportServiceSpy.downloadClassBulletinPdf).toHaveBeenCalledWith(mockClassData.id);
    });

    it('não deve chamar downloadClassBulletinPdf quando classData for nulo', () => {
      component.classData.set(null);
      component.downloadBulletinPdf();
      expect(reportServiceSpy.downloadClassBulletinPdf).not.toHaveBeenCalled();
    });

    it('deve chamar downloadClassResearchCsv quando classData estiver disponível', () => {
      component.classData.set(mockClassData);
      component.downloadResearchCsv();
      expect(reportServiceSpy.downloadClassResearchCsv).toHaveBeenCalledWith(mockClassData.id);
    });

    it('não deve chamar downloadClassResearchCsv quando classData for nulo', () => {
      component.classData.set(null);
      component.downloadResearchCsv();
      expect(reportServiceSpy.downloadClassResearchCsv).not.toHaveBeenCalled();
    });
  });
});
