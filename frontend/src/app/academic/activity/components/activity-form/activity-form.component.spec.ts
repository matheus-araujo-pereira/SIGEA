import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ActivityFormComponent } from './activity-form.component';
import { ActivityService } from '../../services/activity.service';
import { AcademicClassService } from '../../../clazz/services/academic-class.service';
import { ToastService } from '../../../../common/services/toast.service';
import { ClinicalCaseTemplateService } from '../../../clinical/services/clinical-case-template.service';
import { ActivityDetailDTO } from '../../models/activity.model';
import { ClinicalCaseTemplateResponseDTO } from '../../../clinical/models/clinical-case-template.model';

describe('ActivityFormComponent', () => {
  let component: ActivityFormComponent;
  let fixture: ComponentFixture<ActivityFormComponent>;
  let activityServiceSpy: jest.Mocked<ActivityService>;
  let classServiceSpy: jest.Mocked<AcademicClassService>;
  let templateServiceSpy: jest.Mocked<ClinicalCaseTemplateService>;
  let toastSpy: jest.Mocked<ToastService>;
  let routerSpy: jest.Mocked<Router>;

  const mockActivityDetail: ActivityDetailDTO = {
    id: 'a1',
    title: 'Caso Clínico 1',
    description: 'Instruções do caso',
    classId: 'c1',
    className: 'Enfermagem - T01 - 2026.1',
    academicClassId: 'c1',
    academicClassName: 'Enfermagem - T01 - 2026.1',
    createdAt: '2026-09-01T00:00:00Z',
    deadline: '2026-12-31T23:59:00Z',
    isExpired: false,
    submissionCount: 0,
    clinicalCaseData: {
      patientName: 'Maria Silva',
      age: 62,
      gender: 'Feminino',
      bed: 'Leito 12 - UTI',
      admissionDate: '2026-09-01',
      patientDays: 5,
      admissionNotes: 'Admitida com choque séptico',
      evolutionNotes: [
        { dateTime: '2026-09-02 08:00', professionalRole: 'Enfermeiro', note: 'Paciente eupneico' }
      ],
      prescriptions: [
        { medication: 'Vancomicina', dosage: '1g', route: 'EV', frequency: '12/12h', administrationCheck: 'Checado' }
      ],
      labExams: [
        { examName: 'Creatinina', result: '3.2 mg/dL', referenceValue: '0.6 - 1.2 mg/dL', date: '2026-09-02' }
      ],
      procedures: [
        { procedureName: 'Cateter Central', description: 'Inserção de CVC em subclávia', date: '2026-09-01' }
      ]
    }
  };

  const mockTemplateAlternate: ClinicalCaseTemplateResponseDTO = {
    id: 'tpl-1',
    title: 'Nefrotoxicidade por Vancomicina',
    description: 'Caso simulado de toxicidade renal',
    moduleCode: 'M',
    primaryTriggerCode: 'M5',
    expectedSeverity: 'E',
    isSystemTemplate: true,
    createdBy: null as any,
    createdAt: '2026-09-01T00:00:00Z',
    updatedAt: '2026-09-01T00:00:00Z',
    clinicalCaseData: {
      patientName: 'João da Silva',
      age: 63,
      gender: 'Masculino',
      bed: 'Leito 10',
      admissionDate: '2026-03-10',
      patientDays: 6,
      admissionNotes: 'Choque séptico',
      evolutionNotes: [
        { date: '2026-03-10 10:00', role: 'Médico', content: 'Paciente sob sedação' } as any
      ],
      prescriptions: [
        { medication: 'Vancomicina 1g', dosage: '1g', checked: true } as any
      ],
      labExams: [
        { examName: 'Creatinina', result: '4.6', referenceRange: '0.7 - 1.2', date: '2026-03-14' } as any
      ],
      procedures: [
        { procedureName: 'Hemodiálise', details: 'Sessão de urgência', date: '2026-03-14' } as any
      ]
    }
  };

  const mockTemplateStandard: ClinicalCaseTemplateResponseDTO = {
    id: 'tpl-2',
    title: 'Queda do Leito com Fratura',
    description: 'Caso simulado de cuidados gerais',
    moduleCode: 'C',
    primaryTriggerCode: 'C7',
    expectedSeverity: 'F',
    isSystemTemplate: true,
    createdBy: null as any,
    createdAt: '2026-09-01T00:00:00Z',
    updatedAt: '2026-09-01T00:00:00Z',
    clinicalCaseData: {
      patientName: 'Maria de Lurdes',
      age: null as any,
      gender: '',
      bed: '',
      admissionDate: '',
      patientDays: null as any,
      admissionNotes: '',
      evolutionNotes: [
        { dateTime: '2026-03-11 14:00', professionalRole: 'Enfermeira', note: 'Queda da própria altura' }
      ],
      prescriptions: [
        { medication: 'Tramadol 50mg', dosage: '50mg', route: 'EV', frequency: '8/8h', administrationCheck: 'Checado' }
      ],
      labExams: [
        { examName: 'Raio-X de Fêmur', result: 'Fratura de colo', referenceValue: 'Sem fraturas', date: '2026-03-11' }
      ],
      procedures: [
        { procedureName: 'Osteossíntese', description: 'Fixação interna', date: '2026-03-12' }
      ]
    }
  };

  const mockTemplateEmpty: ClinicalCaseTemplateResponseDTO = {
    id: 'tpl-3',
    title: 'Caso Vazio',
    description: 'Caso para fallbacks',
    moduleCode: 'S',
    primaryTriggerCode: 'S1',
    expectedSeverity: 'G',
    isSystemTemplate: true,
    createdBy: null as any,
    createdAt: '2026-09-01T00:00:00Z',
    updatedAt: '2026-09-01T00:00:00Z',
    clinicalCaseData: {
      patientName: '',
      evolutionNotes: [
        {} as any,
      ],
      prescriptions: [
        { checked: false } as any,
      ],
      labExams: [
        {} as any,
      ],
      procedures: [
        {} as any,
      ],
    } as any
  };

  beforeEach(async () => {
    activityServiceSpy = {
      getActivityById: jest.fn().mockReturnValue(of({ success: true, message: 'OK', data: mockActivityDetail })),
      createActivity: jest.fn(),
      updateActivity: jest.fn(),
    } as unknown as jest.Mocked<ActivityService>;

    classServiceSpy = {
      getMyClasses: jest.fn().mockReturnValue(of({
        success: true,
        message: 'OK',
        data: {
          content: [
            { id: 'c1', formattedName: 'Enfermagem - T01 - 2026.1' }
          ],
          totalElements: 1,
          totalPages: 1,
          size: 50,
          page: 0
        },
        timestamp: '2026-09-14T00:00:00Z'
      } as any)),
    } as unknown as jest.Mocked<AcademicClassService>;

    templateServiceSpy = {
      listTemplates: jest.fn().mockReturnValue(of({
        success: true,
        message: 'OK',
        data: [mockTemplateAlternate, mockTemplateStandard],
        timestamp: '2026-09-14T00:00:00Z'
      })),
      getTemplateById: jest.fn(),
      createTemplate: jest.fn(),
      updateTemplate: jest.fn(),
      deleteTemplate: jest.fn(),
    } as unknown as jest.Mocked<ClinicalCaseTemplateService>;

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
      imports: [ActivityFormComponent],
      providers: [
        FormBuilder,
        { provide: ActivityService, useValue: activityServiceSpy },
        { provide: AcademicClassService, useValue: classServiceSpy },
        { provide: ClinicalCaseTemplateService, useValue: templateServiceSpy },
        { provide: ToastService, useValue: toastSpy },
        { provide: Router, useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn().mockReturnValue('a1'),
              },
              queryParamMap: {
                get: jest.fn().mockReturnValue('c1'),
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ActivityFormComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar em modo de edição e carregar a atividade', () => {
    component.ngOnInit();
    expect(classServiceSpy.getMyClasses).toHaveBeenCalled();
    expect(activityServiceSpy.getActivityById).toHaveBeenCalledWith('a1');
    expect(component.isEditMode).toBe(true);
    expect(component.form.get('title')?.value).toBe('Caso Clínico 1');
    expect(component.evolutionNotesArray.length).toBe(1);
    expect(component.prescriptionsArray.length).toBe(1);
    expect(component.labExamsArray.length).toBe(1);
    expect(component.proceduresArray.length).toBe(1);
  });

  it('deve tratar erro ao carregar atividade', () => {
    activityServiceSpy.getActivityById.mockReturnValue(throwError(() => new Error('Error')));
    component.loadActivity('a1');
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar atividade.');
  });

  it('deve adicionar e remover itens nos FormArrays dinâmicos', () => {
    // Evolution Notes
    component.addEvolutionNote('2026-09-03', 'Médico', 'Evolução');
    expect(component.evolutionNotesArray.length).toBe(1);
    component.removeEvolutionNote(0);
    expect(component.evolutionNotesArray.length).toBe(0);

    // Prescriptions
    component.addPrescription('Dipirona', '1g', 'EV', '6/6h', 'OK');
    expect(component.prescriptionsArray.length).toBe(1);
    component.removePrescription(0);
    expect(component.prescriptionsArray.length).toBe(0);

    // Lab Exams
    component.addLabExam('Hemograma', '11.2', '12-16', '2026-09-02');
    expect(component.labExamsArray.length).toBe(1);
    component.removeLabExam(0);
    expect(component.labExamsArray.length).toBe(0);

    // Procedures
    component.addProcedure('Curativo', 'Troca de curativo', '2026-09-02');
    expect(component.proceduresArray.length).toBe(1);
    component.removeProcedure(0);
    expect(component.proceduresArray.length).toBe(0);

    // Default arguments
    component.addEvolutionNote();
    expect(component.evolutionNotesArray.length).toBe(1);
    component.addPrescription();
    expect(component.prescriptionsArray.length).toBe(1);
    component.addLabExam();
    expect(component.labExamsArray.length).toBe(1);
    component.addProcedure();
    expect(component.proceduresArray.length).toBe(1);
  });

  it('deve tratar loadActivity com clinicalCaseData nulo ou com fallbacks', () => {
    // null clinicalCaseData
    activityServiceSpy.getActivityById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: { ...mockActivityDetail, clinicalCaseData: undefined as any },
      timestamp: '2026-09-14T00:00:00Z'
    } as any));
    component.loadActivity('a1');
    expect(component.clinicalCaseGroup.get('patientName')?.value).toBe('');

    // fallback gender, patientDays, and falsy patientName
    activityServiceSpy.getActivityById.mockReturnValue(of({
      success: true,
      message: 'OK',
      data: {
        ...mockActivityDetail,
        clinicalCaseData: {
          patientName: undefined,
          gender: undefined,
          patientDays: 0,
        }
      },
      timestamp: '2026-09-14T00:00:00Z'
    }));
    component.loadActivity('a1');
    expect(component.clinicalCaseGroup.get('patientName')?.value).toBe('');
    expect(component.clinicalCaseGroup.get('gender')?.value).toBe('Feminino');
    expect(component.clinicalCaseGroup.get('patientDays')?.value).toBe(1);
  });

  it('não deve submeter formulário inválido', () => {
    component.form.reset();
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Por favor, preencha todos os campos obrigatórios.');
    expect(activityServiceSpy.createActivity).not.toHaveBeenCalled();
    expect(activityServiceSpy.updateActivity).not.toHaveBeenCalled();
  });

  it('deve criar uma nova atividade em modo de criação', () => {
    component.activityId = null;
    component.form.patchValue({
      classId: 'c1',
      title: 'Caso Nova Atividade',
      description: 'Descrição completa',
      deadline: '2026-12-31T23:59',
      clinicalCase: {
        patientName: 'Paciente Teste',
        patientDays: 2,
        gender: 'Feminino',
      }
    });

    activityServiceSpy.createActivity.mockReturnValue(of({ success: true, message: 'OK', data: {} as any, timestamp: '2026-09-14T00:00:00Z' }));
    component.onSubmit();

    expect(activityServiceSpy.createActivity).toHaveBeenCalled();
    expect(toastSpy.success).toHaveBeenCalledWith('Atividade avaliativa criada com sucesso!');
    expect(routerSpy.navigate).toHaveBeenCalled();
  });

  it('deve tratar erro ao criar atividade com e sem mensagem no payload', () => {
    component.activityId = null;
    component.form.patchValue({
      classId: 'c1',
      title: 'Caso Nova Atividade',
      description: 'Descrição completa',
      deadline: '2026-12-31T23:59',
      clinicalCase: {
        patientName: 'Paciente Teste',
        patientDays: 2,
      }
    });

    activityServiceSpy.createActivity.mockReturnValue(throwError(() => ({ error: { message: 'Erro na criação' } })));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro na criação');
    expect(component.isSaving()).toBe(false);

    // Without message
    activityServiceSpy.createActivity.mockReturnValue(throwError(() => ({})));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao criar atividade.');
  });

  it('deve atualizar uma atividade em modo de edição com e sem mensagem', () => {
    component.activityId = 'a1';
    component.targetClassId = 'c1';
    component.form.patchValue({
      classId: 'c1',
      title: 'Caso Editado',
      description: 'Descrição editada',
      deadline: '2026-12-31T23:59',
      clinicalCase: {
        patientName: 'Paciente Editado',
        patientDays: 4,
      }
    });

    activityServiceSpy.updateActivity.mockReturnValue(of({ success: true, message: 'OK', data: {} as any, timestamp: '2026-09-14T00:00:00Z' }));
    component.onSubmit();

    expect(activityServiceSpy.updateActivity).toHaveBeenCalled();
    expect(toastSpy.success).toHaveBeenCalledWith('Atividade avaliativa atualizada com sucesso!');

    // Error without message
    activityServiceSpy.updateActivity.mockReturnValue(throwError(() => ({})));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao atualizar atividade.');

    // Error with message
    activityServiceSpy.updateActivity.mockReturnValue(throwError(() => ({ error: { message: 'Erro no update' } })));
    component.onSubmit();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro no update');
    expect(component.isSaving()).toBe(false);
  });

  it('deve navegar de volta para a turma se targetClassId estiver preenchido', () => {
    component.targetClassId = 'c1';
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes', 'c1']);
  });

  it('deve navegar de volta para a lista de turmas se targetClassId for nulo', () => {
    component.targetClassId = null;
    component.goBack();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/academic/classes']);
  });

  it('deve carregar templates com sucesso e tratar erro de listagem', () => {
    component.loadTemplates();
    expect(templateServiceSpy.listTemplates).toHaveBeenCalled();
    expect(component.templates().length).toBe(2);

    templateServiceSpy.listTemplates.mockReturnValue(throwError(() => new Error('Error')));
    component.loadTemplates();
    expect(toastSpy.error).toHaveBeenCalledWith('Erro ao carregar catálogo de casos clínicos.');
  });

  it('deve lidar com onTemplateSelect para template existente e inexistente', () => {
    component.templates.set([mockTemplateAlternate, mockTemplateStandard]);

    // Select existing
    component.onTemplateSelect('tpl-1');
    expect(component.selectedTemplateId()).toBe('tpl-1');
    expect(component.activeTemplate()?.id).toBe('tpl-1');

    // Select non-existing
    component.onTemplateSelect('invalid-id');
    expect(component.selectedTemplateId()).toBe('invalid-id');
    expect(component.activeTemplate()).toBeNull();
  });

  it('deve tratar applySelectedTemplate quando template ou dados forem nulos', () => {
    // null activeTemplate
    component.activeTemplate.set(null);
    component.applySelectedTemplate();
    expect(toastSpy.error).toHaveBeenCalledWith('Nenhum modelo selecionado.');

    // null clinicalCaseData
    component.activeTemplate.set({ ...mockTemplateAlternate, clinicalCaseData: null as any });
    component.applySelectedTemplate();
    expect(toastSpy.error).toHaveBeenCalledWith('Nenhum modelo selecionado.');
  });

  it('deve aplicar template com propriedades alternativas em formulário vazio', () => {
    component.form.patchValue({ title: '', description: '' });
    component.activeTemplate.set(mockTemplateAlternate);

    component.applySelectedTemplate();

    expect(component.form.get('title')?.value).toBe('Nefrotoxicidade por Vancomicina');
    expect(component.form.get('description')?.value).toBe('Caso simulado de toxicidade renal');
    expect(component.clinicalCaseGroup.get('patientName')?.value).toBe('João da Silva');
    expect(component.clinicalCaseGroup.get('age')?.value).toBe(63);
    expect(component.clinicalCaseGroup.get('gender')?.value).toBe('Masculino');
    expect(component.clinicalCaseGroup.get('patientDays')?.value).toBe(6);

    expect(component.evolutionNotesArray.length).toBe(1);
    expect(component.evolutionNotesArray.at(0).get('dateTime')?.value).toBe('2026-03-10 10:00');
    expect(component.evolutionNotesArray.at(0).get('professionalRole')?.value).toBe('Médico');
    expect(component.evolutionNotesArray.at(0).get('note')?.value).toBe('Paciente sob sedação');

    expect(component.prescriptionsArray.length).toBe(1);
    expect(component.prescriptionsArray.at(0).get('medication')?.value).toBe('Vancomicina 1g');
    expect(component.prescriptionsArray.at(0).get('administrationCheck')?.value).toBe('Checado/Administrado');

    expect(component.labExamsArray.length).toBe(1);
    expect(component.labExamsArray.at(0).get('examName')?.value).toBe('Creatinina');
    expect(component.labExamsArray.at(0).get('referenceValue')?.value).toBe('0.7 - 1.2');

    expect(component.proceduresArray.length).toBe(1);
    expect(component.proceduresArray.at(0).get('procedureName')?.value).toBe('Hemodiálise');
    expect(component.proceduresArray.at(0).get('description')?.value).toBe('Sessão de urgência');

    expect(toastSpy.success).toHaveBeenCalledWith('Modelo "Nefrotoxicidade por Vancomicina" aplicado ao prontuário com sucesso!');
  });

  it('deve aplicar template com propriedades padrão e preservar título e descrição existentes', () => {
    component.form.patchValue({
      title: 'Título Existente',
      description: 'Descrição Existente',
    });
    component.activeTemplate.set(mockTemplateStandard);

    component.applySelectedTemplate();

    // Preserved titles
    expect(component.form.get('title')?.value).toBe('Título Existente');
    expect(component.form.get('description')?.value).toBe('Descrição Existente');

    // Fallbacks applied
    expect(component.clinicalCaseGroup.get('age')?.value).toBeNull();
    expect(component.clinicalCaseGroup.get('gender')?.value).toBe('Feminino');
    expect(component.clinicalCaseGroup.get('patientDays')?.value).toBe(1);

    expect(component.evolutionNotesArray.length).toBe(1);
    expect(component.evolutionNotesArray.at(0).get('dateTime')?.value).toBe('2026-03-11 14:00');
    expect(component.evolutionNotesArray.at(0).get('professionalRole')?.value).toBe('Enfermeira');
    expect(component.evolutionNotesArray.at(0).get('note')?.value).toBe('Queda da própria altura');

    expect(component.prescriptionsArray.length).toBe(1);
    expect(component.prescriptionsArray.at(0).get('administrationCheck')?.value).toBe('Checado');

    expect(component.labExamsArray.length).toBe(1);
    expect(component.labExamsArray.at(0).get('referenceValue')?.value).toBe('Sem fraturas');

    expect(component.proceduresArray.length).toBe(1);
    expect(component.proceduresArray.at(0).get('description')?.value).toBe('Fixação interna');
  });

  it('deve tratar template com sub-arrays nulos em applySelectedTemplate', () => {
    const minimalTemplate: ClinicalCaseTemplateResponseDTO = {
      ...mockTemplateStandard,
      clinicalCaseData: {
        patientName: 'Paciente Vazio',
        evolutionNotes: null as any,
        prescriptions: null as any,
        labExams: null as any,
        procedures: null as any,
      } as any
    };

    component.activeTemplate.set(minimalTemplate);
    component.applySelectedTemplate();

    expect(component.clinicalCaseGroup.get('patientName')?.value).toBe('Paciente Vazio');
    expect(component.evolutionNotesArray.length).toBe(0);
    expect(component.prescriptionsArray.length).toBe(0);
    expect(component.labExamsArray.length).toBe(0);
    expect(component.proceduresArray.length).toBe(0);
  });

  it('deve aplicar template com campos vazios e acionar fallbacks de extração de string', () => {
    component.form.patchValue({ title: '', description: '' });
    component.activeTemplate.set(mockTemplateEmpty);

    component.applySelectedTemplate();

    expect(component.clinicalCaseGroup.get('patientName')?.value).toBe('');
    expect(component.evolutionNotesArray.length).toBe(1);
    expect(component.evolutionNotesArray.at(0).get('dateTime')?.value).toBe('');
    expect(component.evolutionNotesArray.at(0).get('professionalRole')?.value).toBe('');
    expect(component.evolutionNotesArray.at(0).get('note')?.value).toBe('');

    expect(component.prescriptionsArray.length).toBe(1);
    expect(component.prescriptionsArray.at(0).get('medication')?.value).toBe('');
    expect(component.prescriptionsArray.at(0).get('dosage')?.value).toBe('');
    expect(component.prescriptionsArray.at(0).get('route')?.value).toBe('');
    expect(component.prescriptionsArray.at(0).get('frequency')?.value).toBe('');
    expect(component.prescriptionsArray.at(0).get('administrationCheck')?.value).toBe('');

    expect(component.labExamsArray.length).toBe(1);
    expect(component.labExamsArray.at(0).get('examName')?.value).toBe('');
    expect(component.labExamsArray.at(0).get('result')?.value).toBe('');
    expect(component.labExamsArray.at(0).get('referenceValue')?.value).toBe('');
    expect(component.labExamsArray.at(0).get('date')?.value).toBe('');

    expect(component.proceduresArray.length).toBe(1);
    expect(component.proceduresArray.at(0).get('procedureName')?.value).toBe('');
    expect(component.proceduresArray.at(0).get('description')?.value).toBe('');
    expect(component.proceduresArray.at(0).get('date')?.value).toBe('');
  });
});
