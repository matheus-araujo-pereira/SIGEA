import { Component, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { GttModule } from '../../../../core/models/gtt-module.model';

/**
 * Modal para cadastro e edição de Gatilhos (Triggers) do IHI-GTT.
 */
@Component({
  selector: 'app-trigger-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    @if (isOpen()) {
      <div
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm transition-opacity"
        role="dialog"
        aria-modal="true"
      >
        <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-100 animate-in fade-in zoom-in-95 duration-200">
          <!-- Cabeçalho -->
          <div class="flex items-center justify-between pb-4 border-b border-slate-100">
            <div>
              <h3 class="text-lg font-bold text-slate-900">
                {{ isEditing() ? 'Editar Gatilho Clínico' : 'Novo Gatilho IHI-GTT' }}
              </h3>
              <p class="text-xs text-slate-500 mt-0.5">
                {{ isEditing() ? 'Atualize as diretrizes de investigação do gatilho' : 'Cadastre um novo gatilho com orientações clínicas' }}
              </p>
            </div>
            <button
              type="button"
              (click)="onClose()"
              class="text-slate-400 hover:text-slate-700 p-1.5 rounded-lg hover:bg-slate-100"
              aria-label="Fechar"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Formulário -->
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="mt-5 space-y-4">
            <!-- Seleção do Módulo Pai -->
            <div>
              <label for="moduleId" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Módulo Assistencial Vinculado <span class="text-rose-600">*</span>
              </label>
              <select
                id="moduleId"
                formControlName="moduleId"
                class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              >
                <option value="" disabled>Selecione o módulo...</option>
                @for (mod of modules(); track mod.id) {
                  <option [value]="mod.id">
                    {{ mod.code }} - {{ mod.name }}
                  </option>
                }
              </select>
            </div>

            <!-- Código do Gatilho -->
            <div>
              <label for="code" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Código do Gatilho <span class="text-rose-600">*</span>
              </label>
              <input
                id="code"
                type="text"
                formControlName="code"
                placeholder="Ex: C1, M4, S10, I2, P4, E1"
                class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm uppercase focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <!-- Nome do Gatilho -->
            <div>
              <label for="name" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Nome do Gatilho Clínico <span class="text-rose-600">*</span>
              </label>
              <input
                id="name"
                type="text"
                formControlName="name"
                placeholder="Ex: Transfusão de sangue, hemocomponentes ou hemoderivados"
                class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <!-- Descrição e Diretrizes Clínicas -->
            <div>
              <label for="description" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Diretrizes de Investigação Clínica <span class="text-rose-600">*</span>
              </label>
              <textarea
                id="description"
                rows="4"
                formControlName="description"
                placeholder="Detalhes sobre como investigar este gatilho no prontuário, causas e critérios diagnósticos..."
                class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              ></textarea>
            </div>

            <!-- Botões de Ação -->
            <div class="mt-6 pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
              <button type="button" (click)="onClose()" class="btn-secondary">
                Cancelar
              </button>
              <button
                type="submit"
                [disabled]="form.invalid || isSaving()"
                class="btn-primary"
              >
                {{ isSaving() ? 'Salvando...' : (isEditing() ? 'Atualizar Gatilho' : 'Cadastrar Gatilho') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    }
  `,
})
export class TriggerFormComponent implements OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly triggerService = inject(GttTriggerService);
  private readonly toastService = inject(ToastService);

  readonly isOpen = input<boolean>(false);
  readonly trigger = input<GttTrigger | null>(null);
  readonly modules = input<GttModule[]>([]);

  readonly saved = output<void>();
  readonly closed = output<void>();

  readonly isSaving = signal<boolean>(false);

  readonly form = this.fb.group({
    moduleId: ['', [Validators.required]],
    code: ['', [Validators.required, Validators.maxLength(10)]],
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
    description: ['', [Validators.required]],
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['trigger'] || changes['isOpen']) {
      if (this.isOpen()) {
        const current = this.trigger();
        if (current) {
          this.form.patchValue({
            moduleId: current.moduleId,
            code: current.code,
            name: current.name,
            description: current.description,
          });
        } else {
          this.form.reset({
            moduleId: '',
            code: '',
            name: '',
            description: '',
          });
        }
      }
    }
  }

  isEditing(): boolean {
    return !!this.trigger();
  }

  onClose(): void {
    this.closed.emit();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    const raw = this.form.getRawValue();

    if (this.isEditing()) {
      const id = this.trigger()!.id;
      this.triggerService
        .updateTrigger(id, {
          moduleId: raw.moduleId!,
          code: raw.code!,
          name: raw.name!,
          description: raw.description!,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toastService.success('Sucesso', 'Gatilho GTT atualizado com sucesso.');
            this.saved.emit();
          },
          error: () => this.isSaving.set(false),
        });
    } else {
      this.triggerService
        .createTrigger({
          moduleId: raw.moduleId!,
          code: raw.code!,
          name: raw.name!,
          description: raw.description!,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toastService.success('Sucesso', 'Gatilho GTT cadastrado com sucesso.');
            this.saved.emit();
          },
          error: () => this.isSaving.set(false),
        });
    }
  }
}
