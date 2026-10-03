/**
 * @file severity-form.component.ts
 * @description Modal de cadastro e edição de Categorias de Gravidade de Dano (NCC MERP adaptado pelo IHI).
 * @module SeverityFormComponent
 */

import { Component, HostListener, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HarmSeverityService } from '../../services/harm-severity.service';
import { ToastService } from '../../../../common/services/toast.service';
import { HarmSeverity } from '../../models/harm-severity.model';

/**
 * Modal reativo para parametrização cadastral de categorias NCC MERP (A a I).
 */
@Component({
  selector: 'app-severity-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './severity-form.component.html',
})
export class SeverityFormComponent implements OnChanges {
  /**
   * Listener global de teclado para fechar o diálogo via tecla Escape.
   */
  @HostListener('document:keydown.escape')
  handleEscape(): void {
    if (this.isOpen()) {
      this.onClose();
    }
  }

  /** Fábrica de formulários reativos */
  private readonly fb = inject(FormBuilder);
  /** Serviço de comunicação com API de gravidades */
  private readonly severityService = inject(HarmSeverityService);
  /** Serviço de notificações Toast */
  private readonly toastService = inject(ToastService);

  /** Sinal indicando se o modal está aberto */
  readonly isOpen = input<boolean>(false);
  /** Gravidade em edição (null se for criação) */
  readonly severity = input<HarmSeverity | null>(null);

  /** Evento emitido após salvar */
  readonly saved = output<void>();
  /** Evento emitido ao fechar/cancelar */
  readonly closed = output<void>();

  /** Indicador de salvamento em andamento */
  readonly isSaving = signal<boolean>(false);

  /** Formulário reativo de gravidade */
  readonly form = this.fb.group({
    categoryLetter: ['', [Validators.required, Validators.maxLength(5)]],
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
    description: ['', [Validators.required]],
    isHarm: [false],
  });

  /**
   * Atualiza os campos do formulário quando a gravidade ou status de abertura se alteram.
   *
   * @param changes Mudanças de input detectadas pelo Angular
   */
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

  /**
   * Verifica se está no modo de edição de registro já existente.
   *
   * @returns true se há gravidade selecionada
   */
  isEditing(): boolean {
    return !!this.severity();
  }

  /**
   * Notifica fechamento do modal.
   */
  onClose(): void {
    this.closed.emit();
  }

  /**
   * Submete o formulário para cadastro ou atualização da categoria de gravidade.
   */
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
