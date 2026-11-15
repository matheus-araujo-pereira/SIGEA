import {
  autoResizeTextarea,
  calculateGutItemScore,
  formatTextListOrString,
  getGutTrendValue,
  getTriggerDisplayEvidence,
  getTriggerEvidenceText
} from '../quality-tools-display.util';
import { GutItemData, IdentifiedTriggerData } from '../../models/activity.model';

describe('quality-tools-display.util', () => {
  describe('autoResizeTextarea', () => {
    it('deve ajustar a altura do textarea respeitando a altura mínima padrão e customizada', () => {
      const textarea = document.createElement('textarea');
      Object.defineProperty(textarea, 'scrollHeight', { value: 150, configurable: true });

      autoResizeTextarea({ target: textarea } as unknown as Event);
      expect(textarea.style.height).toBe('150px');

      Object.defineProperty(textarea, 'scrollHeight', { value: 0, configurable: true });
      autoResizeTextarea({ target: textarea } as unknown as Event, 100);
      expect(textarea.style.height).toBe('100px');
    });

    it('não deve falhar quando o alvo do evento não existir', () => {
      expect(() => autoResizeTextarea({ target: null } as unknown as Event)).not.toThrow();
    });
  });

  describe('getTriggerEvidenceText e getTriggerDisplayEvidence', () => {
    it('deve priorizar clinicalJustification, depois notes e rationale removendo espaços', () => {
      const t1 = { clinicalJustification: '  Justificativa oficial  ', notes: 'Nota' } as IdentifiedTriggerData;
      expect(getTriggerEvidenceText(t1)).toBe('Justificativa oficial');
      expect(getTriggerDisplayEvidence(t1)).toBe('Justificativa oficial');

      const t2 = { clinicalJustification: '   ', notes: '  Evidência em notes  ' } as IdentifiedTriggerData;
      expect(getTriggerEvidenceText(t2)).toBe('Evidência em notes');

      const t3 = { rationale: '  Evidência legada  ' } as IdentifiedTriggerData;
      expect(getTriggerEvidenceText(t3)).toBe('Evidência legada');
    });

    it('deve retornar string vazia em getTriggerEvidenceText e texto indicativo em getTriggerDisplayEvidence para registro legado sem evidência', () => {
      const legacyEmpty = { triggerId: '1', triggerCode: 'C1', triggerName: 'Transfusão' } as IdentifiedTriggerData;
      expect(getTriggerEvidenceText(legacyEmpty)).toBe('');
      expect(getTriggerDisplayEvidence(legacyEmpty)).toBe('Evidência não informada (registro anterior)');
      expect(getTriggerEvidenceText(null)).toBe('');
    });
  });

  describe('formatTextListOrString', () => {
    it('deve formatar array de textos ignorando itens vazios ou nulos', () => {
      expect(formatTextListOrString([' Causa A ', '   ', null as unknown as string, 'Causa B'])).toBe(
        '• Causa A\n• Causa B'
      );
    });

    it('deve formatar string direta, fallback legado ou texto vazio', () => {
      expect(formatTextListOrString('  Texto direto  ')).toBe('• Texto direto');
      expect(formatTextListOrString([], '  Legado  ')).toBe('Legado');
      expect(formatTextListOrString([], '   ', 'Não informado')).toBe('Não informado');
      expect(formatTextListOrString([], null, 'Não informado')).toBe('Não informado');
      expect(formatTextListOrString(undefined, undefined)).toBe('Sem observações');
    });
  });

  describe('getGutTrendValue e calculateGutItemScore', () => {
    it('deve resolver trend ou tendency legado e padrão 1', () => {
      expect(getGutTrendValue({ problem: 'P', gravity: 4, urgency: 3, trend: 5 })).toBe(5);
      expect(getGutTrendValue({ problem: 'P', gravity: 4, urgency: 3, tendency: 2 })).toBe(2);
      expect(getGutTrendValue({ problem: 'P', gravity: 4, urgency: 3 } as GutItemData)).toBe(1);
    });

    it('deve retornar score persistido ou calcular G*U*T dinamicamente', () => {
      expect(calculateGutItemScore({ problem: 'P', gravity: 5, urgency: 4, trend: 3, score: 60 })).toBe(60);
      expect(calculateGutItemScore({ problem: 'P', gravity: 5, urgency: 4, trend: 3 })).toBe(60);
      expect(calculateGutItemScore({ problem: 'P', gravity: 0, urgency: 0 } as GutItemData)).toBe(1);
    });
  });
});
