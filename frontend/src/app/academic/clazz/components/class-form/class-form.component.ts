/**
 * @file class-form.component.ts
 * @description Modal e formulário reativo para cadastro e edição de Turmas Acadêmicas (Padrão UFS: "Nome da Matéria - Turma - Período").
 * @module ClassFormComponent
 */

import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AcademicClassService } from '../../services/academic-class.service';
import { UserService } from '../../../../user/services/user.service';
import { ToastService } from '../../../../common/services/toast.service';
import { User } from '../../../../user/models/user.model';
import { AcademicClassDetailDTO } from '../../models/academic-class.model';

/**
 * Modal e formulário para cadastro e edição de Turmas Acadêmicas.
 */
@Component({
  selector: 'app-class-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './class-form.component.html',
})
export class ClassFormComponent implements OnInit, OnChanges {
  /** ID da turma para edição (null se cadastro) */
  @Input() classId: string | null = null;
  /** Visibilidade do modal */
  @Input() isOpen = false;
  /** Evento emitido ao cancelar/fechar */
  @Output() close = new EventEmitter<void>();
  /** Evento emitido após salvar com sucesso */
  @Output() saved = new EventEmitter<void>();

  /** Fábrica de formulários reativos */
  private readonly fb = inject(FormBuilder);
  /** Serviço de turmas */
  private readonly classService = inject(AcademicClassService);
  /** Serviço de usuários */
  private readonly userService = inject(UserService);
  /** Serviço de notificações Toast */
  private readonly toast = inject(ToastService);

  /** Docentes ativos para seleção */
  readonly professors = signal<User[]>([]);
  /** Estudantes ativos disponíveis para matrícula */
  readonly students = signal<User[]>([]);
  /** IDs dos estudantes selecionados na turma */
  readonly selectedStudentIds = signal<string[]>([]);
  /** Indicador de salvamento em andamento */
  readonly isSaving = signal(false);

  /** Formulário reativo de dados da turma */
  form: FormGroup = this.fb.group({
    subjectName: ['', [Validators.required, Validators.maxLength(120)]],
    classCode: ['', [Validators.required, Validators.maxLength(20)]],
    academicPeriod: ['', [Validators.required, Validators.maxLength(10)]],
    professorId: ['', [Validators.required]],
  });

  /** Identifica se o formulário está no modo de edição */
  get isEditMode(): boolean {
    return !!this.classId;
  }

  /**
   * Ciclo de inicialização.
   */
  ngOnInit(): void {
    // Carregamento postergado para quando o modal for aberto
  }

  /**
   * Monitora abertura do modal e ID da turma para popular dados.
   *
   * @param changes Mudanças de input
   */
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

  /**
   * Carrega as listas de docentes e estudantes ativos.
   */
  loadUsers(): void {
    this.userService.listUsers(undefined, 'PROFESSOR', true, 0, 100).subscribe({
      next: (res) => this.professors.set(res.data.content),
    });
    this.userService.listUsers(undefined, 'STUDENT', true, 0, 100).subscribe({
      next: (res) => this.students.set(res.data.content),
    });
  }

  /**
   * Carrega os dados da turma para edição.
   *
   * @param id UUID da turma
   */
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

  /**
   * Restaura o formulário ao estado inicial.
   */
  resetForm(): void {
    this.form.reset({
      subjectName: '',
      classCode: '',
      academicPeriod: '',
      professorId: '',
    });
    this.selectedStudentIds.set([]);
  }

  /**
   * Verifica se determinado estudante está selecionado na lista de matrículas.
   *
   * @param id UUID do estudante
   * @returns true se selecionado
   */
  isStudentSelected(id: string): boolean {
    return this.selectedStudentIds().includes(id);
  }

  /**
   * Alterna a seleção de matrícula de um estudante.
   *
   * @param id UUID do estudante
   */
  toggleStudent(id: string): void {
    const current = this.selectedStudentIds();
    if (current.includes(id)) {
      this.selectedStudentIds.set(current.filter((sId) => sId !== id));
    } else {
      this.selectedStudentIds.set([...current, id]);
    }
  }

  /**
   * Gera uma prévia do nome formatado canônico institucional.
   *
   * @returns String formatada no padrão UFS
   */
  previewFormattedName(): string {
    const s = this.form.get('subjectName')?.value || 'Disciplina';
    const c = (this.form.get('classCode')?.value || 'Turma').toUpperCase();
    const p = this.form.get('academicPeriod')?.value || 'Período';
    return `${s} - ${c} - ${p}`;
  }

  /**
   * Submete os dados para criação ou atualização de turma acadêmica.
   */
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

  /**
   * Fecha o formulário e cancela operação.
   */
  onCancel(): void {
    this.resetForm();
    this.close.emit();
  }
}
