import { Component, HostListener, OnChanges, SimpleChanges, inject, input, output, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { ToastService } from '../../../common/services/toast.service';
import { User, UserRole } from '../../models/user.model';
import { ufsEmailValidator } from '../../../common/validators/ufs-email.validator';

/**
 * Modal e formulário para cadastro e alteração cadastral de usuários no SIGEA.
 *
 * Aplica regra de negócio institucional: campo 'matrícula' é visível e obrigatório
 * estritamente para o perfil STUDENT, sendo omitido e persistido como null para ADMIN e PROFESSOR.
 */
@Component({
  selector: 'app-user-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-form.component.html',
})
export class UserFormComponent implements OnChanges {
  /**
   * Listener global de teclado para fechar o diálogo de usuário via tecla Escape.
   */
  @HostListener('document:keydown.escape')
  handleEscape(): void {
    if (this.isOpen()) {
      this.onClose();
    }
  }

  /** Construtor reativo de formulários */
  private readonly fb = inject(FormBuilder);
  /** Serviço de gestão de usuários */
  private readonly userService = inject(UserService);
  /** Serviço de notificações visuais */
  private readonly toastService = inject(ToastService);

  /** Signal de entrada indicando se o modal de formulário está visível. */
  readonly isOpen = input<boolean>(false);
  /** Signal de entrada com o usuário a editar (null para criação de novo usuário). */
  readonly user = input<User | null>(null);

  /** Emissor acionado após salvamento bem-sucedido. */
  readonly saved = output<void>();
  /** Emissor acionado ao fechar ou cancelar o formulário. */
  readonly closed = output<void>();

  /** Signal reativo indicando persistência assíncrona em andamento. */
  readonly isSaving = signal<boolean>(false);
  /** Signal contendo a senha provisória gerada pelo backend após criação de novo usuário. */
  readonly createdPassword = signal<string | null>(null);
  /** Signal indicando se a senha provisória foi copiada para o clipboard. */
  readonly copied = signal<boolean>(false);

  /**
   * Formulário reativo para validação dos dados de usuário.
   */
  readonly form = this.fb.group({
    fullName: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, ufsEmailValidator()]],
    role: ['STUDENT' as UserRole, [Validators.required]],
    registrationNumber: ['', [Validators.required, Validators.minLength(2)]],
  });

  /** Construtor: inicializa ouvintes de alteração de papel RBAC para regras de matrícula */
  constructor() {
    this.form.get('role')?.valueChanges.pipe(takeUntilDestroyed()).subscribe((role) => {
      this.updateRegistrationValidation(role);
    });
    this.updateRegistrationValidation('STUDENT');
  }

  /**
   * Ciclo de vida disparado ao alterar os inputs do modal para resetar ou preencher os dados.
   *
   * @param changes Objeto de alterações do Angular.
   */
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

  /**
   * Informa se o formulário opera em modo de edição ou criação.
   *
   * @returns Booleano (true se editando usuário existente).
   */
  isEditing(): boolean {
    return !!this.user();
  }

  /**
   * Informa se o perfil STUDENT está selecionado no formulário.
   *
   * @returns Booleano.
   */
  isStudentSelected(): boolean {
    return this.form.get('role')?.value === 'STUDENT';
  }

  /**
   * Verifica se o campo e-mail possui erro de domínio não-institucional.
   *
   * @returns Booleano.
   */
  hasEmailError(): boolean {
    const control = this.form.get('email');
    return !!(control && control.dirty && control.hasError('ufsEmail'));
  }

  /**
   * Atualiza dinamicamente as validações do campo matrícula conforme a regra RBAC do SIGEA.
   *
   * @param role Perfil de usuário selecionado.
   */
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

  /**
   * Copia a senha provisória gerada para a área de transferência do sistema operacional.
   *
   * @returns void
   */
  copyPassword(): void {
    const pwd = this.createdPassword();
    if (pwd) {
      navigator.clipboard.writeText(pwd);
      this.copied.set(true);
      this.toastService.info('Copiado', 'Senha provisória copiada para a área de transferência.');
      setTimeout(() => this.copied.set(false), 2500);
    }
  }

  /**
   * Finaliza o fluxo de criação após cópia da senha provisória.
   *
   * @returns void
   */
  finishCreation(): void {
    this.createdPassword.set(null);
    this.saved.emit();
  }

  /**
   * Fecha o modal cancelando as alterações.
   *
   * @returns void
   */
  onClose(): void {
    this.createdPassword.set(null);
    this.closed.emit();
  }

  /**
   * Submete a criação ou atualização cadastral do usuário.
   *
   * @returns void
   */
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
