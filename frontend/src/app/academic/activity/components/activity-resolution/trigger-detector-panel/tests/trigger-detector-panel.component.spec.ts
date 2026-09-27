import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { TriggerDetectorPanelComponent } from '../trigger-detector-panel.component';
import { GttTrigger } from '../../../../../../gtt/trigger/models/gtt-trigger.model';
import { HarmSeverity } from '../../../../../../gtt/severity/models/harm-severity.model';

describe('TriggerDetectorPanelComponent', () => {
  let component: TriggerDetectorPanelComponent;
  let fixture: ComponentFixture<TriggerDetectorPanelComponent>;

  const mockTriggers: GttTrigger[] = [
    { id: 't1', code: 'M1', name: 'Naloxona', description: 'Uso de naloxona', moduleCode: 'M', moduleId: 'mod-m', moduleName: 'Medicamentos', isActive: true, createdAt: '2026-09-01T00:00:00Z' }
  ];

  const mockSeverities: HarmSeverity[] = [
    { id: 's1', categoryLetter: 'E', name: 'Categoria E', description: 'Dano temporário', isHarm: true, isActive: true }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TriggerDetectorPanelComponent, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(TriggerDetectorPanelComponent);
    component = fixture.componentInstance;
    component.availableTriggers = mockTriggers;
    component.harmSeverities = mockSeverities;
    component.identifiedTriggers = [];
    fixture.detectChanges();
  });

  it('deve emitir addTrigger com os dados corretos ao adicionar gatilho com dano', () => {
    const addSpy = jest.spyOn(component.addTrigger, 'emit');
    component.selectedTriggerCode = 'M1';
    component.isHarmSelected = true;
    component.selectedHarmSeverityLetter = 'E';
    component.triggerNotes = 'Sedação excessiva por opioide';

    component.onAddTrigger();

    expect(addSpy).toHaveBeenCalledWith({
      triggerId: 't1',
      triggerCode: 'M1',
      triggerName: 'Naloxona',
      moduleCode: 'M',
      notes: 'Sedação excessiva por opioide',
      isHarm: true,
      harmSeverityLetter: 'E'
    });
    expect(component.selectedTriggerCode).toBe('');
    expect(component.triggerNotes).toBe('');
    expect(component.isHarmSelected).toBe(false);
  });

  it('deve emitir addTrigger sem categoria de dano quando isHarmSelected for falso', () => {
    const addSpy = jest.spyOn(component.addTrigger, 'emit');
    component.selectedTriggerCode = 'M1';
    component.isHarmSelected = false;
    component.triggerNotes = 'Gatilho sem dano associado';

    component.onAddTrigger();

    expect(addSpy).toHaveBeenCalledWith({
      triggerId: 't1',
      triggerCode: 'M1',
      triggerName: 'Naloxona',
      moduleCode: 'M',
      notes: 'Gatilho sem dano associado',
      isHarm: false,
      harmSeverityLetter: undefined
    });
  });

  it('não deve emitir addTrigger se selectedTriggerCode estiver vazio', () => {
    const addSpy = jest.spyOn(component.addTrigger, 'emit');
    component.selectedTriggerCode = '';
    component.onAddTrigger();
    expect(addSpy).not.toHaveBeenCalled();
  });

  it('deve emitir removeTrigger com o índice correto', () => {
    const removeSpy = jest.spyOn(component.removeTrigger, 'emit');
    component.identifiedTriggers = [
      { triggerCode: 'M1', triggerName: 'Naloxona', isHarm: false }
    ];
    fixture.detectChanges();

    const removeBtn = fixture.nativeElement.querySelector('button[title="Remover gatilho"]') as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(0);
  });

  it('deve exibir mensagem de estado vazio quando não houver gatilhos adicionados', () => {
    component.identifiedTriggers = [];
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Nenhum gatilho adicionado ainda.');
  });
});
