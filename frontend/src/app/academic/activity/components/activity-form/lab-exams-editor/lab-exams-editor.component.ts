import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente para inserção e edição de exames laboratoriais no prontuário simulado.
 */
@Component({
  selector: 'app-lab-exams-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './lab-exams-editor.component.html'
})
export class LabExamsEditorComponent {
  /**
   * Coleção reativa de exames laboratoriais cadastrados.
   */
  @Input({ required: true }) labExamsArray!: FormArray;

  /**
   * Notifica a adição de um novo exame laboratorial.
   */
  @Output() addExam = new EventEmitter<void>();

  /**
   * Notifica a exclusão de um exame pelo índice.
   */
  @Output() removeExam = new EventEmitter<number>();

  /**
   * Cast auxiliar para FormGroup.
   */
  getGroup(control: any): FormGroup {
    return control as FormGroup;
  }
}
