import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SeverityGuideComponent } from './severity-guide.component';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';
import { of, throwError } from 'rxjs';

describe('SeverityGuideComponent', () => {
  let component: SeverityGuideComponent;
  let fixture: ComponentFixture<SeverityGuideComponent>;
  let severityServiceMock: { getGuide: jest.Mock };

  const mockSeverities: HarmSeverity[] = [
    {
      id: 'sev-1',
      categoryLetter: 'A',
      name: 'Circunstância com capacidade de causar dano',
      description: 'Potencial de dano sem ocorrência',
      isHarm: false,
      isActive: true,
    },
    {
      id: 'sev-2',
      categoryLetter: 'E',
      name: 'Dano temporário com intervenção',
      description: 'Causou dano temporário',
      isHarm: true,
      isActive: true,
    },
    {
      id: 'sev-3',
      categoryLetter: 'I',
      name: 'Óbito do paciente',
      description: 'O evento contribuiu ou causou a morte',
      isHarm: true,
      isActive: true,
    },
  ];

  beforeEach(async () => {
    severityServiceMock = {
      getGuide: jest.fn().mockReturnValue(of({ success: true, data: mockSeverities })),
    };

    await TestBed.configureTestingModule({
      imports: [SeverityGuideComponent],
      providers: [{ provide: HarmSeverityService, useValue: severityServiceMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(SeverityGuideComponent);
    component = fixture.componentInstance;
  });

  it('deve inicializar e carregar o guia de severidades', () => {
    fixture.detectChanges();
    expect(component.severities().length).toBe(3);
    expect(component.isLoading()).toBe(false);
    expect(component.filteredList().length).toBe(3);
    expect(component.showNoHarmGroup()).toBe(true);
    expect(component.showHarmGroup()).toBe(true);
  });

  it('deve filtrar por aba Sem Dano (NO_HARM)', () => {
    fixture.detectChanges();
    component.selectedTab.set('NO_HARM');

    expect(component.filteredList().length).toBe(1);
    expect(component.filteredList()[0].categoryLetter).toBe('A');
    expect(component.showNoHarmGroup()).toBe(true);
    expect(component.showHarmGroup()).toBe(false);
  });

  it('deve filtrar por aba Com Dano Real (HARM)', () => {
    fixture.detectChanges();
    component.selectedTab.set('HARM');

    expect(component.filteredList().length).toBe(2);
    expect(component.showNoHarmGroup()).toBe(false);
    expect(component.showHarmGroup()).toBe(true);
  });

  it('deve filtrar por busca textual por categoria, nome ou descrição', () => {
    fixture.detectChanges();
    component.selectedTab.set('ALL');

    component.searchQuery.set('óbito');
    expect(component.filteredList().length).toBe(1);
    expect(component.filteredList()[0].categoryLetter).toBe('I');

    component.searchQuery.set('circunstância');
    expect(component.filteredList().length).toBe(1);
    expect(component.filteredList()[0].categoryLetter).toBe('A');

    component.searchQuery.set('intervenção');
    expect(component.filteredList().length).toBe(1);
    expect(component.filteredList()[0].categoryLetter).toBe('E');

    component.searchQuery.set('');
    expect(component.filteredList().length).toBe(3);
  });

  it('deve tratar erro no carregamento do guia', () => {
    severityServiceMock.getGuide.mockReturnValue(throwError(() => new Error('Falha')));
    component.loadGuide();
    expect(component.isLoading()).toBe(false);
  });
});
