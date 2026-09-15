import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TriggerListComponent } from './trigger-list.component';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { PageResponse } from '../../../../core/models/page.model';
import { of, throwError } from 'rxjs';

describe('TriggerListComponent', () => {
  let component: TriggerListComponent;
  let fixture: ComponentFixture<TriggerListComponent>;
  let triggerServiceMock: {
    listTriggers: jest.Mock;
    updateStatus: jest.Mock;
    deleteTrigger: jest.Mock;
  };
  let moduleServiceMock: {
    getCatalog: jest.Mock;
  };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const mockTrigger: GttTrigger = {
    id: 'trig-1',
    moduleId: 'mod-1',
    moduleCode: 'C',
    moduleName: 'Cuidados Gerais',
    code: 'C1',
    name: 'Transfusão de sangue',
    description: 'Investigar se houve sangramento',
    isActive: true,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockModule: GttModule = {
    id: 'mod-1',
    code: 'C',
    name: 'Cuidados Gerais',
    description: '',
    displayOrder: 1,
    isActive: true,
    triggerCount: 1,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockPageResponse: PageResponse<GttTrigger> = {
    content: [mockTrigger],
    page: 0,
    size: 10,
    totalElements: 1,
    totalPages: 1,
    first: true,
    last: true,
  };

  beforeEach(async () => {
    triggerServiceMock = {
      listTriggers: jest.fn().mockReturnValue(of({ success: true, data: mockPageResponse })),
      updateStatus: jest.fn().mockReturnValue(of({ success: true, data: mockTrigger })),
      deleteTrigger: jest.fn().mockReturnValue(of({ success: true, data: null })),
    };
    moduleServiceMock = {
      getCatalog: jest.fn().mockReturnValue(of({ success: true, data: [mockModule] })),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [TriggerListComponent],
      providers: [
        { provide: GttTriggerService, useValue: triggerServiceMock },
        { provide: GttModuleService, useValue: moduleServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TriggerListComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar carregando módulos e gatilhos', () => {
    fixture.detectChanges();
    expect(component.availableModules().length).toBe(1);
    expect(component.triggers().length).toBe(1);
    expect(component.isLoading()).toBe(false);
  });

  it('deve tratar erro ao carregar gatilhos', () => {
    triggerServiceMock.listTriggers.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadTriggers();
    expect(component.isLoading()).toBe(false);
  });

  it('deve filtrar com debounce na busca textual', fakeAsync(() => {
    fixture.detectChanges();
    component.searchQuery = 'trans';
    component.onSearchChange();
    // Chamada imediata subsequente para cobrir cancelamento do timeout anterior
    component.searchQuery = 'transfusão';
    component.onSearchChange();

    expect(component.currentPage).toBe(0);
    tick(350);
    expect(triggerServiceMock.listTriggers).toHaveBeenCalledWith(
      undefined,
      'transfusão',
      null,
      0,
      10,
      'code,asc'
    );
  }));

  it('deve filtrar por módulo e por status', () => {
    fixture.detectChanges();
    component.selectedModuleId = 'mod-1';
    component.selectedStatus = true;
    component.onFilterChange();

    expect(component.currentPage).toBe(0);
    expect(triggerServiceMock.listTriggers).toHaveBeenCalledWith(
      'mod-1',
      '',
      true,
      0,
      10,
      'code,asc'
    );
  });

  it('deve alterar página', () => {
    fixture.detectChanges();
    component.onPageChange(3);
    expect(component.currentPage).toBe(3);
    expect(triggerServiceMock.listTriggers).toHaveBeenCalledWith(
      undefined,
      '',
      null,
      3,
      10,
      'code,asc'
    );
  });

  it('deve abrir modal para novo gatilho e salvar', () => {
    component.openCreateModal();
    expect(component.selectedTrigger()).toBeNull();
    expect(component.isFormModalOpen()).toBe(true);

    component.onTriggerSaved();
    expect(component.isFormModalOpen()).toBe(false);
    expect(triggerServiceMock.listTriggers).toHaveBeenCalled();
  });

  it('deve abrir modal para editar gatilho existente', () => {
    component.openEditModal(mockTrigger);
    expect(component.selectedTrigger()).toEqual(mockTrigger);
    expect(component.isFormModalOpen()).toBe(true);
  });

  it('deve abrir diálogo de status e inativar gatilho', () => {
    component.openStatusDialog(mockTrigger);
    expect(component.targetTrigger()).toEqual(mockTrigger);
    expect(component.isStatusDialogOpen()).toBe(true);

    component.onConfirmStatusChange();
    expect(component.isStatusDialogOpen()).toBe(false);
    expect(triggerServiceMock.updateStatus).toHaveBeenCalledWith('trig-1', { isActive: false });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      'Gatilho C1 inativado com sucesso.'
    );
  });

  it('deve abrir diálogo de status e reativar gatilho inativo', () => {
    const inactive = { ...mockTrigger, isActive: false };
    component.openStatusDialog(inactive);

    component.onConfirmStatusChange();
    expect(triggerServiceMock.updateStatus).toHaveBeenCalledWith('trig-1', { isActive: true });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      'Gatilho C1 ativado com sucesso.'
    );
  });

  it('não deve alterar status se targetTrigger for nulo', () => {
    component.targetTrigger.set(null);
    component.onConfirmStatusChange();
    expect(triggerServiceMock.updateStatus).not.toHaveBeenCalled();
  });

  it('deve abrir diálogo de exclusão e excluir gatilho', () => {
    component.openDeleteDialog(mockTrigger);
    expect(component.targetTrigger()).toEqual(mockTrigger);
    expect(component.isDeleteDialogOpen()).toBe(true);

    component.onConfirmDelete();
    expect(component.isDeleteDialogOpen()).toBe(false);
    expect(triggerServiceMock.deleteTrigger).toHaveBeenCalledWith('trig-1');
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      'Gatilho C1 excluído com sucesso.'
    );
  });

  it('não deve excluir se targetTrigger for nulo', () => {
    component.targetTrigger.set(null);
    component.onConfirmDelete();
    expect(triggerServiceMock.deleteTrigger).not.toHaveBeenCalled();
  });
});
