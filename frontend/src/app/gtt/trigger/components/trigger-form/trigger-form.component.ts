/**
 * @file trigger-form.component.ts
 * @description Modal de cadastro e edição de Gatilhos Clínicos do IHI Global Trigger Tool (IHI-GTT).
 * @module TriggerFormComponent
 */

import { Component, HostListener, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { GttTriggerService } from '../../services/gtt-trigger.service';
import { ToastService } from '../../../../common/services/toast.service';
import { GttTrigger } from '../../models/gtt-trigger.model';
import { GttModule } from '../../../module/models/gtt-module.model';

/**
 * Modal reativo para cadastro e atualização cadastral de gatilhos clínicos vinculados a módulos assistenciais.
 */
@Component({
  selector: 'app-trigger-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './trigger-form.component.html',
})
export class TriggerFormComponent implements OnChanges {
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
  /** Serviço de comunicação com API de gatilhos */
  private readonly triggerService = inject(GttTriggerService);
  /** Serviço de notificações visuais */
  private readonly toastService = inject(ToastService);

  /** Sinal indicando se o modal está aberto */
  readonly isOpen = input<boolean>(false);
  /** Gatilho em edição (null se for criação) */
  readonly trigger = input<GttTrigger | null>(null);
  /** Lista de módulos ativos para seleção no formulário */
  readonly modules = input<GttModule[]>([]);

  /** Evento emitido após salvar com sucesso */
  readonly saved = output<void>();
  /** Evento emitido ao fechar/cancelar o modal */
  readonly closed = output<void>();

  /** Indicador de operação de salvamento em andamento */
  readonly isSaving = signal<boolean>(false);

  /** Formulário reativo de dados cadastrais do gatilho */
  readonly form = this.fb.group({
    moduleId: ['', [Validators.required]],
    code: ['', [Validators.required, Validators.maxLength(10)]],
    name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(150)]],
    description: ['', [Validators.required]],
  });

  /**
   * Atualiza os campos do formulário ao detectar alteração no gatilho ou visibilidade.
   *
   * @param changes Mudanças de input detectadas pelo Angular
   */
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

  /**
   * Verifica se o modal está operando em modo de edição.
   *
   * @returns true se há gatilho definido
   */
  isEditing(): boolean {
    return !!this.trigger();
  }

  /**
   * Notifica fechamento do modal.
   */
  onClose(): void {
    this.closed.emit();
  }

  /**
   * Submete os dados do formulário para criação ou atualização de gatilho clínico.
   */
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
