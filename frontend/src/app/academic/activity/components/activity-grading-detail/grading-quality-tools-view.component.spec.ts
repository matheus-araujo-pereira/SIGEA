import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GradingQualityToolsViewComponent } from './grading-quality-tools-view.component';
import { QualityToolsDataDTO } from '../../models/activity.model';

describe('GradingQualityToolsViewComponent', () => {
  let component: GradingQualityToolsViewComponent;
  let fixture: ComponentFixture<GradingQualityToolsViewComponent>;

  const mockQualityTools: QualityToolsDataDTO = {
    ishikawa: {
      problem: 'Queda de leito',
      method: 'Sem contenção',
      manpower: 'Equipe reduzida',
      material: 'Grade com defeito',
      machine: 'Leito manual antigo',
      environment: 'Piso escorregadio',
      measurement: 'Escala de Morse não aplicada'
    },
    gutItems: [
      { problem: 'Risco de queda', gravity: 5, urgency: 4, tendency: 4, score: 80 }
    ],
    fiveWTwoHItems: [
      {
        what: 'Revisão das grades',
        why: 'Evitar novas quedas',
        where: 'Enfermaria 3',
        when: '24 horas',
        who: 'Manutenção',
        how: 'Troca de travas',
        howMuch: 'R$ 150,00'
      }
    ],
    pdca: {
      plan: 'Mapear leitos críticos',
      doAction: 'Instalar grades novas',
      checkAction: 'Ronda diária de segurança',
      act: 'Padronizar checklist matinal'
    },
    swot: {
      strengths: ['Equipe engajada'],
      weaknesses: ['Leitos antigos'],
      opportunities: ['Aquisição de novas camas'],
      threats: ['Cortes orçamentários']
    },
    brainstormingNotes: 'Sugerida campainha sem fio para o leito.'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GradingQualityToolsViewComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(GradingQualityToolsViewComponent);
    component = fixture.componentInstance;
  });

  it('deve renderizar estados vazios quando qualityTools for nulo', () => {
    component.qualityTools = null;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Ishikawa não preenchido.');
    expect(compiled.textContent).toContain('Nenhum item adicionado na Matriz GUT.');
    expect(compiled.textContent).toContain('Nenhum plano 5W2H cadastrado.');
    expect(compiled.textContent).toContain('PDCA não preenchido.');
  });

  it('deve renderizar as ferramentas da qualidade preenchidas', () => {
    component.qualityTools = mockQualityTools;
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Queda de leito');
    expect(compiled.textContent).toContain('Risco de queda');
    expect(compiled.textContent).toContain('80');
    expect(compiled.textContent).toContain('Revisão das grades');
    expect(compiled.textContent).toContain('Mapear leitos críticos');
    expect(compiled.textContent).toContain('Equipe engajada');
    expect(compiled.textContent).toContain('Sugerida campainha sem fio para o leito.');
  });
});
