import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { TriggerGuideComponent } from './trigger-guide.component';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { of, throwError } from 'rxjs';

describe('TriggerGuideComponent', () => {
  let component: TriggerGuideComponent;
  let fixture: ComponentFixture<TriggerGuideComponent>;
  let triggerServiceMock: { getCatalog: jest.Mock };
  let moduleServiceMock: { getCatalog: jest.Mock };

  const mockModule: GttModule = {
    id: 'mod-1',
    code: 'C',
    name: 'Cuidados Gerais',
    description: '',
    displayOrder: 1,
    isActive: true,
    triggerCount: 2,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockTriggers: GttTrigger[] = [
    {
      id: 'trig-1',
      moduleId: 'mod-1',
      moduleCode: 'C',
      moduleName: 'Cuidados Gerais',
      code: 'C1',
      name: 'Transfusão de sangue',
      description: 'Investigar sangramento',
      isActive: true,
      createdAt: '2026-09-14T00:00:00Z',
    },
    {
      id: 'trig-2',
      moduleId: 'mod-2',
      moduleCode: 'M',
      moduleName: 'Medicamentos',
      code: 'M1',
      name: 'Naloxona',
      description: 'Investigar sobredose de opioides',
      isActive: true,
      createdAt: '2026-09-14T00:00:00Z',
    },
  ];

  beforeEach(async () => {
    triggerServiceMock = {
      getCatalog: jest.fn().mockReturnValue(of({ success: true, data: mockTriggers })),
    };
    moduleServiceMock = {
      getCatalog: jest.fn().mockReturnValue(of({ success: true, data: [mockModule] })),
    };

    await TestBed.configureTestingModule({
      imports: [TriggerGuideComponent],
      providers: [
        { provide: GttTriggerService, useValue: triggerServiceMock },
        { provide: GttModuleService, useValue: moduleServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ moduleId: 'mod-1' }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TriggerGuideComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar com parâmetros de rota e carregar módulos e gatilhos', () => {
    fixture.detectChanges();
    expect(component.selectedModuleId).toBe('mod-1');
    expect(component.modules().length).toBe(1);
    expect(component.triggers().length).toBe(2);
    expect(component.isExpanded('trig-1')).toBe(true);
  });

  it('deve alternar expansão de item accordion', () => {
    fixture.detectChanges();
    expect(component.isExpanded('trig-1')).toBe(true);

    component.toggleExpand('trig-1');
    expect(component.isExpanded('trig-1')).toBe(false);

    component.toggleExpand('trig-1');
    expect(component.isExpanded('trig-1')).toBe(true);
  });

  it('deve selecionar módulo e filtrar lista', () => {
    fixture.detectChanges();
    component.selectModule('mod-2');
    expect(component.selectedModuleId).toBe('mod-2');

    const filtered = component.filteredTriggers();
    expect(filtered.length).toBe(1);
    expect(filtered[0].code).toBe('M1');
  });

  it('deve filtrar gatilhos por busca textual', () => {
    fixture.detectChanges();
    component.selectModule('');
    component.searchQuery = 'Naloxona';

    const filtered = component.filteredTriggers();
    expect(filtered.length).toBe(1);
    expect(filtered[0].name).toBe('Naloxona');
  });

  it('deve tratar erro no carregamento de módulos', () => {
    moduleServiceMock.getCatalog.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadData();
    expect(component.isLoading()).toBe(false);
  });

  it('deve tratar erro no carregamento de gatilhos', () => {
    triggerServiceMock.getCatalog.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadData();
    expect(component.isLoading()).toBe(false);
  });
});
