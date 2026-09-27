import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { User } from '../../core/models/user.model';

/**
 * Tela de visualização dos dados cadastrais do perfil e alteração voluntária de senha.
 */
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly toastService = inject(ToastService);

  readonly user = signal<User | null>(this.authService.currentUser());
  readonly isUpdatingProfile = signal<boolean>(false);
  readonly isChangingPassword = signal<boolean>(false);

  readonly profileForm = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(3)]],
  });

  readonly passwordForm = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  ngOnInit(): void {
    const currentUser = this.authService.currentUser();
    if (currentUser) {
      this.profileForm.patchValue({ fullName: currentUser.fullName });
    }
    this.loadFreshProfile();
  }

  loadFreshProfile(): void {
    this.authService.getProfile().subscribe({
      next: (res) => {
        if (res.data) {
          this.user.set(res.data);
          this.profileForm.patchValue({ fullName: res.data.fullName });
        }
      },
    });
  }

  passwordMismatch(): boolean {
    const np = this.passwordForm.get('newPassword')?.value;
    const cp = this.passwordForm.get('confirmPassword')?.value;
    return !!(cp && np && np !== cp);
  }

  getRoleBadgeClasses(): string {
    const role = this.user()?.role;
    switch (role) {
      case 'ADMIN':
        return 'bg-purple-100 text-purple-800 border border-purple-200';
      case 'PROFESSOR':
        return 'bg-blue-100 text-blue-800 border border-blue-200';
      case 'STUDENT':
        return 'bg-emerald-100 text-emerald-800 border border-emerald-200';
      default:
        return 'bg-slate-100 text-slate-800';
    }
  }

  onUpdateProfile(): void {
    if (this.profileForm.invalid) return;

    this.isUpdatingProfile.set(true);
    const { fullName } = this.profileForm.getRawValue();

    this.authService.updateProfile({ fullName: fullName! }).subscribe({
      next: (res) => {
        this.isUpdatingProfile.set(false);
        this.user.set(res.data);
        this.toastService.success('Sucesso', 'Nome atualizado com sucesso.');
      },
      error: () => this.isUpdatingProfile.set(false),
    });
  }

  onChangePassword(): void {
    if (this.passwordForm.invalid || this.passwordMismatch()) return;

    this.isChangingPassword.set(true);
    const { currentPassword, newPassword, confirmPassword } = this.passwordForm.getRawValue();

    this.authService
      .changePassword({
        currentPassword: currentPassword!,
        newPassword: newPassword!,
        confirmPassword: confirmPassword!,
      })
      .subscribe({
        next: () => {
          this.isChangingPassword.set(false);
          this.passwordForm.reset();
          this.toastService.success('Sucesso', 'Senha alterada com sucesso.');
        },
        error: () => this.isChangingPassword.set(false),
      });
  }
}
