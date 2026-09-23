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
  template: `
    <div class="min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 bg-gradient-to-br from-slate-900 via-clinical-950 to-slate-900">
      <div class="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <!-- Logo Institucional -->
        <div class="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-tr from-clinical-600 to-blue-400 shadow-xl shadow-blue-500/20 mb-4">
          <span class="text-white font-black text-2xl tracking-tighter">S</span>
        </div>
        <h2 class="text-2xl font-extrabold tracking-tight text-white sm:text-3xl">SIGEA</h2>
        <p class="mt-1.5 text-xs text-blue-200 uppercase tracking-widest font-medium">
          Universidade Federal de Sergipe • DCOMP / Enfermagem
        </p>
        <p class="mt-2 text-sm text-slate-400">
          Sistema Inteligente de Gestão de Eventos Adversos
        </p>
      </div>

      <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0">
        <div class="bg-white py-8 px-6 shadow-2xl rounded-2xl sm:px-10 border border-slate-100">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
            <!-- Campo E-mail Institucional -->
            <div>
              <label for="email" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                E-mail Institucional
              </label>
              <div class="mt-1.5 relative rounded-lg shadow-xs">
                <input
                  id="email"
                  type="email"
                  formControlName="email"
                  placeholder="usuario@academico.ufs.br"
                  class="block w-full px-3.5 py-2.5 bg-slate-50 border rounded-lg text-sm transition-colors focus:bg-white focus:outline-none focus:ring-2"
                  [ngClass]="
                    hasEmailError()
                      ? 'border-rose-300 focus:ring-rose-500 focus:border-rose-500'
                      : 'border-slate-300 focus:ring-clinical-500 focus:border-clinical-500'
                  "
                />
              </div>

              <!-- Alerta em Tempo Real de Domínio Inválido -->
              @if (hasEmailError()) {
                <div class="mt-2 p-2.5 bg-rose-50 border border-rose-200 rounded-lg flex items-start gap-2">
                  <svg class="w-4 h-4 text-rose-600 flex-shrink-0 mt-0.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                  </svg>
                  <p class="text-xs text-rose-700 font-medium leading-tight">
                    Apenas e-mails institucionais <span class="font-bold">&#64;academico.ufs.br</span> são permitidos.
                  </p>
                </div>
              }
            </div>

            <!-- Campo Senha -->
            <div>
              <label for="password" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Senha
              </label>
              <div class="mt-1.5 relative rounded-lg shadow-xs">
                <input
                  id="password"
                  [type]="showPassword() ? 'text' : 'password'"
                  formControlName="password"
                  placeholder="••••••••"
                  class="block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm transition-colors focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500 focus:border-clinical-500 pr-10"
                />
                <button
                  type="button"
                  (click)="showPassword.set(!showPassword())"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-400 hover:text-slate-600"
                  aria-label="Alternar visibilidade da senha"
                >
                  @if (showPassword()) {
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
                    </svg>
                  } @else {
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    </svg>
                  }
                </button>
              </div>
            </div>

            <!-- Botão de Acesso -->
            <div class="pt-2">
              <button
                type="submit"
                [disabled]="form.invalid || isSubmitting()"
                class="w-full btn-primary py-3 text-sm font-semibold tracking-wide shadow-md shadow-blue-500/20"
              >
                @if (isSubmitting()) {
                  <span class="flex items-center gap-2">
                    <svg class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                    </svg>
                    Acessando...
                  </span>
                } @else {
                  <span>Acessar SIGEA</span>
                }
              </button>
            </div>
          </form>

          <!-- Informações de Apoio -->
          <div class="mt-6 pt-6 border-t border-slate-100 text-center">
            <p class="text-xs text-slate-500 leading-relaxed">
              Primeiro acesso? Utilize a senha provisória enviada pela coordenação. Será obrigatório cadastrar uma nova senha.
            </p>
          </div>
        </div>
      </div>
    </div>
  `,
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
