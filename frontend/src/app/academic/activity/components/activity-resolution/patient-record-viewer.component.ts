import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { ClinicalCaseData } from '../../models/activity.model';

/**
 * Componente de visualização em formato de prontuário eletrônico do paciente simulado.
 * Apresenta dados de admissão, evoluções multiprofissionais, prescrições, exames e cirurgias.
 */
@Component({
  selector: 'app-patient-record-viewer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patient-record-viewer.component.html'
})
export class PatientRecordViewerComponent {
  /**
   * Dados clínicos do prontuário simulado.
   */
  @Input() clinicalCase?: ClinicalCaseData;

  /**
   * Inicial do paciente para exibição em avatar.
   */
  @Input() patientInitial = 'P';
}
