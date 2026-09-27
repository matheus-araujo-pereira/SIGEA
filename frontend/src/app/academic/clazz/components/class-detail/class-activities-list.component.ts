import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivityResponseDTO } from '../../../activity/models/activity.model';

/**
 * Componente atômico para listagem das atividades avaliativas de uma turma acadêmica,
 * exibindo prazos, contagem de submissões e ações contextuais por perfil de acesso.
 */
@Component({
  selector: 'app-class-activities-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './class-activities-list.component.html',
})
export class ClassActivitiesListComponent {
  /** Lista de atividades vinculadas à turma */
  readonly activities = input.required<ActivityResponseDTO[]>();
  /** Identifica se o usuário atual é administrador */
  readonly isAdmin = input<boolean>(false);
  /** Identifica se o usuário atual é professor */
  readonly isProfessor = input<boolean>(false);
  /** Identifica se o usuário atual é estudante */
  readonly isStudent = input<boolean>(false);

  /** Emite o ID da atividade para início da resolução pelo discente */
  readonly resolveActivity = output<string>();
  /** Emite o ID da atividade para tela de correção pelo docente */
  readonly viewSubmissions = output<string>();
  /** Emite o ID da atividade para formulário de edição */
  readonly editActivity = output<string>();
  /** Emite a atividade selecionada para exclusão */
  readonly deleteActivity = output<ActivityResponseDTO>();
}
