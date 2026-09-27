import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Componente com formulário para atribuição de nota (0.00 a 10.00) e parecer formativo do docente.
 */
@Component({
  selector: 'app-grading-feedback-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './grading-feedback-form.component.html'
})
export class GradingFeedbackFormComponent {
  /**
   * Formulário reativo com campos de nota e parecer formativo.
   */
  @Input({ required: true }) gradeForm!: FormGroup;

  /**
   * Indica se a submissão da avaliação está em processamento no backend.
   */
  @Input() isSubmitting = false;

  /**
   * Indica se a submissão já possui nota previamente lançada.
   */
  @Input() isGraded = false;

  /**
   * Notifica a submissão do formulário de avaliação pedagógica.
   */
  @Output() submitGrade = new EventEmitter<void>();

  /**
   * Notifica o cancelamento ou retorno para a tela anterior.
   */
  @Output() goBack = new EventEmitter<void>();

  /**
   * Dispara a validação e emissão do evento de salvamento.
   */
  onSubmit(): void {
    this.submitGrade.emit();
  }
}
