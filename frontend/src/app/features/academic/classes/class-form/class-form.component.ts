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
  template: `
    @if (isOpen) {
      <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/50 backdrop-blur-xs animate-fade-in">
        <div class="bg-white rounded-3xl shadow-2xl border border-slate-200/80 w-full max-w-2xl max-h-[90vh] flex flex-col overflow-hidden animate-scale-up">
          <!-- Cabeçalho do Modal -->
          <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50/50">
            <div>
              <h3 class="text-base font-black text-slate-900">
                {{ isEditMode ? 'Editar Turma Acadêmica' : 'Nova Turma Acadêmica' }}
              </h3>
              <p class="text-xs text-slate-500 mt-0.5">
                Formato institucional: Disciplina - Código da Turma - Período Letivo
              </p>
            </div>
            <button
              type="button"
              (click)="onCancel()"
              class="text-slate-400 hover:text-slate-600 p-1.5 rounded-xl hover:bg-slate-100 transition-colors"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Corpo do Formulário -->
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="p-6 overflow-y-auto space-y-5 flex-1 text-xs">
            <!-- Preview do Formato Padronizado -->
            <div class="p-3 bg-clinical-50 border border-clinical-200/80 rounded-2xl flex items-center justify-between">
              <div>
                <span class="text-[10px] font-bold uppercase tracking-wider text-clinical-700 block">Denominação Padronizada</span>
                <span class="text-sm font-black text-clinical-950">
                  {{ previewFormattedName() }}
                </span>
              </div>
              <span class="px-2.5 py-1 text-[10px] font-bold rounded-full bg-clinical-200 text-clinical-900">
                UFS
              </span>
            </div>

            <!-- Dados Principais (3 Colunas) -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <!-- Nome da Disciplina -->
              <div class="md:col-span-1">
                <label class="block font-bold text-slate-700 mb-1.5">
                  Disciplina <span class="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  formControlName="subjectName"
                  placeholder="Ex: Física 3"
                  class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
                @if (form.get('subjectName')?.touched && form.get('subjectName')?.invalid) {
                  <span class="text-rose-600 text-[10px] mt-1 block">Disciplina obrigatória</span>
                }
              </div>

              <!-- Código da Turma -->
              <div>
                <label class="block font-bold text-slate-700 mb-1.5">
                  Turma <span class="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  formControlName="classCode"
                  placeholder="Ex: T06"
                  class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs uppercase focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
                @if (form.get('classCode')?.touched && form.get('classCode')?.invalid) {
                  <span class="text-rose-600 text-[10px] mt-1 block">Código obrigatório</span>
                }
              </div>

              <!-- Período Letivo -->
              <div>
                <label class="block font-bold text-slate-700 mb-1.5">
                  Período Letivo <span class="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  formControlName="academicPeriod"
                  placeholder="Ex: 2026.2"
                  class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
                @if (form.get('academicPeriod')?.touched && form.get('academicPeriod')?.invalid) {
                  <span class="text-rose-600 text-[10px] mt-1 block">Período obrigatório</span>
                }
              </div>
            </div>

            <!-- Professor Titular (Obrigatório) -->
            <div>
              <label class="block font-bold text-slate-700 mb-1.5">
                Professor Titular Responsável <span class="text-rose-500">*</span>
              </label>
              <select
                formControlName="professorId"
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-800 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              >
                <option value="">Selecione exatamente 1 docente titular...</option>
                @for (prof of professors(); track prof.id) {
                  <option [value]="prof.id">{{ prof.fullName }} ({{ prof.email }})</option>
                }
              </select>
              @if (form.get('professorId')?.touched && form.get('professorId')?.invalid) {
                <span class="text-rose-600 text-[10px] mt-1 block">Professor titular é obrigatório</span>
              }
            </div>

            <!-- Matrícula de Alunos -->
            <div>
              <div class="flex items-center justify-between mb-1.5">
                <label class="font-bold text-slate-700">
                  Estudantes Matriculados ({{ selectedStudentIds().length }} selecionados)
                </label>
                <span class="text-[10px] text-slate-400">Validação de e-mail &#64;academico.ufs.br</span>
              </div>
              <div class="border border-slate-200 rounded-2xl max-h-44 overflow-y-auto divide-y divide-slate-100 bg-slate-50/50 p-1">
                @for (student of students(); track student.id) {
                  <label class="flex items-center gap-3 p-2 hover:bg-white rounded-xl cursor-pointer transition-colors">
                    <input
                      type="checkbox"
                      [checked]="isStudentSelected(student.id)"
                      (change)="toggleStudent(student.id)"
                      class="rounded border-slate-300 text-clinical-600 focus:ring-clinical-500 w-3.5 h-3.5"
                    />
                    <div class="flex-1 min-w-0">
                      <p class="font-bold text-slate-900 truncate">{{ student.fullName }}</p>
                      <p class="text-[11px] text-slate-500 font-mono">
                        {{ student.email }} @if (student.registrationNumber) { • Matrícula: {{ student.registrationNumber }} }
                      </p>
                    </div>
                  </label>
                } @empty {
                  <p class="p-3 text-center text-slate-400 text-xs">Nenhum estudante com perfil STUDENT encontrado.</p>
                }
              </div>
            </div>

            <!-- Rodapé com Ações -->
            <div class="pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
              <button
                type="button"
                (click)="onCancel()"
                class="btn-secondary"
              >
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="form.invalid || isSaving()"
                class="btn-primary"
              >
                @if (isSaving()) {
                  <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                  <span>Salvando...</span>
                } @else {
                  <span>{{ isEditMode ? 'Salvar Alterações' : 'Cadastrar Turma' }}</span>
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    }
  `,
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
    this.loadUsers();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
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
