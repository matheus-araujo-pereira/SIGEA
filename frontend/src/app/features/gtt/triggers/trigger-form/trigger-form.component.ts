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
  templateUrl: './trigger-form.component.html',
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
