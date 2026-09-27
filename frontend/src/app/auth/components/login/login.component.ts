import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ToastService } from '../../../common/services/toast.service';
import { ufsEmailValidator } from '../../../common/validators/ufs-email.validator';

/**
 * Componente de tela única para autenticação de usuários no SIGEA.
 *
 * Oferece validação em tempo real de domínio institucional (`@academico.ufs.br`),
 * alternância de visibilidade de senha e feedback ergonômico com semiótica hospitalar.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  /** Construtor reativo de formulários */
  private readonly fb = inject(FormBuilder);
  /** Serviço de autenticação JWT e RBAC */
  private readonly authService = inject(AuthService);
  /** Serviço de navegação e roteamento SPA */
  private readonly router = inject(Router);
  /** Serviço de mensagens toast */
  private readonly toastService = inject(ToastService);

  /**
   * Signal reativo indicando se a requisição de login está em processamento assíncrono.
   */
  readonly isSubmitting = signal<boolean>(false);

  /**
   * Signal reativo controlando a alternância entre texto claro e mascarado para a senha.
   */
  readonly showPassword = signal<boolean>(false);

  /**
   * Formulário reativo com validação estrita de e-mail institucional UFS e obrigatoriedade.
   */
  readonly form = this.fb.group({
    email: ['', [Validators.required, ufsEmailValidator()]],
    password: ['', [Validators.required]],
  });

  /**
   * Verifica se o campo de e-mail possui erro de domínio não-institucional após interação.
   *
   * @returns Booleano indicando presença de erro específico de e-mail UFS.
   */
  hasEmailError(): boolean {
    const control = this.form.get('email');
    return !!(control && control.dirty && control.hasError('ufsEmail'));
  }

  /**
   * Submete as credenciais para autenticação e redireciona de acordo com o estado do usuário
   * (Primeiro acesso `/first-login`, ADMIN `/users` ou Usuário regular `/profile`).
   *
   * @returns void
   */
  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const { email, password } = this.form.getRawValue();

    this.authService.login({ email: email!, password: password! }).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        this.toastService.success('Bem-vindo!', response.message || 'Login efetuado com sucesso.');

        if (response.data.user.mustChangePassword) {
          this.router.navigate(['/first-login']);
        } else if (response.data.user.role === 'ADMIN') {
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
