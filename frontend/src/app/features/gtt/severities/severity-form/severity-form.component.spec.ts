import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SeverityFormComponent } from './severity-form.component';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';
import { SimpleChange } from '@angular/core';
import { of, throwError } from 'rxjs';

describe('SeverityFormComponent', () => {
  let component: SeverityFormComponent;
  let fixture: ComponentFixture<SeverityFormComponent>;
  let severityServiceMock: { createSeverity: jest.Mock; updateSeverity: jest.Mock };
  let toastServiceMock: { success: jest.Mock; error: jest.Mock };

  const mockSeverity: HarmSeverity = {
    id: 'sev-1',
    categoryLetter: 'E',
    name: 'Dano temporário com intervenção',
    description: 'Exigiu intervenção clínica',
    isHarm: true,
    isActive: true,
  };

  beforeEach(async () => {
    severityServiceMock = {
      createSeverity: jest.fn(),
      updateSeverity: jest.fn(),
    };
    toastServiceMock = {
      success: jest.fn(),
      error: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [SeverityFormComponent],
      providers: [
        { provide: HarmSeverityService, useValue: severityServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SeverityFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve inicializar com formulário vazio e não estar em modo edição', () => {
    expect(component).toBeTruthy();
    expect(component.isEditing()).toBe(false);
  });

  it('deve preencher o formulário em modo de edição', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('severity', mockSeverity);
    component.ngOnChanges({
      severity: new SimpleChange(null, mockSeverity, true),
      isOpen: new SimpleChange(false, true, true),
    });

    expect(component.isEditing()).toBe(true);
    expect(component.form.get('categoryLetter')?.value).toBe('E');
    expect(component.form.get('name')?.value).toBe('Dano temporário com intervenção');
    expect(component.form.get('isHarm')?.value).toBe(true);
  });

  it('deve resetar formulário ao abrir sem severidade', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('severity', null);
    component.ngOnChanges({
      severity: new SimpleChange(mockSeverity, null, false),
      isOpen: new SimpleChange(false, true, false),
    });

    expect(component.isEditing()).toBe(false);
    expect(component.form.get('categoryLetter')?.value).toBe('');
    expect(component.form.get('isHarm')?.value).toBe(false);
  });

  it('não deve submeter formulário inválido', () => {
    component.onSubmit();
    expect(component.form.touched).toBe(true);
    expect(severityServiceMock.createSeverity).not.toHaveBeenCalled();
  });

  it('deve cadastrar nova categoria de gravidade com sucesso', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    severityServiceMock.createSeverity.mockReturnValue(of({ success: true, data: mockSeverity }));

    component.form.patchValue({
      categoryLetter: 'F',
      name: 'Dano temporário com hospitalização',
      description: 'Prolongou a internação',
      isHarm: true,
    });

    component.onSubmit();

    expect(severityServiceMock.createSeverity).toHaveBeenCalledWith({
      categoryLetter: 'F',
      name: 'Dano temporário com hospitalização',
      description: 'Prolongou a internação',
      isHarm: true,
    });
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Categoria de gravidade cadastrada com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
    expect(component.isSaving()).toBe(false);
  });

  it('deve tratar erro no cadastro da categoria de gravidade', () => {
    severityServiceMock.createSeverity.mockReturnValue(throwError(() => new Error('Falha')));

    component.form.patchValue({
      categoryLetter: 'F',
      name: 'Dano temporário com hospitalização',
      description: 'Prolongou a internação',
      isHarm: true,
    });

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
  });

  it('deve atualizar categoria de gravidade existente com sucesso', () => {
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);

    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('severity', mockSeverity);
    component.ngOnChanges({
      severity: new SimpleChange(null, mockSeverity, true),
      isOpen: new SimpleChange(false, true, true),
    });

    severityServiceMock.updateSeverity.mockReturnValue(of({ success: true, data: mockSeverity }));

    component.form.patchValue({
      name: 'Dano temporário atualizado',
    });

    component.onSubmit();

    expect(severityServiceMock.updateSeverity).toHaveBeenCalledWith(
      'sev-1',
      expect.objectContaining({
        name: 'Dano temporário atualizado',
      })
    );
    expect(toastServiceMock.success).toHaveBeenCalledWith('Sucesso', 'Categoria de gravidade atualizada com sucesso.');
    expect(savedSpy).toHaveBeenCalled();
    expect(component.isSaving()).toBe(false);
  });

  it('deve tratar erro na atualização da gravidade', () => {
    fixture.componentRef.setInput('isOpen', true);
    fixture.componentRef.setInput('severity', mockSeverity);
    component.ngOnChanges({
      severity: new SimpleChange(null, mockSeverity, true),
      isOpen: new SimpleChange(false, true, true),
    });

    severityServiceMock.updateSeverity.mockReturnValue(throwError(() => new Error('Falha')));

    component.onSubmit();

    expect(component.isSaving()).toBe(false);
  });

  it('não deve fazer nada no ngOnChanges se isOpen for falso ou mudanças forem irrelevantes', () => {
    fixture.componentRef.setInput('isOpen', false);
    component.ngOnChanges({
      isOpen: new SimpleChange(true, false, false),
    });
    expect(component.form.get('categoryLetter')?.value).toBe('');

    component.ngOnChanges({
      unrelated: new SimpleChange(null, 'val', true),
    });
    expect(component.form.get('categoryLetter')?.value).toBe('');
  });

  it('deve emitir closed ao chamar onClose', () => {
    const closedSpy = jest.fn();
    component.closed.subscribe(closedSpy);

    component.onClose();

    expect(closedSpy).toHaveBeenCalled();
  });
});
