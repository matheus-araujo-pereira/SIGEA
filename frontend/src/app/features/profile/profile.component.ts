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
  template: `
    <div class="space-y-6">
      <!-- Cabeçalho da Página -->
      <div>
        <h2 class="text-2xl font-black text-slate-900 tracking-tight">Meu Perfil</h2>
        <p class="text-xs text-slate-500 mt-1">Gerencie suas informações cadastrais e credenciais de acesso</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Card de Informações Cadastrais -->
        <div class="lg:col-span-2 bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-6">
          <div class="flex items-center justify-between pb-4 border-b border-slate-100">
            <h3 class="text-base font-bold text-slate-800">Dados do Usuário</h3>
            <span class="px-2.5 py-1 text-xs font-semibold rounded-md uppercase" [ngClass]="getRoleBadgeClasses()">
              {{ user()?.role }}
            </span>
          </div>

          <form [formGroup]="profileForm" (ngSubmit)="onUpdateProfile()" class="space-y-4">
            <div>
              <label for="fullName" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Nome Completo
              </label>
              <input
                id="fullName"
                type="text"
                formControlName="fullName"
                class="mt-1.5 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                  E-mail Institucional
                </label>
                <input
                  type="text"
                  [value]="user()?.email"
                  disabled
                  class="mt-1.5 block w-full px-3.5 py-2.5 bg-slate-100 border border-slate-200 text-slate-500 rounded-lg text-sm cursor-not-allowed font-mono text-xs"
                />
              </div>

              <!-- Matrícula se Aluno -->
              @if (user()?.role === 'STUDENT') {
                <div>
                  <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                    Matrícula Institucional
                  </label>
                  <input
                    type="text"
                    [value]="user()?.registrationNumber || '-'"
                    disabled
                    class="mt-1.5 block w-full px-3.5 py-2.5 bg-slate-100 border border-slate-200 text-slate-500 rounded-lg text-sm cursor-not-allowed font-mono text-xs"
                  />
                </div>
              }
            </div>

            <div class="pt-2 flex justify-end">
              <button
                type="submit"
                [disabled]="profileForm.invalid || isUpdatingProfile()"
                class="btn-primary"
              >
                {{ isUpdatingProfile() ? 'Salvando...' : 'Salvar Alterações' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Card de Alteração Voluntária de Senha -->
        <div class="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-xs space-y-4">
          <div class="pb-3 border-b border-slate-100">
            <h3 class="text-base font-bold text-slate-800">Alterar Senha</h3>
            <p class="text-xs text-slate-500 mt-0.5">Atualize sua senha de acesso a qualquer instante</p>
          </div>

          <form [formGroup]="passwordForm" (ngSubmit)="onChangePassword()" class="space-y-4">
            <div>
              <label for="currentPwd" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Senha Atual
              </label>
              <input
                id="currentPwd"
                type="password"
                formControlName="currentPassword"
                placeholder="••••••••"
                class="mt-1.5 block w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <div>
              <label for="newPwd" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Nova Senha
              </label>
              <input
                id="newPwd"
                type="password"
                formControlName="newPassword"
                placeholder="••••••••"
                class="mt-1.5 block w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />
            </div>

            <div>
              <label for="confirmPwd" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                Confirmar Senha
              </label>
              <input
                id="confirmPwd"
                type="password"
                formControlName="confirmPassword"
                placeholder="••••••••"
                class="mt-1.5 block w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              />

              @if (passwordMismatch()) {
                <p class="mt-1 text-xs text-rose-600 font-medium">As senhas não coincidem.</p>
              }
            </div>

            <div class="pt-2">
              <button
                type="submit"
                [disabled]="passwordForm.invalid || passwordMismatch() || isChangingPassword()"
                class="w-full btn-secondary"
              >
                {{ isChangingPassword() ? 'Alterando Senha...' : 'Atualizar Senha' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
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
