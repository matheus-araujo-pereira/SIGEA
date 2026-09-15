import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModuleFormComponent } from './module-form.component';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { SimpleChange } from '@angular/core';
import { of, throwError } from 'rxjs';

describe('ModuleFormComponent', () => {
  let component: ModuleFormComponent;
  let fixture: ComponentFixture<ModuleFormComponent>;
  let moduleServiceMock: { createModule: jest.Mock; updateModule: jest.Mock };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const mockModule: GttModule = {
    id: 'mod-1',
    code: 'C',
    name: 'Cuidados Gerais',
    description: 'Escopo geral de cuidados',
    displayOrder: 1,
    isActive: true,
    triggerCount: 5,
    createdAt: '2026-09-14T00:00:00Z',
  };

  beforeEach(async () => {
    moduleServiceMock = {
      createModule: jest.fn(),
      updateModule: jest.fn(),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ModuleFormComponent],
      providers: [
        { provide: GttModuleService, useValue: moduleServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ModuleFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar com formulário vazio e não estar em modo de edição', () => {
    expect(component).toBeTruthy();
    expect(component.isEditing()).toBe(false);
  });

  it('deve preencher o formulário em modo de edição', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('module', mockModule);
    component.ngOnChanges({
      module: new SimpleChange(null, mockModule, true),
      isOpen: new SimpleChange(false, true, true),
    });

    expect(component.isEditing()).toBe(true);
    expect(component.form.get('code')?.value).toBe('C');
    expect(component.form.get('name')?.value).toBe('Cuidados Gerais');
    expect(component.form.get('description')?.value).toBe('Escopo geral de cuidados');
  });

  it('deve resetar o formulário ao abrir sem módulo', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('module', null);
    component.ngOnChanges({
      module: new SimpleChange(mockModule, null, false),
      isOpen: new SimpleChange(false, true, false),
    });

    expect(component.isEditing()).toBe(false);
    expect(component.form.get('code')?.value).toBe('');
    expect(component.form.get('name')?.value).toBe('');
  });

  it('não deve submeter formulário inválido', () => {
    component.onSubmit();
    expect(component.form.touched).toBe(true);
    expect(moduleServiceMock.createModule).not.toHaveBeenCalled();
    expect(moduleServiceMock.updateModule).not.toHaveBeenCalled();
  });

  it('deve cadastrar novo módulo com sucesso', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    moduleServiceMock.createModule.mockReturnValue(of({ success: true, data: mockModule }));

    component.form.patchValue({
      code: 'M',
      name: 'Medicamentos',
      description: 'Módulo de medicação',
    });

    component.onSubmit();

    expect(moduleServiceMock.createModule).toHaveBeenCalledWith({
      code: 'M',
      name: 'Medicamentos',
      description: 'Módulo de medicação',
    });
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Módulo GTT cadastrado com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
    expect(component.isSaving()).toBe(false);
  });

  it('deve tratar erro no cadastro de novo módulo', () => {
    moduleServiceMock.createModule.mockReturnValue(throwError(() => new Error('Falha')));

    component.form.patchValue({
      code: 'M',
      name: 'Medicamentos',
      description: 'Módulo de medicação',
    });

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
  });

  it('deve atualizar módulo existente com sucesso', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('module', mockModule);
    component.ngOnChanges({
      module: new SimpleChange(null, mockModule, true),
      isOpen: new SimpleChange(false, true, true),
    });

    moduleServiceMock.updateModule.mockReturnValue(of({ success: true, data: mockModule }));

    component.form.patchValue({
      name: 'Cuidados Gerais Alterado',
    });

    component.onSubmit();

    expect(moduleServiceMock.updateModule).toHaveBeenCalledWith(
      'mod-1',
      expect.objectContaining({
        name: 'Cuidados Gerais Alterado',
      })
    );
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Módulo GTT atualizado com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
    expect(component.isSaving()).toBe(false);
  });

  it('deve tratar erro na atualização do módulo', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('module', mockModule);
    component.ngOnChanges({
      module: new SimpleChange(null, mockModule, true),
      isOpen: new SimpleChange(false, true, true),
    });

    moduleServiceMock.updateModule.mockReturnValue(throwError(() => new Error('Falha')));

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
  });

  it('deve preencher com descrição vazia quando description for nulo ou vazio', () => {
    const modNoDesc = { ...mockModule, description: '' };
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('module', modNoDesc);
    component.ngOnChanges({
      module: new SimpleChange(null, modNoDesc, true),
    });

    expect(component.form.get('description')?.value).toBe('');
  });

  it('não deve fazer nada no ngOnChanges se isOpen for falso ou mudanças forem irrelevantes', () => {
    fixture.componentRef.setInput('isOpen', false);
    component.ngOnChanges({
      isOpen: new SimpleChange(true, false, false),
    });
    expect(component.form.get('code')?.value).toBe('');

    component.ngOnChanges({
      unrelated: new SimpleChange(null, 'val', true),
    });
    expect(component.form.get('code')?.value).toBe('');
  });

  it('deve emitir closed ao chamar onClose', () => {
    const closedSpy = jest.fn();
    component.closed.subscribe(closedSpy);

    component.onClose();

    expect(closedSpy).toHaveBeenCalled();
  });
});
