import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClassActivitiesListComponent } from '../class-activities-list.component';
import { ActivityResponseDTO } from '../../../../../activity/models/activity.model';

describe('ClassActivitiesListComponent', () => {
  let component: ClassActivitiesListComponent;
  let fixture: ComponentFixture<ClassActivitiesListComponent>;

  const mockActivities: ActivityResponseDTO[] = [
    {
      id: 'act-1',
      classId: 'class-1',
      className: 'Turma A',
      title: 'Auditoria Clínica 1',
      description: 'Caso do paciente Roberto',
      deadline: '2026-10-01T23:59:59Z',
      isExpired: false,
      submissionCount: 5,
      createdAt: '2026-09-01T00:00:00Z',
    },
    {
      id: 'act-2',
      classId: 'class-1',
      className: 'Turma A',
      title: 'Auditoria Clínica 2',
      description: 'Caso encerrado',
      deadline: '2026-09-10T23:59:59Z',
      isExpired: true,
      submissionCount: 12,
      createdAt: '2026-09-01T00:00:00Z',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassActivitiesListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassActivitiesListComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('activities', mockActivities);
    fixture.componentRef.setInput('isAdmin', true);
    fixture.componentRef.setInput('isProfessor', false);
    fixture.componentRef.setInput('isStudent', false);
    fixture.detectChanges();
  });

  it('deve listar as atividades com status de prazo correto', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Auditoria Clínica 1');
    expect(compiled.textContent).toContain('Prazo Aberto');
    expect(compiled.textContent).toContain('Auditoria Clínica 2');
    expect(compiled.textContent).toContain('Expirada');
  });

  it('deve emitir eventos de docente/admin (correções, editar, excluir)', () => {
    const viewSpy = jest.spyOn(component.viewSubmissions, 'emit');
    const editSpy = jest.spyOn(component.editActivity, 'emit');
    const deleteSpy = jest.spyOn(component.deleteActivity, 'emit');

    const firstCard = fixture.nativeElement.querySelectorAll('.bg-white')[0];
    const buttons = firstCard.querySelectorAll('button');

    // Botão Correções
    buttons[0].click();
    expect(viewSpy).toHaveBeenCalledWith('act-1');

    // Botão Editar
    buttons[1].click();
    expect(editSpy).toHaveBeenCalledWith('act-1');

    // Botão Excluir
    buttons[2].click();
    expect(deleteSpy).toHaveBeenCalledWith(mockActivities[0]);
  });

  it('deve exibir botão de resolver atividade para estudantes', () => {
    fixture.componentRef.setInput('isAdmin', false);
    fixture.componentRef.setInput('isProfessor', false);
    fixture.componentRef.setInput('isStudent', true);
    fixture.detectChanges();

    const resolveSpy = jest.spyOn(component.resolveActivity, 'emit');
    const studentBtn = fixture.nativeElement.querySelector('button');
    studentBtn.click();
    expect(resolveSpy).toHaveBeenCalledWith('act-1');
  });

  it('deve exibir mensagem de vazio quando não houver atividades', () => {
    fixture.componentRef.setInput('activities', []);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhuma atividade avaliativa cadastrada');
  });
});
