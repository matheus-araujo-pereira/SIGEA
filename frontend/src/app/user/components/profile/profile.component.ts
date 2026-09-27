import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../auth/services/auth.service';
import { ToastService } from '../../../common/services/toast.service';
import { User } from '../../models/user.model';

/**
 * Componente de tela única para visualização cadastral do perfil e alteração voluntária de senha.
 *
 * Permite ao usuário conectado retificar seu nome civil e redefinir sua senha pessoal
 * mediante validação da senha atual.
 */
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
})
export class ProfileComponent implements OnInit {
  /** Construtor reativo de formulários */
  private readonly fb = inject(FormBuilder);
  /** Serviço de autenticação e sessão do usuário */
  private readonly authService = inject(AuthService);
  /** Serviço de notificações de toast */
  private readonly toastService = inject(ToastService);

  /** Signal reativo com os dados do usuário autenticado no momento. */
  readonly user = signal<User | null>(this.authService.currentUser());
  /** Signal reativo indicando persistência da atualização cadastral em andamento. */
  readonly isUpdatingProfile = signal<boolean>(false);
  /** Signal reativo indicando alteração de senha em processamento assíncrono. */
  readonly isChangingPassword = signal<boolean>(false);

  /**
   * Formulário reativo para atualização do nome civil do perfil.
   */
  readonly profileForm = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(3)]],
  });

  /**
   * Formulário reativo para alteração voluntária de senha pessoal.
   */
  readonly passwordForm = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  /** Inicialização: preenche o formulário com os dados cadastrais da sessão */
  ngOnInit(): void {
    const currentUser = this.authService.currentUser();
    if (currentUser) {
      this.profileForm.patchValue({ fullName: currentUser.fullName });
    }
    this.loadFreshProfile();
  }

  /**
   * Consulta os dados cadastrais mais recentes do usuário diretamente no backend.
   *
   * @returns void
   */
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

  /**
   * Verifica se há divergência entre a nova senha digitada e a confirmação.
   *
   * @returns Booleano.
   */
  passwordMismatch(): boolean {
    const np = this.passwordForm.get('newPassword')?.value;
    const cp = this.passwordForm.get('confirmPassword')?.value;
    return !!(cp && np && np !== cp);
  }

  /**
   * Retorna a classe CSS da badge de perfil com semiótica hospitalar.
   *
   * @returns Classes CSS utilitárias.
   */
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

  /**
   * Submete a atualização cadastral do nome civil.
   *
   * @returns void
   */
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

  /**
   * Submete a alteração voluntária de senha pessoal.
   *
   * @returns void
   */
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
