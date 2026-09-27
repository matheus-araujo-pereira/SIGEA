import { Component, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { GttModuleService } from '../../../../core/services/gtt-module.service';
import { ToastService } from '../../../../core/services/toast.service';
import { GttModule } from '../../../../core/models/gtt-module.model';

/**
 * Modal para cadastro e edição de Módulos do IHI-GTT.
 */
@Component({
  selector: 'app-module-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './module-form.component.html',
})
export class ModuleFormComponent implements OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly moduleService = inject(GttModuleService);
  private readonly toastService = inject(ToastService);

  readonly isOpen = input<boolean>(false);
  readonly module = input<GttModule | null>(null);

  readonly saved = output<void>();
  readonly closed = output<void>();

  readonly isSaving = signal<boolean>(false);

  readonly form = this.fb.group({
    code: ['', [Validators.required, Validators.maxLength(10)]],
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    description: [''],
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['module'] || changes['isOpen']) {
      if (this.isOpen()) {
        const current = this.module();
        if (current) {
          this.form.patchValue({
            code: current.code,
            name: current.name,
            description: current.description || '',
          });
        } else {
          this.form.reset({
            code: '',
            name: '',
            description: '',
          });
        }
      }
    }
  }

  isEditing(): boolean {
    return !!this.module();
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
      const id = this.module()!.id;
      this.moduleService
        .updateModule(id, {
          code: raw.code!,
          name: raw.name!,
          description: raw.description,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toastService.success('Sucesso', 'Módulo GTT atualizado com sucesso.');
            this.saved.emit();
          },
          error: () => this.isSaving.set(false),
        });
    } else {
      this.moduleService
        .createModule({
          code: raw.code!,
          name: raw.name!,
          description: raw.description,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toastService.success('Sucesso', 'Módulo GTT cadastrado com sucesso.');
            this.saved.emit();
          },
          error: () => this.isSaving.set(false),
        });
    }
  }
}
