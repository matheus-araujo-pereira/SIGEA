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
  templateUrl: './severity-form.component.html',
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
