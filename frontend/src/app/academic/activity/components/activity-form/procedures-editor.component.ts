import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente para inserção e edição de procedimentos invasivos ou cirúrgicos no prontuário simulado.
 */
@Component({
  selector: 'app-procedures-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './procedures-editor.component.html'
})
export class ProceduresEditorComponent {
  /**
   * Coleção reativa de procedimentos cadastrados.
   */
  @Input({ required: true }) proceduresArray!: FormArray;

  /**
   * Notifica a adição de um novo procedimento cirúrgico ou invasivo.
   */
  @Output() addProcedure = new EventEmitter<void>();

  /**
   * Notifica a exclusão de um procedimento por índice.
   */
  @Output() removeProcedure = new EventEmitter<number>();

  /**
   * Cast auxiliar para FormGroup.
   */
  getGroup(control: any): FormGroup {
    return control as FormGroup;
  }
}
