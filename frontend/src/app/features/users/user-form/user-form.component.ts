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
  templateUrl: './user-form.component.html',
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
