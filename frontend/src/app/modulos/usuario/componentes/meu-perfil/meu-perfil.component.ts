import { Component, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { TagModule } from 'primeng/tag';
import { MessageService } from 'primeng/api';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';

import { AutenticacaoService } from '../../../autenticacao/servicos/autenticacao.service';
import { UsuarioService, AlterarSenhaPayload } from '../../servicos/usuario.service';

/**
 * Componente de visualização e edição do perfil do usuário autenticado.
 *
 * Exibe os dados cadastrais do usuário logado e permite a troca de senha
 * através do fluxo de redefinição autenticada (senha atual + nova + confirmação).
 */
@Component({
  selector: 'app-meu-perfil',
  imports: [
    FormsModule,
    PasswordModule,
    ButtonModule,
    MessageModule,
    TagModule,
    IconFieldModule,
    InputIconModule,
  ],
  templateUrl: './meu-perfil.component.html',
  styleUrl: './meu-perfil.component.scss',
})
export class MeuPerfilComponent {
  private readonly auth = inject(AutenticacaoService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly messageService = inject(MessageService);

  readonly usuario = computed(() => this.auth.usuarioLogado());

  readonly iniciais = computed(() => {
    const nome = this.usuario()?.nomeCompleto || 'U';
    const partes = nome.trim().split(/\s+/);
    if (partes.length === 1) return partes[0].substring(0, 2).toUpperCase();
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
  });

  readonly alterandoSenha = signal(false);
  readonly mensagemErro = signal<string | null>(null);

  formularioSenha: AlterarSenhaPayload = {
    senhaAtual: '',
    novaSenha: '',
    confirmacaoNovaSenha: '',
  };

  validarFormularioSenha(): boolean {
    this.mensagemErro.set(null);

    if (!this.formularioSenha.senhaAtual) {
      this.mensagemErro.set('Informe sua senha atual.');
      return false;
    }

    if (!this.formularioSenha.novaSenha || this.formularioSenha.novaSenha.length < 6) {
      this.mensagemErro.set('A nova senha deve ter no mínimo 6 caracteres.');
      return false;
    }

    if (this.formularioSenha.novaSenha !== this.formularioSenha.confirmacaoNovaSenha) {
      this.mensagemErro.set('A confirmação da nova senha não confere com a nova senha digitada.');
      return false;
    }

    if (this.formularioSenha.novaSenha === this.formularioSenha.senhaAtual) {
      this.mensagemErro.set('A nova senha deve ser diferente da sua senha atual.');
      return false;
    }

    return true;
  }

  salvarNovaSenha(): void {
    if (!this.validarFormularioSenha()) {
      return;
    }

    const u = this.usuario();
    if (!u) {
      this.mensagemErro.set('Sessão inválida. Faça login novamente.');
      return;
    }

    this.alterandoSenha.set(true);
    this.mensagemErro.set(null);

    this.usuarioService.alterarSenha(u.id, this.formularioSenha).subscribe({
      next: () => {
        this.alterandoSenha.set(false);
        this.messageService.add({
          severity: 'success',
          summary: 'Senha Alterada',
          detail: 'Sua senha foi alterada com sucesso!',
        });
        this.formularioSenha = {
          senhaAtual: '',
          novaSenha: '',
          confirmacaoNovaSenha: '',
        };
      },
      error: (err) => {
        this.alterandoSenha.set(false);
        this.mensagemErro.set(
          err.error?.mensagem || 'Falha ao alterar senha. Verifique os dados informados.',
        );
      },
    });
  }

  getPerfilSeverity(perfil?: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (perfil) {
      case 'ADMINISTRADOR':
        return 'danger';
      case 'PROFESSOR':
        return 'info';
      case 'ALUNO':
        return 'success';
      default:
        return 'secondary';
    }
  }
}
