import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ModuleListComponent } from './module-list.component';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { PageResponse } from '../../../../core/models/page.model';
import { of, throwError } from 'rxjs';

describe('ModuleListComponent', () => {
  let component: ModuleListComponent;
  let fixture: ComponentFixture<ModuleListComponent>;
  let moduleServiceMock: {
    listModules: jest.Mock;
    updateStatus: jest.Mock;
    deleteModule: jest.Mock;
  };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const mockModule: GttModule = {
    id: 'mod-1',
    code: 'C',
    name: 'Cuidados Gerais',
    description: 'Escopo geral',
    displayOrder: 1,
    isActive: true,
    triggerCount: 10,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockPageResponse: PageResponse<GttModule> = {
    content: [mockModule],
    page: 0,
    size: 10,
    totalElements: 1,
    totalPages: 1,
    first: true,
    last: true,
  };

  beforeEach(async () => {
    moduleServiceMock = {
      listModules: jest.fn().mockReturnValue(of({ success: true, data: mockPageResponse })),
      updateStatus: jest.fn().mockReturnValue(of({ success: true, data: mockModule })),
      deleteModule: jest.fn().mockReturnValue(of({ success: true, data: null })),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ModuleListComponent],
      providers: [
        { provide: GttModuleService, useValue: moduleServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ModuleListComponent);
    component = fixture.componentInstance;
  });

  it('deve carregar lista de módulos na inicialização', () => {
    fixture.detectChanges();
    expect(component.modules().length).toBe(1);
    expect(component.isLoading()).toBe(false);
    expect(moduleServiceMock.listModules).toHaveBeenCalled();
  });

  it('deve tratar erro ao carregar módulos', () => {
    moduleServiceMock.listModules.mockReturnValue(throwError(() => new Error('Erro')));
    component.loadModules();
    expect(component.isLoading()).toBe(false);
  });

  it('deve atualizar busca com debounce', fakeAsync(() => {
    fixture.detectChanges();
    component.searchQuery = 'Med';
    component.onSearchChange();
    // Chamada imediata subsequente para cobrir cancelamento de timeout anterior
    component.searchQuery = 'Medicação';
    component.onSearchChange();

    expect(component.currentPage).toBe(0);
    tick(350);
    expect(moduleServiceMock.listModules).toHaveBeenCalledWith(
      'Medicação',
      null,
      0,
      10,
      'code,asc'
    );
  }));

  it('deve filtrar por status', () => {
    fixture.detectChanges();
    component.selectedStatus = true;
    component.onFilterChange();
    expect(component.currentPage).toBe(0);
    expect(moduleServiceMock.listModules).toHaveBeenCalledWith('', true, 0, 10, 'code,asc');
  });

  it('deve mudar de página', () => {
    fixture.detectChanges();
    component.onPageChange(2);
    expect(component.currentPage).toBe(2);
    expect(moduleServiceMock.listModules).toHaveBeenCalledWith('', null, 2, 10, 'code,asc');
  });

  it('deve abrir modal para novo módulo e salvar', () => {
    component.openCreateModal();
    expect(component.selectedModule()).toBeNull();
    expect(component.isFormModalOpen()).toBe(true);

    component.onModuleSaved();
    expect(component.isFormModalOpen()).toBe(false);
    expect(moduleServiceMock.listModules).toHaveBeenCalled();
  });

  it('deve abrir modal para editar módulo existente', () => {
    component.openEditModal(mockModule);
    expect(component.selectedModule()).toEqual(mockModule);
    expect(component.isFormModalOpen()).toBe(true);
  });

  it('deve abrir diálogo de status e confirmar inativação com sucesso', () => {
    component.openStatusDialog(mockModule);
    expect(component.targetModule()).toEqual(mockModule);
    expect(component.isStatusDialogOpen()).toBe(true);

    component.onConfirmStatusChange();
    expect(component.isStatusDialogOpen()).toBe(false);
    expect(moduleServiceMock.updateStatus).toHaveBeenCalledWith('mod-1', { isActive: false });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      'Módulo Cuidados Gerais inativado com sucesso.'
    );
  });

  it('deve abrir diálogo de status e confirmar ativação com sucesso', () => {
    const inactiveMod = { ...mockModule, isActive: false };
    component.openStatusDialog(inactiveMod);

    component.onConfirmStatusChange();
    expect(moduleServiceMock.updateStatus).toHaveBeenCalledWith('mod-1', { isActive: true });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      'Módulo Cuidados Gerais ativado com sucesso.'
    );
  });

  it('não deve alterar status se targetModule for nulo', () => {
    component.targetModule.set(null);
    component.onConfirmStatusChange();
    expect(moduleServiceMock.updateStatus).not.toHaveBeenCalled();
  });

  it('deve abrir diálogo de exclusão e confirmar com sucesso', () => {
    component.openDeleteDialog(mockModule);
    expect(component.targetModule()).toEqual(mockModule);
    expect(component.isDeleteDialogOpen()).toBe(true);

    component.onConfirmDelete();
    expect(component.isDeleteDialogOpen()).toBe(false);
    expect(moduleServiceMock.deleteModule).toHaveBeenCalledWith('mod-1');
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Sucesso',
      'Módulo Cuidados Gerais excluído com sucesso.'
    );
  });

  it('não deve excluir se targetModule for nulo', () => {
    component.targetModule.set(null);
    component.onConfirmDelete();
    expect(moduleServiceMock.deleteModule).not.toHaveBeenCalled();
  });
});
