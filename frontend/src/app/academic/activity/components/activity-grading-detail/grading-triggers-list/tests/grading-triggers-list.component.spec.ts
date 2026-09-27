import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GradingTriggersListComponent } from '../grading-triggers-list.component';
import { IdentifiedTriggerDTO } from '../../../../models/activity.model';

describe('GradingTriggersListComponent', () => {
  let component: GradingTriggersListComponent;
  let fixture: ComponentFixture<GradingTriggersListComponent>;

  const mockTriggers: IdentifiedTriggerDTO[] = [
    {
      triggerCode: 'M1',
      triggerName: 'Uso de Naloxona',
      harmCategory: 'E',
      rationale: 'Paciente apresentou depressão respiratória.'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GradingTriggersListComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(GradingTriggersListComponent);
    component = fixture.componentInstance;
  });

  it('deve renderizar estado vazio quando não houver gatilhos', () => {
    component.triggers = [];
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum gatilho foi identificado');
  });

  it('deve renderizar a lista de gatilhos clínicos', () => {
    component.triggers = mockTriggers;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('M1');
    expect(compiled.textContent).toContain('Uso de Naloxona');
    expect(compiled.textContent).toContain('Paciente apresentou depressão respiratória.');
  });
});
