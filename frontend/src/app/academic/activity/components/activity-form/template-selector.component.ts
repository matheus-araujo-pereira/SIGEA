import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClinicalCaseTemplateResponseDTO } from '../../../clinical/models/clinical-case-template.model';

/**
 * Componente para seleção e carregamento de modelos canônicos de prontuário simulado do catálogo IHI-GTT.
 */
@Component({
  selector: 'app-template-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './template-selector.component.html'
})
export class TemplateSelectorComponent {
  /**
   * Catálogo de modelos de casos clínicos cadastrados.
   */
  @Input() templates: ClinicalCaseTemplateResponseDTO[] = [];

  /**
   * Identificador do modelo atualmente selecionado no dropdown.
   */
  @Input() selectedTemplateId = '';

  /**
   * Objeto do modelo ativo selecionado.
   */
  @Input() activeTemplate: ClinicalCaseTemplateResponseDTO | null = null;

  /**
   * Indica se o formulário está em modo de edição (oculta a biblioteca para não sobrescrever).
   */
  @Input() isEditMode = false;

  /**
   * Emite o ID do modelo selecionado ao alterar o dropdown.
   */
  @Output() templateChange = new EventEmitter<string>();

  /**
   * Dispara a aplicação do modelo selecionado aos campos do formulário.
   */
  @Output() applyTemplate = new EventEmitter<void>();

  /**
   * Captura o evento de mudança no elemento select.
   */
  onSelectChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.templateChange.emit(target.value);
  }
}
