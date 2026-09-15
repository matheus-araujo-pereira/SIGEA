import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StudentActivitiesComponent } from './student-activities.component';
import { ActivityService } from '../../../../core/services/activity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SubmissionResponseDTO } from '../../../../core/models/activity.model';

describe('StudentActivitiesComponent', () => {
  let component: StudentActivitiesComponent;
  let fixture: ComponentFixture<StudentActivitiesComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockSubmissions: SubmissionResponseDTO[] = [
    {
      id: 'sub1',
      activityId: 'act1',
      activityTitle: 'Caso Clínico 1',
      studentId: 'st1',
      studentName: 'Matheus Araujo',
      studentEmail: 'matheus@academico.ufs.br',
      submissionDate: '2026-09-10T10:00:00Z',
      isGraded: true,
      grade: 9.5,
      pedagogicalFeedback: 'Excelente raciocínio clínico!',
      identifiedTriggers: [],
      qualityTools: {} as any,
    },
    {
      id: 'sub2',
      activityId: 'act2',
      activityTitle: 'Caso Clínico 2',
      studentId: 'st1',
      studentName: 'Matheus Araujo',
      studentEmail: 'matheus@academico.ufs.br',
      submissionDate: '2026-09-12T14:00:00Z',
      isGraded: false,
      grade: null,
      pedagogicalFeedback: null,
      identifiedTriggers: [],
      qualityTools: {} as any,
    },
  ];

  beforeEach(async () => {
    activityServiceSpy = {
      getMySubmissions: jest.fn().mockReturnValue(of({
        success: true,
        message: 'OK',
        data: {
          content: mockSubmissions,
          totalElements: 2,
          totalPages: 1,
          size: 10,
          page: 0,
          first: true,
          last: true,
        },
        timestamp: '2026-09-14T00:00:00Z',
      })),
    } as unknown as jest.Mocked<ActivityService>;

    toastSpy = {
      success: jest.fn(),
      error: jest.fn(),
      info: jest.fn(),
      warning: jest.fn(),
    } as unknown as jest.Mocked<ToastService>;

    routerSpy = {
      navigate: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [StudentActivitiesComponent],
      providers: [
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentActivitiesComponent);
    component = fixture.componentInstance;
  });

  it('should initialize and load submissions', () => {
    component.ngOnInit();
    expect(activityServiceSpy.getMySubmissions).toHaveBeenCalledWith(0, 10);
    expect(component.submissions().length).toBe(2);
    expect(component.totalElements()).toBe(2);
  });

  it('should handle error when loading submissions', () => {
    activityServiceSpy.getMySubmissions.mockReturnValue(throwError(() => new Error('Error')));
    component.loadSubmissions();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar atividades do estudante.');
  });

  it('should filter submissions by ALL, GRADED, and PENDING', () => {
    component.submissions.set(mockSubmissions);

    component.setFilter('ALL');
    expect(component.filteredSubmissions().length).toBe(2);

    component.setFilter('GRADED');
    expect(component.filteredSubmissions().length).toBe(1);
    expect(component.filteredSubmissions()[0].id).toBe('sub1');

    component.setFilter('PENDING');
    expect(component.filteredSubmissions().length).toBe(1);
    expect(component.filteredSubmissions()[0].id).toBe('sub2');
  });

  it('should handle page change', () => {
    activityServiceSpy.getMySubmissions.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: {
        content: mockSubmissions,
        totalElements: 2,
        totalPages: 2,
        size: 10,
        page: 1,
        first: false,
        last: true,
      },
      timestamp: '2026-09-14T00:00:00Z'
    } as any));
    component.onPageChange(1);
    expect(component.currentPage()).toBe(1);
    expect(activityServiceSpy.getMySubmissions).toHaveBeenCalledWith(1, 10);
  });

  it('should navigate to my classes', () => {
    component.goToMyClasses();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes/my-classes']);
  });

  it('should navigate to continue resolution', () => {
    component.continueResolution('act2');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/activities', 'act2', 'resolve']);
  });

  it('should navigate to view feedback', () => {
    component.viewFeedback('sub1');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/submissions', 'sub1', 'feedback']);
  });
});
