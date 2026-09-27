/**
 * @file module-form.component.ts
 * @description Modal para cadastro e edição cadastral de Módulos do IHI Global Trigger Tool (IHI-GTT).
 * @module ModuleFormComponent
 */

import { Component, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { GttModuleService } from '../../services/gtt-module.service';
import { ToastService } from '../../../../common/services/toast.service';
import { GttModule } from '../../models/gtt-module.model';

/**
 * Modal reativo para criação e edição de Módulos GTT com validações estritas.
 */
@Component({
  selector: 'app-module-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './module-form.component.html',
})
export class ModuleFormComponent implements OnChanges {
  /** Fábrica de formulários reativos */
  private readonly fb = inject(FormBuilder);
  /** Serviço de comunicação com API de módulos GTT */
  private readonly moduleService = inject(GttModuleService);
  /** Serviço de notificações visuais */
  private readonly toastService = inject(ToastService);

  /** Sinal de entrada indicando se o modal está aberto */
  readonly isOpen = input<boolean>(false);
  /** Módulo em edição (null se for criação) */
  readonly module = input<GttModule | null>(null);

  /** Evento emitido após salvamento bem-sucedido */
  readonly saved = output<void>();
  /** Evento emitido ao fechar/cancelar o formulário */
  readonly closed = output<void>();

  /** Indicador de operação de salvamento em andamento */
  readonly isSaving = signal<boolean>(false);

  /** Formulário reativo de dados cadastrais do módulo */
  readonly form = this.fb.group({
    code: ['', [Validators.required, Validators.maxLength(10)]],
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    description: [''],
  });

  /**
   * Trata alterações nas propriedades de entrada do componente, populando ou resetando o formulário.
   *
   * @param changes Objeto de mudanças detectadas pelo Angular
   */
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

  /**
   * Verifica se o modal está em modo de edição de registro existente.
   *
   * @returns true se há módulo selecionado
   */
  isEditing(): boolean {
    return !!this.module();
  }

  /**
   * Notifica fechamento do modal.
   */
  onClose(): void {
    this.closed.emit();
  }

  /**
   * Submete os dados do formulário para criação ou atualização de módulo.
   */
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
