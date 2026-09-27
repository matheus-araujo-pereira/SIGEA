import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../../common/services/toast.service';

/**
 * Componente de tela única para redefinição compulsória de senha no primeiro login institucional do SIGEA.
 *
 * Aplica em tempo real a política estrita de complexidade de senha (mínimo 8 caracteres,
 * letras maiúsculas, minúsculas, dígitos numéricos e caracteres especiais).
 */
@Component({
  selector: 'app-first-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './first-login.component.html',
})
export class FirstLoginComponent {
  /** Construtor reativo de formulários */
  private readonly fb = inject(FormBuilder);
  /** Serviço de autenticação e sessão */
  private readonly authService = inject(AuthService);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Serviço de notificações de toast */
  private readonly toastService = inject(ToastService);

  /**
   * Signal reativo indicando submissão assíncrona da troca de senha.
   */
  readonly isSubmitting = signal<boolean>(false);

  /**
   * Formulário reativo para validação da senha atual e nova senha pessoal.
   */
  readonly form = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  /**
   * Retorna o valor textual digitado no controle de nova senha.
   */
  get newPasswordVal(): string {
    return this.form.get('newPassword')?.value || '';
  }

  /**
   * Verifica se a senha atende ao critério de comprimento mínimo de 8 caracteres.
   *
   * @returns Booleano.
   */
  hasMinLength(): boolean {
    return this.newPasswordVal.length >= 8;
  }

  /**
   * Verifica se a senha possui pelo menos uma letra maiúscula.
   *
   * @returns Booleano.
   */
  hasUpper(): boolean {
    return /[A-Z]/.test(this.newPasswordVal);
  }

  /**
   * Verifica se a senha possui pelo menos uma letra minúscula.
   *
   * @returns Booleano.
   */
  hasLower(): boolean {
    return /[a-z]/.test(this.newPasswordVal);
  }

  /**
   * Verifica se a senha possui pelo menos um dígito numérico.
   *
   * @returns Booleano.
   */
  hasDigit(): boolean {
    return /\d/.test(this.newPasswordVal);
  }

  /**
   * Verifica se a senha possui pelo menos um caractere especial (@, $, !, %, *, ?, &).
   *
   * @returns Booleano.
   */
  hasSpecial(): boolean {
    return /[@$!%*?&]/.test(this.newPasswordVal);
  }

  /**
   * Valida se todos os 5 critérios de segurança da política de senhas foram preenchidos.
   *
   * @returns Booleano indicando conformidade integral.
   */
  isPasswordPolicyMet(): boolean {
    return this.hasMinLength() && this.hasUpper() && this.hasLower() && this.hasDigit() && this.hasSpecial();
  }

  /**
   * Verifica se houve divergência entre a nova senha e a confirmação digitada.
   *
   * @returns Booleano indicando incompatibilidade.
   */
  passwordsDoNotMatch(): boolean {
    const newPwd = this.form.get('newPassword')?.value;
    const confirmPwd = this.form.get('confirmPassword')?.value;
    return !!(confirmPwd && newPwd && newPwd !== confirmPwd);
  }

  /**
   * Submete a nova senha definitiva para gravação no backend e redireciona para a home do perfil.
   *
   * @returns void
   */
  onSubmit(): void {
    if (this.form.invalid || !this.isPasswordPolicyMet() || this.passwordsDoNotMatch()) {
      return;
    }

    this.isSubmitting.set(true);
    const { currentPassword, newPassword, confirmPassword } = this.form.getRawValue();

    this.authService
      .firstLoginChangePassword({
        currentPassword: currentPassword!,
        newPassword: newPassword!,
        confirmPassword: confirmPassword!,
      })
      .subscribe({
        next: (response) => {
          this.isSubmitting.set(false);
          this.toastService.success('Sucesso!', 'Senha alterada com sucesso.');
          if (response.data.user.role === 'ADMIN') {
            this.router.navigate(['/users']);
          } else {
            this.router.navigate(['/profile']);
          }
        },
        error: () => {
          this.isSubmitting.set(false);
        },
      });
  }
}
