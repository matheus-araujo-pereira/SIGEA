import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AcademicClassService } from '../../../../core/services/academic-class.service';
import { UserService } from '../../../../core/services/user.service';
import { ToastService } from '../../../../core/services/toast.service';
import { User, UserRole } from '../../../../core/models/user.model';
import { AcademicClassDetailDTO } from '../../../../core/models/academic-class.model';

/**
 * Modal e formulário para cadastro e edição de Turmas Acadêmicas.
 * Padrão UFS: "Nome da Matéria - Turma - Período" (Ex: Física 3 - T06 - 2026.2).
 */
@Component({
  selector: 'app-class-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './class-form.component.html',
})
export class ClassFormComponent implements OnInit, OnChanges {
  @Input() classId: string | null = null;
  @Input() isOpen = false;
  @Output() close = new EventEmitter<void>();
  @Output() saved = new EventEmitter<void>();

  private readonly fb = inject(FormBuilder);
  private readonly classService = inject(AcademicClassService);
  private readonly userService = inject(UserService);
  private readonly toast = inject(ToastService);

  readonly professors = signal<User[]>([]);
  readonly students = signal<User[]>([]);
  readonly selectedStudentIds = signal<string[]>([]);
  readonly isSaving = signal(false);

  form: FormGroup = this.fb.group({
    subjectName: ['', [Validators.required, Validators.maxLength(120)]],
    classCode: ['', [Validators.required, Validators.maxLength(20)]],
    academicPeriod: ['', [Validators.required, Validators.maxLength(10)]],
    professorId: ['', [Validators.required]],
  });

  get isEditMode(): boolean {
    return !!this.classId;
  }

  ngOnInit(): void {
    // Carregamento postergado para quando o modal for efetivamente aberto
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.loadUsers();
      if (this.classId) {
        this.loadClassDetail(this.classId);
      } else {
        this.resetForm();
      }
    }
  }

  loadUsers(): void {
    this.userService.listUsers(undefined, 'PROFESSOR', true, 0, 100).subscribe({
      next: (res) => this.professors.set(res.data.content),
    });
    this.userService.listUsers(undefined, 'STUDENT', true, 0, 100).subscribe({
      next: (res) => this.students.set(res.data.content),
    });
  }

  loadClassDetail(id: string): void {
    this.classService.getClassById(id).subscribe({
      next: (res) => {
        const c = res.data;
        this.form.patchValue({
          subjectName: c.subjectName,
          classCode: c.classCode,
          academicPeriod: c.academicPeriod,
          professorId: c.professorId,
        });
        this.selectedStudentIds.set(c.students.map((s) => s.id));
      },
      error: () => {
        this.toast.error('Erro ao carregar detalhes da turma.');
        this.onCancel();
      },
    });
  }

  resetForm(): void {
    this.form.reset({
      subjectName: '',
      classCode: '',
      academicPeriod: '',
      professorId: '',
    });
    this.selectedStudentIds.set([]);
  }

  isStudentSelected(id: string): boolean {
    return this.selectedStudentIds().includes(id);
  }

  toggleStudent(id: string): void {
    const current = this.selectedStudentIds();
    if (current.includes(id)) {
      this.selectedStudentIds.set(current.filter((sId) => sId !== id));
    } else {
      this.selectedStudentIds.set([...current, id]);
    }
  }

  previewFormattedName(): string {
    const s = this.form.get('subjectName')?.value || 'Disciplina';
    const c = (this.form.get('classCode')?.value || 'Turma').toUpperCase();
    const p = this.form.get('academicPeriod')?.value || 'Período';
    return `${s} - ${c} - ${p}`;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    const val = this.form.value;

    if (this.isEditMode && this.classId) {
      this.classService
        .updateClass(this.classId, {
          subjectName: val.subjectName,
          classCode: val.classCode,
          academicPeriod: val.academicPeriod,
          professorId: val.professorId,
          studentIds: this.selectedStudentIds(),
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toast.success('Turma acadêmica atualizada com sucesso!');
            this.saved.emit();
          },
          error: (err) => {
            this.isSaving.set(false);
            this.toast.error(err?.error?.message || 'Erro ao atualizar turma.');
          },
        });
    } else {
      this.classService
        .createClass({
          subjectName: val.subjectName,
          classCode: val.classCode,
          academicPeriod: val.academicPeriod,
          professorId: val.professorId,
          studentIds: this.selectedStudentIds(),
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toast.success('Turma acadêmica criada com sucesso!');
            this.saved.emit();
          },
          error: (err) => {
            this.isSaving.set(false);
            this.toast.error(err?.error?.message || 'Erro ao criar turma.');
          },
        });
    }
  }

  onCancel(): void {
    this.resetForm();
    this.close.emit();
  }
}
