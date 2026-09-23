import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

/**
 * Tela de redefinição obrigatória de senha no primeiro login.
 */
@Component({
  selector: 'app-first-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 bg-gradient-to-br from-slate-900 via-clinical-950 to-slate-900">
      <div class="sm:mx-auto sm:w-full sm:max-w-md text-center">
        <div class="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-amber-500/20 border border-amber-500/30 text-amber-400 mb-3 shadow-lg">
          <svg class="w-7 h-7" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
          </svg>
        </div>
        <h2 class="text-2xl font-extrabold text-white sm:text-3xl tracking-tight">Primeiro Acesso ao SIGEA</h2>
        <p class="mt-2 text-sm text-slate-300">
          Por segurança, você deve substituir a senha provisória por uma nova senha definitiva.
        </p>
      </div>

      <div class="mt-8 sm:mx-auto sm:w-full sm:max-w-md px-4 sm:px-0">
        <div class="bg-white py-8 px-6 shadow-2xl rounded-2xl sm:px-10 border border-slate-100">
          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-5">
            <!-- Senha Atual (Provisória) -->
            <div>
              <label for="currentPassword" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Senha Provisória Atual
              </label>
              <div class="mt-1.5">
                <input
                  id="currentPassword"
                  type="password"
                  formControlName="currentPassword"
                  placeholder="••••••••"
                  class="block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
              </div>
            </div>

            <!-- Nova Senha -->
            <div>
              <label for="newPassword" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Nova Senha
              </label>
              <div class="mt-1.5">
                <input
                  id="newPassword"
                  type="password"
                  formControlName="newPassword"
                  placeholder="••••••••"
                  class="block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
              </div>
            </div>

            <!-- Confirmação da Nova Senha -->
            <div>
              <label for="confirmPassword" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Confirmar Nova Senha
              </label>
              <div class="mt-1.5">
                <input
                  id="confirmPassword"
                  type="password"
                  formControlName="confirmPassword"
                  placeholder="••••••••"
                  class="block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
              </div>

              @if (passwordsDoNotMatch()) {
                <p class="mt-1.5 text-xs text-rose-600 font-medium">As senhas não coincidem.</p>
              }
            </div>

            <!-- Checklist de Requisitos da Senha -->
            <div class="p-4 bg-slate-50 rounded-xl border border-slate-200 text-xs space-y-2">
              <span class="font-bold text-slate-700 uppercase tracking-wider block text-[10px]">Requisitos da Senha:</span>
              <div class="grid grid-cols-1 gap-1.5 text-slate-600">
                <div class="flex items-center gap-2" [ngClass]="hasMinLength() ? 'text-emerald-600 font-medium' : ''">
                  <span class="w-1.5 h-1.5 rounded-full" [ngClass]="hasMinLength() ? 'bg-emerald-600' : 'bg-slate-300'"></span>
                  Mínimo de 8 caracteres
                </div>
                <div class="flex items-center gap-2" [ngClass]="hasUpper() ? 'text-emerald-600 font-medium' : ''">
                  <span class="w-1.5 h-1.5 rounded-full" [ngClass]="hasUpper() ? 'bg-emerald-600' : 'bg-slate-300'"></span>
                  Pelo menos uma letra maiúscula (A-Z)
                </div>
                <div class="flex items-center gap-2" [ngClass]="hasLower() ? 'text-emerald-600 font-medium' : ''">
                  <span class="w-1.5 h-1.5 rounded-full" [ngClass]="hasLower() ? 'bg-emerald-600' : 'bg-slate-300'"></span>
                  Pelo menos uma letra minúscula (a-z)
                </div>
                <div class="flex items-center gap-2" [ngClass]="hasDigit() ? 'text-emerald-600 font-medium' : ''">
                  <span class="w-1.5 h-1.5 rounded-full" [ngClass]="hasDigit() ? 'bg-emerald-600' : 'bg-slate-300'"></span>
                  Pelo menos um número (0-9)
                </div>
                <div class="flex items-center gap-2" [ngClass]="hasSpecial() ? 'text-emerald-600 font-medium' : ''">
                  <span class="w-1.5 h-1.5 rounded-full" [ngClass]="hasSpecial() ? 'bg-emerald-600' : 'bg-slate-300'"></span>
                  Pelo menos um símbolo especial (&#64;$!%*?&)
                </div>
              </div>
            </div>

            <!-- Botão Salvar Senha -->
            <div class="pt-2">
              <button
                type="submit"
                [disabled]="form.invalid || isSubmitting() || passwordsDoNotMatch() || !isPasswordPolicyMet()"
                class="w-full btn-primary py-3 text-sm font-semibold tracking-wide"
              >
                @if (isSubmitting()) {
                  <span class="flex items-center gap-2">
                    <svg class="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                    </svg>
                    Salvando Nova Senha...
                  </span>
                } @else {
                  <span>Concluir Redefinição e Entrar</span>
                }
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
})
export class FirstLoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);

  readonly isSubmitting = signal<boolean>(false);

  readonly form = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  get newPasswordVal(): string {
    return this.form.get('newPassword')?.value || '';
  }

  hasMinLength(): boolean {
    return this.newPasswordVal.length >= 8;
  }

  hasUpper(): boolean {
    return /[A-Z]/.test(this.newPasswordVal);
  }

  hasLower(): boolean {
    return /[a-z]/.test(this.newPasswordVal);
  }

  hasDigit(): boolean {
    return /\d/.test(this.newPasswordVal);
  }

  hasSpecial(): boolean {
    return /[@$!%*?&]/.test(this.newPasswordVal);
  }

  isPasswordPolicyMet(): boolean {
    return this.hasMinLength() && this.hasUpper() && this.hasLower() && this.hasDigit() && this.hasSpecial();
  }

  passwordsDoNotMatch(): boolean {
    const newPwd = this.form.get('newPassword')?.value;
    const confirmPwd = this.form.get('confirmPassword')?.value;
    return !!(confirmPwd && newPwd && newPwd !== confirmPwd);
  }

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
