import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ResolutionHeaderComponent } from '../resolution-header.component';
import { ActivityDetailDTO } from '../../../../models/activity.model';

describe('ResolutionHeaderComponent', () => {
  let component: ResolutionHeaderComponent;
  let fixture: ComponentFixture<ResolutionHeaderComponent>;

  const mockActivity: ActivityDetailDTO = {
    id: 'act-1',
    title: 'Caso Clínico IHI',
    description: 'Instruções',
    classId: 'c1',
    className: 'Turma Enfermagem',
    academicClassId: 'c1',
    academicClassName: 'Turma Enfermagem',
    createdAt: '2026-09-27T00:00:00Z',
    deadline: '2026-09-30T23:59:00Z',
    isExpired: false,
    submissionCount: 0,
    clinicalCaseData: {} as any
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResolutionHeaderComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ResolutionHeaderComponent);
    component = fixture.componentInstance;
    component.activity = mockActivity;
    component.formattedTime = '18:45';
    fixture.detectChanges();
  });

  it('deve exibir título, tempo formatado e dados da atividade', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Caso Clínico IHI');
    expect(compiled.textContent).toContain('18:45');
    expect(compiled.textContent).toContain('Turma Enfermagem');
  });

  it('deve emitir toggleTimer ao clicar no botão de pausar/retomar', () => {
    const toggleSpy = jest.spyOn(component.toggleTimer, 'emit');
    const toggleBtn = fixture.nativeElement.querySelector('button[title="Pausar cronômetro"]') as HTMLButtonElement;
    toggleBtn.click();
    expect(toggleSpy).toHaveBeenCalled();
  });

  it('deve emitir submit ao clicar em Submeter Resposta', () => {
    const submitSpy = jest.spyOn(component.submit, 'emit');
    const submitBtn = fixture.nativeElement.querySelector('button.bg-clinical-600') as HTMLButtonElement;
    submitBtn.click();
    expect(submitSpy).toHaveBeenCalled();
  });

  it('deve emitir goBack ao clicar no botão Voltar', () => {
    const backSpy = jest.spyOn(component.goBack, 'emit');
    const backBtn = fixture.nativeElement.querySelectorAll('button')[0] as HTMLButtonElement;
    backBtn.click();
    expect(backSpy).toHaveBeenCalled();
  });

  it('deve exibir indicadores de salvamento automático e selo de somente leitura', () => {
    component.autoSaveStatus = 'SAVING';
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Salvando rascunho');

    component.autoSaveStatus = 'SAVED';
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Salvo automaticamente');

    component.autoSaveStatus = 'ERROR';
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Rascunho salvo localmente');

    component.isReadOnly = true;
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Submissão Enviada (Somente Leitura)');
    expect(fixture.nativeElement.querySelector('button.bg-clinical-600')).toBeNull();
  });
});
