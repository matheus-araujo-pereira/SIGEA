import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { TriggerDetectorPanelComponent } from '../trigger-detector-panel.component';
import { GttTrigger } from '../../../../../../gtt/trigger/models/gtt-trigger.model';
import { HarmSeverity } from '../../../../../../gtt/severity/models/harm-severity.model';

describe('TriggerDetectorPanelComponent', () => {
  let component: TriggerDetectorPanelComponent;
  let fixture: ComponentFixture<TriggerDetectorPanelComponent>;

  const mockTriggers: GttTrigger[] = [
    {
      id: 't1',
      code: 'M1',
      name: 'Naloxona',
      description: 'Uso de naloxona',
      moduleCode: 'M',
      moduleId: 'mod-m',
      moduleName: 'Medicamentos',
      isActive: true,
      createdAt: '2026-09-01T00:00:00Z'
    },
    {
      id: 't2',
      code: 'M2',
      name: 'Flumazenil',
      description: 'Uso de flumazenil',
      moduleCode: 'M',
      moduleId: 'mod-m',
      moduleName: 'Medicamentos',
      isActive: true,
      createdAt: '2026-09-01T00:00:00Z'
    },
    {
      id: 't3',
      code: 'C1',
      name: 'Transfusão',
      description: 'Transfusão sanguínea',
      moduleCode: 'C',
      moduleId: 'mod-c',
      isActive: true,
      createdAt: '2026-09-01T00:00:00Z'
    }
  ];

  const mockSeverities: HarmSeverity[] = [
    {
      id: 's1',
      categoryLetter: 'E',
      name: 'Categoria E',
      description: 'Dano temporário',
      isHarm: true,
      isActive: true
    }
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
      clinicalJustification: 'Sedação excessiva por opioide',
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
      clinicalJustification: 'Gatilho sem dano associado',
      isHarm: false,
      harmSeverityLetter: undefined
    });
  });

  it('não deve emitir addTrigger se selectedTriggerCode estiver vazio ou modo somente leitura estiver ativo', () => {
    const addSpy = jest.spyOn(component.addTrigger, 'emit');
    component.selectedTriggerCode = '';
    component.onAddTrigger();
    expect(addSpy).not.toHaveBeenCalled();

    component.readOnly = true;
    component.selectedTriggerCode = 'M1';
    component.triggerNotes = 'Evidência válida';
    component.onAddTrigger();
    expect(addSpy).not.toHaveBeenCalled();
  });

  it('deve bloquear adição e exibir erro quando evidência obrigatória estiver vazia ou apenas espaços', () => {
    const addSpy = jest.spyOn(component.addTrigger, 'emit');
    component.selectedTriggerCode = 'M1';
    component.triggerNotes = '   ';

    component.onAddTrigger();

    expect(addSpy).not.toHaveBeenCalled();
    expect(component.showEvidenceError).toBe(true);

    const textarea = document.createElement('textarea');
    component.triggerNotes = 'Prescrição de naloxona às 14h';
    component.onEvidenceInput({ target: textarea } as unknown as Event);
    expect(component.showEvidenceError).toBe(false);

    component.triggerNotes = '   ';
    component.onFieldChange();
    expect(component.triggerNotes).toBe('   ');
  });

  it('deve listar módulos disponíveis com contagem e filtrar gatilhos por módulo GTT', () => {
    const modules = component.getAvailableModules();
    expect(modules).toEqual([
      { code: 'M', name: 'Medicamentos', count: 2 },
      { code: 'C', name: 'C', count: 1 }
    ]);

    expect(component.getFilteredTriggers().length).toBe(3);

    component.selectedTriggerCode = 'M1';
    component.onModuleFilterChange('M');
    expect(component.selectedTriggerCode).toBe('M1');
    expect(component.getFilteredTriggers().length).toBe(2);

    component.onModuleFilterChange('C');
    expect(component.selectedTriggerCode).toBe('');
    expect(component.getFilteredTriggers().length).toBe(1);

    component.onModuleFilterChange('');
    expect(component.selectedTriggerCode).toBe('');
  });

  it('deve permitir iniciar edição, atualizar gatilho em rascunho e cancelar edição', () => {
    const updateSpy = jest.spyOn(component.updateTrigger, 'emit');
    component.identifiedTriggers = [
      {
        triggerId: 't1',
        triggerCode: 'M1',
        triggerName: 'Naloxona',
        clinicalJustification: 'Dose revertida no leito',
        isHarm: true
      }
    ];
    fixture.detectChanges();

    component.startEditTrigger(99);
    expect(component.editingIndex).toBeNull();

    component.startEditTrigger(0);
    expect(component.editingIndex).toBe(0);
    expect(component.selectedModuleCode).toBe('');
    expect(component.selectedTriggerCode).toBe('M1');
    expect(component.selectedHarmSeverityLetter).toBe('E');
    expect(component.triggerNotes).toBe('Dose revertida no leito');

    component.triggerNotes = 'Evidência atualizada após revisão do prontuário';
    component.onAddTrigger();
    expect(updateSpy).toHaveBeenCalledWith({
      index: 0,
      trigger: expect.objectContaining({
        triggerCode: 'M1',
        clinicalJustification: 'Evidência atualizada após revisão do prontuário'
      })
    });
    expect(component.editingIndex).toBeNull();

    component.startEditTrigger(0);
    expect(component.editingIndex).toBe(0);
    component.cancelEditTrigger();
    expect(component.editingIndex).toBeNull();
    expect(component.selectedTriggerCode).toBe('');
  });

  it('deve bloquear startEditTrigger quando readOnly for verdadeiro e reidratar formState no ngOnChanges', () => {
    component.identifiedTriggers = [{ triggerCode: 'M1', triggerName: 'Naloxona', isHarm: false }];
    component.readOnly = true;
    component.startEditTrigger(0);
    expect(component.editingIndex).toBeNull();

    const newState = {
      selectedModuleCode: 'C',
      selectedTriggerCode: 'C1',
      isHarmSelected: true,
      selectedHarmSeverityLetter: 'F',
      triggerNotes: 'Transfusão de urgência',
      editingIndex: 1
    };
    component.formState = newState;
    component.ngOnChanges({
      formState: new SimpleChange(undefined, newState, true)
    });
    expect(component.selectedModuleCode).toBe('C');
    expect(component.selectedTriggerCode).toBe('C1');
    expect(component.triggerNotes).toBe('Transfusão de urgência');
    expect(component.editingIndex).toBe(1);

    component.ngOnChanges({});
  });

  it('deve formatar evidência com compatibilidade para registros legados sem justificativa', () => {
    expect(
      component.getDisplayEvidence({ triggerCode: 'M1', triggerName: 'Naloxona' })
    ).toBe('Evidência não informada (registro anterior)');
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
