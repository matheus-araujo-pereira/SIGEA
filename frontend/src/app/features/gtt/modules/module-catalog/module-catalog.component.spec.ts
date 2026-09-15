import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ModuleCatalogComponent } from './module-catalog.component';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { of, throwError } from 'rxjs';

describe('ModuleCatalogComponent', () => {
  let component: ModuleCatalogComponent;
  let fixture: ComponentFixture<ModuleCatalogComponent>;
  let moduleServiceMock: { getCatalog: jest.Mock };

  const mockModules: GttModule[] = [
    {
      id: 'mod-1',
      code: 'C',
      name: 'Cuidados Gerais',
      description: 'Descrição de cuidados',
      displayOrder: 1,
      isActive: true,
      triggerCount: 10,
    createdAt: '2026-09-14T00:00:00Z',
    },
    {
      id: 'mod-2',
      code: 'M',
      name: 'Medicamentos',
      description: '',
      displayOrder: 2,
      isActive: true,
      triggerCount: 13,
    createdAt: '2026-09-14T00:00:00Z',
    },
  ];

  beforeEach(async () => {
    moduleServiceMock = {
      getCatalog: jest.fn().mockReturnValue(of({ success: true, data: mockModules })),
    };

    await TestBed.configureTestingModule({
      imports: [ModuleCatalogComponent],
      providers: [
        provideRouter([]),
        { provide: GttModuleService, useValue: moduleServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ModuleCatalogComponent);
    component = fixture.componentInstance;
  });

  it('deve carregar o catálogo de módulos com sucesso', () => {
    fixture.detectChanges();
    expect(component.modules().length).toBe(2);
    expect(component.isLoading()).toBe(false);
    expect(moduleServiceMock.getCatalog).toHaveBeenCalled();
  });

  it('deve tratar erro ao carregar catálogo', () => {
    moduleServiceMock.getCatalog.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadCatalog();
    expect(component.isLoading()).toBe(false);
  });

  it('deve retornar classes de cores adequadas para cada código de módulo', () => {
    expect(component.getModuleColor('C')).toContain('blue');
    expect(component.getModuleColor('M')).toContain('amber');
    expect(component.getModuleColor('S')).toContain('emerald');
    expect(component.getModuleColor('I')).toContain('purple');
    expect(component.getModuleColor('P')).toContain('rose');
    expect(component.getModuleColor('E')).toContain('orange');
    expect(component.getModuleColor('X')).toContain('slate');
  });
});
