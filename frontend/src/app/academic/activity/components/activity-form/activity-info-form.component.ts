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
  template: `
    <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4" [formGroup]="form">
      <div class="flex items-center gap-2 border-b border-slate-100 pb-3">
        <span class="w-6 h-6 rounded-lg bg-clinical-100 text-clinical-700 flex items-center justify-center font-bold text-xs">1</span>
        <h3 class="text-sm font-black text-slate-900">Informações da Atividade</h3>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <!-- Turma Acadêmica -->
        @if (!isEditMode) {
          <div class="md:col-span-1">
            <label class="block font-bold text-slate-700 mb-1.5 text-xs">
              Turma <span class="text-rose-500">*</span>
            </label>
            <select
              formControlName="classId"
              class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-800 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
            >
              <option value="">Selecione a turma...</option>
              @for (c of classes; track c.id) {
                <option [value]="c.id">{{ c.formattedName }}</option>
              }
            </select>
            @if (form.get('classId')?.touched && form.get('classId')?.invalid) {
              <span class="text-rose-600 text-[10px] mt-1 block">Selecione uma turma</span>
            }
          </div>
        }

        <!-- Título da Atividade -->
        <div [class.md:col-span-2]="!isEditMode" [class.md:col-span-2]="isEditMode">
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">
            Título da Atividade <span class="text-rose-500">*</span>
          </label>
          <input
            type="text"
            formControlName="title"
            placeholder="Ex: Caso Clínico 1 - Evento Adverso em Terapia Medicamentosa"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
          />
          @if (form.get('title')?.touched && form.get('title')?.invalid) {
            <span class="text-rose-600 text-[10px] mt-1 block">Título obrigatório</span>
          }
        </div>

        <!-- Prazo Limite -->
        <div>
          <label class="block font-bold text-slate-700 mb-1.5 text-xs">
            Prazo de Entrega (Data e Hora) <span class="text-rose-500">*</span>
          </label>
          <input
            type="datetime-local"
            formControlName="deadline"
            class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 font-mono"
          />
          @if (form.get('deadline')?.touched && form.get('deadline')?.invalid) {
            <span class="text-rose-600 text-[10px] mt-1 block">Informe uma data futura</span>
          }
        </div>
      </div>

      <!-- Orientações Pedagógicas -->
      <div>
        <label class="block font-bold text-slate-700 mb-1.5 text-xs">
          Orientações para os Alunos <span class="text-rose-500">*</span>
        </label>
        <textarea
          rows="3"
          formControlName="description"
          placeholder="Instruções aos discentes para revisão do prontuário, identificação de gatilhos IHI e aplicação das ferramentas da qualidade..."
          class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 resize-y"
        ></textarea>
        @if (form.get('description')?.touched && form.get('description')?.invalid) {
          <span class="text-rose-600 text-[10px] mt-1 block">Orientações obrigatórias</span>
        }
      </div>
    </div>
  `
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
