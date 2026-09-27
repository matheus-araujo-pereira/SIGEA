import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AcademicClassResponseDTO } from '../../../clazz/models/academic-class.model';

/**
 * Componente de formulário para preenchimento dos dados gerais e pedagógicos da atividade avaliativa.
 */
@Component({
  selector: 'app-activity-info-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './activity-info-form.component.html'
})
export class ActivityInfoFormComponent {
  /**
   * Formulário raiz com os campos de controle da atividade.
   */
  @Input({ required: true }) form!: FormGroup;

  /**
   * Turmas ativas do professor logado.
   */
  @Input() classes: AcademicClassResponseDTO[] = [];

  /**
   * Indica se a atividade está sendo editada.
   */
  @Input() isEditMode = false;
}
