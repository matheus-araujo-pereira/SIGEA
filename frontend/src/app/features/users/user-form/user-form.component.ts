import { Component, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { User, UserRole } from '../../../core/models/user.model';

/**
 * Validador para domínio @academico.ufs.br.
 */
function ufsEmailValidator(control: { value: string | null }) {
  const value = control.value?.trim().toLowerCase() || '';
  if (!value) return null;
  return /^[a-zA-Z0-9._%+-]+@academico\.ufs\.br$/.test(value) ? null : { invalidUfsEmail: true };
}

/**
 * Modal para cadastro e edição de usuários com controle dinâmico do campo matrícula.
 */
@Component({
  selector: 'app-user-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    @if (isOpen()) {
      <div
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm transition-opacity"
        role="dialog"
        aria-modal="true"
      >
        <div class="bg-white rounded-2xl max-w-lg w-full p-6 shadow-2xl border border-slate-100 animate-in fade-in zoom-in-95 duration-200">
          <!-- Cabeçalho -->
          <div class="flex items-center justify-between pb-4 border-b border-slate-100">
            <div>
              <h3 class="text-lg font-bold text-slate-900">
                {{ isEditing() ? 'Editar Usuário' : 'Novo Usuário Institucional' }}
              </h3>
              <p class="text-xs text-slate-500 mt-0.5">
                {{ isEditing() ? 'Atualize as informações cadastrais' : 'Preencha os dados do usuário para geração de credenciais' }}
              </p>
            </div>
            <button
              type="button"
              (click)="onClose()"
              class="text-slate-400 hover:text-slate-700 p-1.5 rounded-lg hover:bg-slate-100"
              aria-label="Fechar"
            >
              <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <!-- Formulário -->
          @if (!createdPassword()) {
            <form [formGroup]="form" (ngSubmit)="onSubmit()" class="mt-5 space-y-4">
              <!-- Nome Completo -->
              <div>
                <label for="fullName" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                  Nome Completo
                </label>
                <input
                  id="fullName"
                  type="text"
                  formControlName="fullName"
                  placeholder="Ex: Matheus Araujo Pereira"
                  class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
              </div>

              <!-- E-mail Institucional -->
              <div>
                <label for="email" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                  E-mail Institucional (&#64;academico.ufs.br)
                </label>
                <input
                  id="email"
                  type="email"
                  formControlName="email"
                  placeholder="usuario@academico.ufs.br"
                  class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                />
                @if (hasEmailError()) {
                  <p class="mt-1 text-xs text-rose-600 font-medium">
                    O e-mail deve pertencer obrigatoriamente ao domínio &#64;academico.ufs.br
                  </p>
                }
              </div>

              <!-- Perfil de Acesso -->
              <div>
                <label for="role" class="block text-xs font-bold text-slate-700 uppercase tracking-wider">
                  Perfil de Acesso
                </label>
                <select
                  id="role"
                  formControlName="role"
                  class="mt-1 block w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-lg text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                >
                  <option value="ADMIN">ADMINISTRADOR</option>
                  <option value="PROFESSOR">PROFESSOR</option>
                  <option value="STUDENT">ESTUDANTE</option>
                </select>
              </div>

              <!-- Campo Matrícula (Exclusivo e Obrigatório para STUDENT) -->
              @if (isStudentSelected()) {
                <div class="p-3.5 bg-blue-50/60 rounded-xl border border-blue-200/80 space-y-1 animate-in fade-in duration-200">
                  <label for="regNum" class="block text-xs font-bold text-blue-900 uppercase tracking-wider">
                    Matrícula Institucional <span class="text-rose-600">*</span>
                  </label>
                  <input
                    id="regNum"
                    type="text"
                    formControlName="registrationNumber"
                    placeholder="Ex: 20260001234"
                    class="mt-1 block w-full px-3.5 py-2 bg-white border border-blue-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-clinical-500"
                  />
                  <p class="text-[11px] text-blue-700 font-medium">
                    Obrigatório exclusivamente para estudantes da disciplina/curso.
                  </p>
                </div>
              }

              <!-- Ações -->
              <div class="mt-6 pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
                <button type="button" (click)="onClose()" class="btn-secondary">
                  Cancelar
                </button>
                <button
                  type="submit"
                  [disabled]="form.invalid || isSaving()"
                  class="btn-primary"
                >
                  {{ isSaving() ? 'Salvando...' : (isEditing() ? 'Atualizar Usuário' : 'Cadastrar Usuário') }}
                </button>
              </div>
            </form>
          } @else {
            <!-- Modal de Exibição de Senha Provisória Gerada -->
            <div class="mt-5 space-y-4 text-center">
              <div class="w-12 h-12 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
              </div>
              <h4 class="text-base font-bold text-slate-800">Usuário Criado com Sucesso!</h4>
              <p class="text-xs text-slate-600">
                Uma senha provisória aleatória foi gerada pelo sistema. Copie e envie ao usuário.
              </p>

              <!-- Caixa de Senha Provisória com Botão Copiar -->
              <div class="p-4 bg-slate-100 rounded-xl border border-slate-200 flex items-center justify-between gap-2">
                <span class="font-mono text-base font-bold text-slate-800 tracking-wider">
                  {{ createdPassword() }}
                </span>
                <button
                  type="button"
                  (click)="copyPassword()"
                  class="btn-secondary text-xs py-1.5 px-3"
                >
                  {{ copied() ? 'Copiado!' : 'Copiar Senha' }}
                </button>
              </div>

              <div class="pt-3">
                <button type="button" (click)="finishCreation()" class="w-full btn-primary">
                  Concluir e Fechar
                </button>
              </div>
            </div>
          }
        </div>
      </div>
    }
  `,
})
export class UserFormComponent implements OnChanges {
  private readonly fb = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly toastService = inject(ToastService);

  readonly isOpen = input<boolean>(false);
  readonly user = input<User | null>(null);

  readonly saved = output<void>();
  readonly closed = output<void>();

  readonly isSaving = signal<boolean>(false);
  readonly createdPassword = signal<string | null>(null);
  readonly copied = signal<boolean>(false);

  readonly form = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, ufsEmailValidator]],
    role: ['STUDENT' as UserRole, [Validators.required]],
    registrationNumber: ['', [Validators.required, Validators.minLength(2)]],
  });

  constructor() {
    this.form.get('role')?.valueChanges.subscribe((role) => {
      this.updateRegistrationValidation(role);
    });
    this.updateRegistrationValidation('STUDENT');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['user'] || changes['isOpen']) {
      if (this.isOpen()) {
        this.createdPassword.set(null);
        this.copied.set(false);

        const current = this.user();
        if (current) {
          this.form.patchValue({
            fullName: current.fullName,
            email: current.email,
            role: current.role,
            registrationNumber: current.registrationNumber || '',
          });
          this.updateRegistrationValidation(current.role);
        } else {
          this.form.reset({
            fullName: '',
            email: '',
            role: 'STUDENT',
            registrationNumber: '',
          });
          this.updateRegistrationValidation('STUDENT');
        }
      }
    }
  }

  isEditing(): boolean {
    return !!this.user();
  }

  isStudentSelected(): boolean {
    return this.form.get('role')?.value === 'STUDENT';
  }

  hasEmailError(): boolean {
    const control = this.form.get('email');
    return !!(control && control.dirty && control.hasError('invalidUfsEmail'));
  }

  updateRegistrationValidation(role: UserRole | null): void {
    const regControl = this.form.get('registrationNumber');
    if (role === 'STUDENT') {
      regControl?.setValidators([Validators.required, Validators.minLength(2)]);
    } else {
      regControl?.clearValidators();
      regControl?.setValue('');
    }
    regControl?.updateValueAndValidity();
  }

  copyPassword(): void {
    const pwd = this.createdPassword();
    if (pwd) {
      navigator.clipboard.writeText(pwd);
      this.copied.set(true);
      this.toastService.info('Copiado', 'Senha provisória copiada para a área de transferência.');
      setTimeout(() => this.copied.set(false), 2500);
    }
  }

  finishCreation(): void {
    this.createdPassword.set(null);
    this.saved.emit();
  }

  onClose(): void {
    this.createdPassword.set(null);
    this.closed.emit();
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSaving.set(true);
    const raw = this.form.getRawValue();

    if (this.isEditing()) {
      const id = this.user()!.id;
      this.userService
        .updateUser(id, {
          fullName: raw.fullName!,
          email: raw.email!,
          role: raw.role!,
          registrationNumber: raw.role === 'STUDENT' ? raw.registrationNumber : null,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toastService.success('Sucesso', 'Usuário atualizado com sucesso.');
            this.saved.emit();
          },
          error: () => this.isSaving.set(false),
        });
    } else {
      this.userService
        .createUser({
          fullName: raw.fullName!,
          email: raw.email!,
          role: raw.role!,
          registrationNumber: raw.role === 'STUDENT' ? raw.registrationNumber : null,
        })
        .subscribe({
          next: (res) => {
            this.isSaving.set(false);
            if (res.data.provisionalPassword) {
              this.createdPassword.set(res.data.provisionalPassword);
            } else {
              this.toastService.success('Sucesso', 'Usuário cadastrado com sucesso.');
              this.saved.emit();
            }
          },
          error: () => this.isSaving.set(false),
        });
    }
  }
}
