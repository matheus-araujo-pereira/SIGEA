import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente para edição de anotações de evolução multiprofissional no prontuário simulado.
 */
@Component({
  selector: 'app-evolution-notes-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './evolution-notes-editor.component.html'
})
export class EvolutionNotesEditorComponent {
  /**
   * Array de controles reativos com as evoluções multiprofissionais.
   */
  @Input({ required: true }) evolutionNotesArray!: FormArray;

  /**
   * Notifica a solicitação de adição de uma nova nota de evolução.
   */
  @Output() addNote = new EventEmitter<void>();

  /**
   * Notifica a exclusão de uma nota de evolução por índice.
   */
  @Output() removeNote = new EventEmitter<number>();

  /**
   * Cast auxiliar para FormGroup.
   */
  getGroup(control: any): FormGroup {
    return control as FormGroup;
  }
}
