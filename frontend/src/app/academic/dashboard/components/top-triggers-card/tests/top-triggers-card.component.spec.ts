import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TopTriggersCardComponent } from '../top-triggers-card.component';
import { TriggerOccurrenceDTO } from '../../../models/class-dashboard.model';

describe('TopTriggersCardComponent', () => {
  let component: TopTriggersCardComponent;
  let fixture: ComponentFixture<TopTriggersCardComponent>;

  const mockTriggers: TriggerOccurrenceDTO[] = [
    { triggerCode: 'C1', triggerName: 'Queda de leito', count: 6 },
    { triggerCode: 'M2', triggerName: 'Uso de Naloxona', count: 3 },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopTriggersCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TopTriggersCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('triggers', mockTriggers);
    fixture.detectChanges();
  });

  it('deve listar os gatilhos clínicos na tabela', () => {
    expect(component).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('C1');
    expect(compiled.textContent).toContain('Queda de leito');
    expect(compiled.textContent).toContain('6x');
    expect(compiled.textContent).toContain('M2');
    expect(compiled.textContent).toContain('Uso de Naloxona');
    expect(compiled.textContent).toContain('3x');
  });

  it('deve exibir mensagem apropriada quando a lista estiver vazia', () => {
    fixture.componentRef.setInput('triggers', []);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum gatilho registrado nas submissões');
  });
});
