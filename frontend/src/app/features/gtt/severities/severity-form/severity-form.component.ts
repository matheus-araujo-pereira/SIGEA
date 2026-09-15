import { Component, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { ToastService } from '../../../../core/services/toast.service';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';

/**
 * Modal para cadastro e edição de Categorias de Gravidade de Dano (NCC MERP).
 */
@Component({
  selector: 'app-severity-form-modal',
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
                {{ isEditing() ? 'Editar Categoria de Gravidade' : 'Nova Categoria de Gravidade (NCC MERP)' }}
              </h3>
              <p class="text-xs text-slate-500 mt-0.5">
                {{ isEditing() ? 'Atualize as definições e o enquadramento de dano' : 'Cadastre uma classificação de severidade de A a I' }}
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
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <!-- Letra da Categoria -->
              <div>
                <label for="categoryLetter" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                  Letra <span class="text-rose-600">*</span>
                </label>
                <input
                  id="categoryLetter"
                  type="text"
                  formControlName="categoryLetter"
                  placeholder="Ex: E"
                  maxlength="5"
                  class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm uppercase text-center font-bold focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
              </div>

              <!-- Flag de Dano Real (isHarm) -->
              <div class="sm:col-span-2 flex items-center pt-5">
                <label class="relative flex items-start gap-3 cursor-pointer select-none">
                  <input
                    type="checkbox"
                    formControlName="isHarm"
                    class="mt-1 w-4 h-4 text-clinical-600 rounded border-slate-300 focus:ring-clinical-500"
                  />
                  <div>
                    <span class="text-xs font-bold text-slate-800 uppercase tracking-wider block">
                      Classificar como Dano Real?
                    </span>
                    <span class="text-[11px] text-slate-500 block">
                      Marcar para categorias com dano mensurável ao paciente (E a I).
                    </span>
                  </div>
                </label>
              </div>
            </div>

            <!-- Nome / Título da Gravidade -->
            <div>
              <label for="name" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Nome da Categoria <span class="text-rose-600">*</span>
              </label>
              <input
                id="name"
                type="text"
                formControlName="name"
                placeholder="Ex: Dano temporário com necessidade de intervenção"
                class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <!-- Descrição e Definição Oficial NCC MERP -->
            <div>
              <label for="description" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Definição Oficial NCC MERP <span class="text-rose-600">*</span>
              </label>
              <textarea
                id="description"
                rows="4"
                formControlName="description"
                placeholder="Descreva a definição técnica da categoria de dano e critérios de enquadramento..."
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
                {{ isSaving() ? 'Salvando...' : (isEditing() ? 'Atualizar Categoria' : 'Cadastrar Categoria') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    }
  `,
})
export class SeverityFormComponent implements OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly severityService = inject(HarmSeverityService);
  private readonly toastService = inject(ToastService);

  readonly isOpen = input<boolean>(false);
  readonly severity = input<HarmSeverity | null>(null);

  readonly saved = output<void>();
  readonly closed = output<void>();

  readonly isSaving = signal<boolean>(false);

  readonly form = this.fb.group({
    categoryLetter: ['', [Validators.required, Validators.maxLength(5)]],
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
    description: ['', [Validators.required]],
    isHarm: [false],
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['severity'] || changes['isOpen']) {
      if (this.isOpen()) {
        const current = this.severity();
        if (current) {
          this.form.patchValue({
            categoryLetter: current.categoryLetter,
            name: current.name,
            description: current.description,
            isHarm: current.isHarm,
          });
        } else {
          this.form.reset({
            categoryLetter: '',
            name: '',
            description: '',
            isHarm: false,
          });
        }
      }
    }
  }

  isEditing(): boolean {
    return !!this.severity();
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
      const id = this.severity()!.id;
      this.severityService
        .updateSeverity(id, {
          categoryLetter: raw.categoryLetter!.toUpperCase().trim(),
          name: raw.name!.trim(),
          description: raw.description!.trim(),
          isHarm: !!raw.isHarm,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toastService.success('Sucesso', 'Categoria de gravidade atualizada com sucesso.');
            this.saved.emit();
          },
          error: () => this.isSaving.set(false),
        });
    } else {
      this.severityService
        .createSeverity({
          categoryLetter: raw.categoryLetter!.toUpperCase().trim(),
          name: raw.name!.trim(),
          description: raw.description!.trim(),
          isHarm: !!raw.isHarm,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toastService.success('Sucesso', 'Categoria de gravidade cadastrada com sucesso.');
            this.saved.emit();
          },
          error: () => this.isSaving.set(false),
        });
    }
  }
}
