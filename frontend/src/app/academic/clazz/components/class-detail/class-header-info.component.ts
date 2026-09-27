import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AcademicClassDetailDTO } from '../../models/academic-class.model';

/**
 * Componente atômico para exibição do cabeçalho da turma, dados do docente,
 * métricas gerais e botões de ação (Boletim, CSV, Painel Analítico, Nova Atividade).
 */
@Component({
  selector: 'app-class-header-info',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './class-header-info.component.html',
})
export class ClassHeaderInfoComponent {
  /** Dados completos de detalhes da turma */
  readonly classData = input.required<AcademicClassDetailDTO>();
  /** Flag indicando se o usuário logado é administrador */
  readonly isAdmin = input<boolean>(false);
  /** Flag indicando se o usuário logado é professor */
  readonly isProfessor = input<boolean>(false);
  /** Quantidade total de atividades vinculadas à turma */
  readonly activitiesCount = input<number>(0);

  /** Emite evento para retorno à lista de turmas */
  readonly goBack = output<void>();
  /** Emite solicitação de download do boletim em PDF */
  readonly downloadBulletin = output<void>();
  /** Emite solicitação de exportação de dados em CSV */
  readonly downloadResearch = output<void>();
  /** Emite solicitação de abertura do painel analítico GTT */
  readonly viewDashboard = output<void>();
  /** Emite solicitação de cadastro de nova atividade */
  readonly createNewActivity = output<void>();
}
