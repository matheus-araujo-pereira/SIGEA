import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TriggerFormComponent } from './trigger-form.component';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { GttModule } from '../../../../core/models/gtt-module.model';
import { SimpleChange } from '@angular/core';
import { of, throwError } from 'rxjs';

describe('TriggerFormComponent', () => {
  let component: TriggerFormComponent;
  let fixture: ComponentFixture<TriggerFormComponent>;
  let triggerServiceMock: { createTrigger: jest.Mock; updateTrigger: jest.Mock };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const mockTrigger: GttTrigger = {
    id: 'trig-1',
    moduleId: 'mod-1',
    moduleCode: 'C',
    moduleName: 'Cuidados',
    code: 'C1',
    name: 'Transfusão de sangue',
    description: 'Investigar se houve hemorragia aguda',
    isActive: true,
    createdAt: '2026-09-14T00:00:00Z',
  };

  const mockModules: GttModule[] = [
    {
      id: 'mod-1',
      code: 'C',
      name: 'Cuidados Gerais',
      description: 'Cuidados',
      displayOrder: 1,
      isActive: true,
      triggerCount: 1,
    createdAt: '2026-09-14T00:00:00Z',
    },
  ];

  beforeEach(async () => {
    triggerServiceMock = {
      createTrigger: jest.fn(),
      updateTrigger: jest.fn(),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [TriggerFormComponent],
      providers: [
        { provide: GttTriggerService, useValue: triggerServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TriggerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar com formulário vazio e não estar em modo edição', () => {
    expect(component).toBeTruthy();
    expect(component.isEditing()).toBe(false);
  });

  it('deve preencher o formulário em modo de edição', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('trigger', mockTrigger);
    component.ngOnChanges({
      trigger: new SimpleChange(null, mockTrigger, true),
      isOpen: new SimpleChange(false, true, true),
    });

    expect(component.isEditing()).toBe(true);
    expect(component.form.get('code')?.value).toBe('C1');
    expect(component.form.get('moduleId')?.value).toBe('mod-1');
    expect(component.form.get('name')?.value).toBe('Transfusão de sangue');
  });

  it('deve resetar o formulário ao abrir sem gatilho', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('trigger', null);
    component.ngOnChanges({
      trigger: new SimpleChange(mockTrigger, null, false),
      isOpen: new SimpleChange(false, true, false),
    });

    expect(component.isEditing()).toBe(false);
    expect(component.form.get('code')?.value).toBe('');
  });

  it('não deve submeter formulário inválido', () => {
    component.onSubmit();
    expect(component.form.touched).toBe(true);
    expect(triggerServiceMock.createTrigger).not.toHaveBeenCalled();
  });

  it('deve cadastrar novo gatilho com sucesso', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    triggerServiceMock.createTrigger.mockReturnValue(of({ success: true, data: mockTrigger }));

    component.form.patchValue({
      moduleId: 'mod-1',
      code: 'C1',
      name: 'Transfusão de sangue',
      description: 'Diretrizes de investigação',
    });

    component.onSubmit();

    expect(triggerServiceMock.createTrigger).toHaveBeenCalledWith({
      moduleId: 'mod-1',
      code: 'C1',
      name: 'Transfusão de sangue',
      description: 'Diretrizes de investigação',
    });
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Gatilho GTT cadastrado com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
    expect(component.isSaving()).toBe(false);
  });

  it('deve tratar erro no cadastro do gatilho', () => {
    triggerServiceMock.createTrigger.mockReturnValue(throwError(() => new Error('Falha')));

    component.form.patchValue({
      moduleId: 'mod-1',
      code: 'C1',
      name: 'Transfusão de sangue',
      description: 'Diretrizes de investigação',
    });

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
  });

  it('deve atualizar gatilho existente com sucesso', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('trigger', mockTrigger);
    component.ngOnChanges({
      trigger: new SimpleChange(null, mockTrigger, true),
      isOpen: new SimpleChange(false, true, true),
    });

    triggerServiceMock.updateTrigger.mockReturnValue(of({ success: true, data: mockTrigger }));

    component.form.patchValue({
      name: 'Transfusão de sangue atualizado',
    });

    component.onSubmit();

    expect(triggerServiceMock.updateTrigger).toHaveBeenCalledWith(
      'trig-1',
      expect.objectContaining({
        name: 'Transfusão de sangue atualizado',
      })
    );
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Gatilho GTT atualizado com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
    expect(component.isSaving()).toBe(false);
  });

  it('deve tratar erro na atualização do gatilho', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('trigger', mockTrigger);
    component.ngOnChanges({
      trigger: new SimpleChange(null, mockTrigger, true),
      isOpen: new SimpleChange(false, true, true),
    });

    triggerServiceMock.updateTrigger.mockReturnValue(throwError(() => new Error('Falha')));

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
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
