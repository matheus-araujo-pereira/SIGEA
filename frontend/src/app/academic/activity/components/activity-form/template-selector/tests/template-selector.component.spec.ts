import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TemplateSelectorComponent } from '../template-selector.component';
import { ClinicalCaseTemplateResponseDTO } from '../../../../../clinical/models/clinical-case-template.model';

describe('TemplateSelectorComponent', () => {
  let component: TemplateSelectorComponent;
  let fixture: ComponentFixture<TemplateSelectorComponent>;

  const mockTemplates: ClinicalCaseTemplateResponseDTO[] = [
    {
      id: 'tpl-1',
      title: 'Hemorragia Pós-operatória',
      description: 'Queda súbita de hematócrito e taquicardia',
      moduleCode: 'S',
      primaryTriggerCode: 'S1',
      expectedSeverity: 'E',
      isSystemTemplate: true,
      clinicalCaseData: {} as any,
      createdAt: '2026-09-27T00:00:00Z',
      updatedAt: '2026-09-27T00:00:00Z'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemplateSelectorComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateSelectorComponent);
    component = fixture.componentInstance;
  });

  it('não deve exibir a biblioteca quando em modo de edição', () => {
    component.isEditMode = true;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('select')).toBeNull();
  });

  it('deve emitir templateChange ao mudar a seleção', () => {
    component.isEditMode = false;
    component.templates = mockTemplates;
    fixture.detectChanges();

    const changeSpy = jest.spyOn(component.templateChange, 'emit');
    const select = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    select.value = 'tpl-1';
    select.dispatchEvent(new Event('change'));

    expect(changeSpy).toHaveBeenCalledWith('tpl-1');
  });

  it('deve emitir applyTemplate ao clicar no botão de carregar', () => {
    component.isEditMode = false;
    component.templates = mockTemplates;
    component.selectedTemplateId = 'tpl-1';
    component.activeTemplate = mockTemplates[0];
    fixture.detectChanges();

    const applySpy = jest.spyOn(component.applyTemplate, 'emit');
    const button = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    button.click();

    expect(applySpy).toHaveBeenCalled();
  });
});
