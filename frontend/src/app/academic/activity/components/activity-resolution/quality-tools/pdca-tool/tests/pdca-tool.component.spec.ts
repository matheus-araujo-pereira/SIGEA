import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { PdcaToolComponent } from '../pdca-tool.component';
import { PdcaData } from '../../../../../models/activity.model';

describe('PdcaToolComponent', () => {
  let component: PdcaToolComponent;
  let fixture: ComponentFixture<PdcaToolComponent>;

  const mockPdca: PdcaData = {
    plan: 'Plano de ação',
    doPhase: 'Executar treinamento',
    checkPhase: 'Checar auditoria',
    actPhase: 'Padronizar POP'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PdcaToolComponent, FormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(PdcaToolComponent);
    component = fixture.componentInstance;
    component.pdca = mockPdca;
    fixture.detectChanges();
  });

  it('deve renderizar as 4 etapas do ciclo PDCA', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('P - Plan (Planejar)');
    expect(compiled.textContent).toContain('D - Do (Executar)');
    expect(compiled.textContent).toContain('C - Check (Checar / Estudar)');
    expect(compiled.textContent).toContain('A - Act (Agir / Padronizar)');
  });
});
