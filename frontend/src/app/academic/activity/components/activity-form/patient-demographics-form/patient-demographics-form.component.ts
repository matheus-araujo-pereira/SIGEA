import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente para inserção de dados sociodemográficos e de internação do prontuário simulado.
 */
@Component({
  selector: 'app-patient-demographics-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './patient-demographics-form.component.html'
})
export class PatientDemographicsFormComponent {
  /**
   * Sub-grupo reativo do prontuário simulado contendo dados do paciente.
   */
  @Input({ required: true }) clinicalCaseGroup!: FormGroup;
}
