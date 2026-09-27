import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClassStudentsListComponent } from '../class-students-list.component';
import { AcademicStudentSummaryDTO } from '../../../../models/academic-class.model';

describe('ClassStudentsListComponent', () => {
  let component: ClassStudentsListComponent;
  let fixture: ComponentFixture<ClassStudentsListComponent>;

  const mockStudents: AcademicStudentSummaryDTO[] = [
    {
      id: 'st-1',
      fullName: 'Alice Silva',
      email: 'alice@academico.ufs.br',
      registrationNumber: '202600100',
    },
    {
      id: 'st-2',
      fullName: 'Bruno Santos',
      email: 'bruno@academico.ufs.br',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassStudentsListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ClassStudentsListComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('students', mockStudents);
    fixture.detectChanges();
  });

  it('deve listar os estudantes matriculados', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Alice Silva');
    expect(compiled.textContent).toContain('alice@academico.ufs.br');
    expect(compiled.textContent).toContain('202600100');
    expect(compiled.textContent).toContain('Bruno Santos');
  });

  it('deve exibir mensagem apropriada quando não houver estudantes', () => {
    fixture.componentRef.setInput('students', []);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum estudante matriculado');
  });
});
