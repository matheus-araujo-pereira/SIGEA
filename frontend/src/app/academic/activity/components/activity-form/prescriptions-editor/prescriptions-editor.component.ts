import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente para gestão e edição de prescrições medicamentosas e anotações de checagem.
 */
@Component({
  selector: 'app-prescriptions-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './prescriptions-editor.component.html'
})
export class PrescriptionsEditorComponent {
  /**
   * Coleção reativa de medicamentos prescritos.
   */
  @Input({ required: true }) prescriptionsArray!: FormArray;

  /**
   * Notifica a adição de uma nova prescrição medicamentosa.
   */
  @Output() addPrescription = new EventEmitter<void>();

  /**
   * Notifica a exclusão de uma prescrição pelo índice.
   */
  @Output() removePrescription = new EventEmitter<number>();

  /**
   * Cast auxiliar para FormGroup.
   */
  getGroup(control: any): FormGroup {
    return control as FormGroup;
  }
}
