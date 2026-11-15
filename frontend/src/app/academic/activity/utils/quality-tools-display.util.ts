import { GutItemData, IdentifiedTriggerData } from '../models/activity.model';

/**
 * Ajusta automaticamente a altura de um elemento `<textarea>` conforme o conteúdo digitado.
 */
export function autoResizeTextarea(event: Event, minHeightPx = 84): void {
  const target = event.target as HTMLTextAreaElement | null;
  if (!target) {
    return;
  }
  target.style.height = 'auto';
  const nextHeight = Math.max(target.scrollHeight || 0, minHeightPx);
  target.style.height = `${nextHeight}px`;
}

/**
 * Extrai o texto de evidência/justificativa clínica de um gatilho suportando formatos atuais e legados.
 */
export function getTriggerEvidenceText(trigger?: IdentifiedTriggerData | null): string {
  if (!trigger) {
    return '';
  }
  return (
    trigger.clinicalJustification?.trim() ||
    trigger.notes?.trim() ||
    trigger.rationale?.trim() ||
    ''
  );
}

/**
 * Retorna a evidência clínica formatada para leitura, mantendo compatibilidade com registros legados sem justificativa.
 */
export function getTriggerDisplayEvidence(trigger: IdentifiedTriggerData): string {
  return getTriggerEvidenceText(trigger) || 'Evidência não informada (registro anterior)';
}

/**
 * Formata listas estruturadas (Ishikawa 6M, SWOT, Brainstorming) ou strings legadas em texto legível multi-linha.
 */
export function formatTextListOrString(
  list?: string[] | string | null,
  fallbackString?: string | null,
  emptyText = 'Sem observações'
): string {
  const items = Array.isArray(list) ? list : typeof list === 'string' ? [list] : [];
  const cleaned = items.map((item) => item?.trim()).filter((item): item is string => Boolean(item));
  if (cleaned.length > 0) {
    return cleaned.map((item) => `• ${item}`).join('\n');
  }
  const single = fallbackString?.trim();
  return single || emptyText;
}

/**
 * Retorna o valor de Tendência (T) da Matriz GUT suportando `trend` e o alias legado `tendency`.
 */
export function getGutTrendValue(item: GutItemData): number {
  return item.trend ?? item.tendency ?? 1;
}

/**
 * Calcula ou retorna o score GUT (G × U × T) mantendo compatibilidade com itens legados ou sem score pré-calculado.
 */
export function calculateGutItemScore(item: GutItemData): number {
  if (typeof item.score === 'number' && item.score > 0) {
    return item.score;
  }
  const g = item.gravity || 1;
  const u = item.urgency || 1;
  const t = getGutTrendValue(item);
  return g * u * t;
}
