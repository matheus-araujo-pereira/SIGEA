import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SeverityListComponent } from './severity-list.component';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';
import { PageResponse } from '../../../../core/models/page.model';
import { of, throwError } from 'rxjs';

describe('SeverityListComponent', () => {
  let component: SeverityListComponent;
  let fixture: ComponentFixture<SeverityListComponent>;
  let severityServiceMock: {
    listSeverities: jest.Mock;
    updateStatus: jest.Mock;
    deleteSeverity: jest.Mock;
  };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const mockSeverity: HarmSeverity = {
    id: 'sev-1',
    categoryLetter: 'E',
    name: 'Dano temporário com intervenção',
    description: 'Intervenção clínica necessária',
    isHarm: true,
    isActive: true,
  };

  const mockPageResponse: PageResponse<HarmSeverity> = {
    content: [mockSeverity],
    page: 0,
    size: 10,
    totalElements: 1,
    totalPages: 1,
    first: true,
    last: true,
  };

  beforeEach(async () => {
    severityServiceMock = {
      listSeverities: jest.fn().mockReturnValue(of({ success: true, data: mockPageResponse })),
      updateStatus: jest.fn().mockReturnValue(of({ success: true, data: mockSeverity })),
      deleteSeverity: jest.fn().mockReturnValue(of({ success: true, data: null })),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [SeverityListComponent],
      providers: [
        { provide: HarmSeverityService, useValue: severityServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SeverityListComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar carregando lista de gravidades', () => {
    fixture.detectChanges();
    expect(component.severities().length).toBe(1);
    expect(component.isLoading()).toBe(false);
    expect(severityServiceMock.listSeverities).toHaveBeenCalled();
  });

  it('deve tratar erro ao carregar gravidades', () => {
    severityServiceMock.listSeverities.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadSeverities();
    expect(component.isLoading()).toBe(false);
  });

  it('deve buscar com debounce de 300ms', fakeAsync(() => {
    fixture.detectChanges();
    component.searchQuery = 'temp';
    component.onSearchChange();
    // Chamada imediata subsequente para cobrir cancelamento de timeout anterior
    component.searchQuery = 'temporário';
    component.onSearchChange();

    tick(300);
    expect(severityServiceMock.listSeverities).toHaveBeenCalledWith(
      'temporário',
      null,
      null,
      0,
      10,
      'categoryLetter,asc'
    );
  }));

  it('deve filtrar por enquadramento de dano e status', () => {
    fixture.detectChanges();
    component.selectedHarmFilter = true;
    component.selectedStatus = true;
    component.onFilterChange();

    expect(severityServiceMock.listSeverities).toHaveBeenCalledWith(
      '',
      true,
      true,
      0,
      10,
      'categoryLetter,asc'
    );
  });

  it('deve mudar de página', () => {
    fixture.detectChanges();
    component.onPageChange(1);

    expect(severityServiceMock.listSeverities).toHaveBeenCalledWith(
      '',
      null,
      null,
      1,
      10,
      'categoryLetter,asc'
    );
  });

  it('deve abrir modal para criar nova gravidade e salvar', () => {
    component.openCreateModal();
    expect(component.selectedSeverity()).toBeNull();
    expect(component.isFormModalOpen()).toBe(true);

    component.onSeveritySaved();
    expect(component.isFormModalOpen()).toBe(false);
    expect(severityServiceMock.listSeverities).toHaveBeenCalled();
  });

  it('deve abrir modal para editar gravidade existente', () => {
    component.openEditModal(mockSeverity);
    expect(component.selectedSeverity()).toEqual(mockSeverity);
    expect(component.isFormModalOpen()).toBe(true);
  });

  it('deve abrir diálogo de status e inativar categoria', () => {
    component.openStatusDialog(mockSeverity);
    expect(component.targetSeverity()).toEqual(mockSeverity);
    expect(component.isStatusDialogOpen()).toBe(true);

    component.onConfirmStatusChange();
    expect(component.isProcessingStatus()).toBe(false);
    expect(component.isStatusDialogOpen()).toBe(false);
    expect(severityServiceMock.updateStatus).toHaveBeenCalledWith('sev-1', { isActive: false });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Status Atualizado',
      'Categoria E foi inativada com sucesso.'
    );
  });

  it('deve abrir diálogo de status e reativar categoria inativa', () => {
    const inactiveSev = { ...mockSeverity, isActive: false };
    component.openStatusDialog(inactiveSev);

    component.onConfirmStatusChange();
    expect(severityServiceMock.updateStatus).toHaveBeenCalledWith('sev-1', { isActive: true });
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Status Atualizado',
      'Categoria E foi ativada com sucesso.'
    );
  });

  it('não deve alterar status se targetSeverity for nulo', () => {
    component.targetSeverity.set(null);
    component.onConfirmStatusChange();
    expect(severityServiceMock.updateStatus).not.toHaveBeenCalled();
  });

  it('deve tratar erro ao alterar status', () => {
    severityServiceMock.updateStatus.mockReturnValue(throwError(() => new Error('Falha')));
    component.targetSeverity.set(mockSeverity);
    component.onConfirmStatusChange();
    expect(component.isProcessingStatus()).toBe(false);
  });

  it('deve abrir diálogo de exclusão e excluir categoria', () => {
    component.openDeleteDialog(mockSeverity);
    expect(component.targetSeverity()).toEqual(mockSeverity);
    expect(component.isDeleteDialogOpen()).toBe(true);

    component.onConfirmDelete();
    expect(component.isDeleting()).toBe(false);
    expect(component.isDeleteDialogOpen()).toBe(false);
    expect(severityServiceMock.deleteSeverity).toHaveBeenCalledWith('sev-1');
    expect(toastServiceMock.success).toHaveBeenCalledWith(
      'Exclusão Concluída',
      'Categoria E foi excluída permanentemente.'
    );
  });

  it('não deve excluir se targetSeverity for nulo', () => {
    component.targetSeverity.set(null);
    component.onConfirmDelete();
    expect(severityServiceMock.deleteSeverity).not.toHaveBeenCalled();
  });

  it('deve tratar erro na exclusão de categoria', () => {
    severityServiceMock.deleteSeverity.mockReturnValue(throwError(() => new Error('Falha')));
    component.targetSeverity.set(mockSeverity);
    component.onConfirmDelete();
    expect(component.isDeleting()).toBe(false);
  });
});
