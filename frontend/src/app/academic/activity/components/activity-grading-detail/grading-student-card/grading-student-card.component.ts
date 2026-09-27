import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import type { SubmissionResponseDTO } from '../../../models/activity.model';

/**
 * Componente que exibe os dados cadastrais do estudante e o status da submissão clínica.
 */
@Component({
  selector: 'app-grading-student-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grading-student-card.component.html'
})
export class GradingStudentCardComponent {
  /**
   * Dados detalhados da submissão avaliada.
   */
  @Input({ required: true }) submission!: SubmissionResponseDTO;
}
