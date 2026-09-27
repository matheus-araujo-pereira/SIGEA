import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClassHeaderInfoComponent } from '../class-header-info.component';
import { AcademicClassDetailDTO } from '../../../../models/academic-class.model';

describe('ClassHeaderInfoComponent', () => {
  let component: ClassHeaderInfoComponent;
  let fixture: ComponentFixture<ClassHeaderInfoComponent>;

  const mockClassData: AcademicClassDetailDTO = {
    id: 'class-1',
    subjectName: 'Enfermagem Médica',
    classCode: 'T01',
    academicPeriod: '2026.2',
    formattedName: 'Enfermagem Médica - T01 - 2026.2',
    professorId: 'prof-1',
    professorName: 'Dr. Professor',
    professorEmail: 'prof@academico.ufs.br',
    studentCount: 20,
    isClosed: false,
    createdAt: '2026-09-01T00:00:00Z',
    students: [],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassHeaderInfoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassHeaderInfoComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('classData', mockClassData);
    fixture.componentRef.setInput('isAdmin', true);
    fixture.componentRef.setInput('isProfessor', false);
    fixture.componentRef.setInput('activitiesCount', 5);
    fixture.detectChanges();
  });

  it('deve criar o componente e renderizar os dados da turma', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Enfermagem Médica - T01 - 2026.2');
    expect(compiled.textContent).toContain('Dr. Professor');
    expect(compiled.textContent).toContain('Ativa');
  });

  it('deve emitir eventos ao clicar nos botões de ação', () => {
    const goBackSpy = jest.spyOn(component.goBack, 'emit');
    const bulletinSpy = jest.spyOn(component.downloadBulletin, 'emit');
    const researchSpy = jest.spyOn(component.downloadResearch, 'emit');
    const dashboardSpy = jest.spyOn(component.viewDashboard, 'emit');
    const newActivitySpy = jest.spyOn(component.createNewActivity, 'emit');

    const buttons = fixture.nativeElement.querySelectorAll('button');
    // Button 0: Voltar
    buttons[0].click();
    expect(goBackSpy).toHaveBeenCalled();

    // Button 1: Boletim PDF
    buttons[1].click();
    expect(bulletinSpy).toHaveBeenCalled();

    // Button 2: CSV
    buttons[2].click();
    expect(researchSpy).toHaveBeenCalled();

    // Button 3: Painel Analítico
    buttons[3].click();
    expect(dashboardSpy).toHaveBeenCalled();

    // Button 4: Nova Atividade
    buttons[4].click();
    expect(newActivitySpy).toHaveBeenCalled();
  });

  it('deve exibir status Encerrada quando isClosed for true', () => {
    fixture.componentRef.setInput('classData', { ...mockClassData, isClosed: true });
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Encerrada');
  });
});
