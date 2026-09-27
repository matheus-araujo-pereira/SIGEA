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
  templateUrl: './first-login.component.html',
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
