import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

/**
 * Validador síncrono para e-mail institucional @academico.ufs.br.
 */
function ufsEmailValidator(control: { value: string | null }) {
  const value = control.value?.trim().toLowerCase() || '';
  if (!value) return null;
  const valid = /^[a-zA-Z0-9._%+-]+@academico\.ufs\.br$/.test(value);
  return valid ? null : { invalidUfsEmail: true };
}

/**
 * Tela de login do SIGEA com validação em tempo real de e-mail institucional.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);

  readonly isSubmitting = signal<boolean>(false);
  readonly showPassword = signal<boolean>(false);

  readonly form = this.fb.group({
    email: ['', [Validators.required, ufsEmailValidator]],
    password: ['', [Validators.required]],
  });

  hasEmailError(): boolean {
    const control = this.form.get('email');
    return !!(control && control.dirty && control.hasError('invalidUfsEmail'));
  }

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
