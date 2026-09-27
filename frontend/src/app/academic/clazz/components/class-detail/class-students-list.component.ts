import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AcademicStudentSummaryDTO } from '../../models/academic-class.model';

/**
 * Componente atômico para listagem dos estudantes matriculados em uma turma acadêmica.
 */
@Component({
  selector: 'app-class-students-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './class-students-list.component.html',
})
export class ClassStudentsListComponent {
  /** Coleção de estudantes matriculados na turma acadêmica */
  readonly students = input.required<AcademicStudentSummaryDTO[]>();
}
