import { Component, inject, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AutenticacaoService } from '../../servicos/autenticacao.service';
import { Card } from 'primeng/card';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { Message } from 'primeng/message';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    Card,
    InputText,
    Password,
    ButtonModule,
    Message,
    IconFieldModule,
    InputIconModule,
  ],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly authService = inject(AutenticacaoService);
  private readonly router = inject(Router);

  identificador = '';
  senha = '';
  readonly carregando = signal(false);
  readonly mensagemErro = signal<string | null>(null);

  readonly textoBotao = computed(() => {
    return this.carregando() ? 'Autenticando...' : 'Acessar Plataforma';
  });

  entrar(): void {
    if (!this.identificador.trim() || !this.senha.trim()) {
      this.mensagemErro.set('Informe suas credenciais institucionais para continuar.');
      return;
    }

    this.carregando.set(true);
    this.mensagemErro.set(null);

    this.authService
      .entrar({
        identificador: this.identificador.trim(),
        senha: this.senha,
      })
      .subscribe({
        next: (usuario) => {
          this.carregando.set(false);
          if (usuario.primeiroAcesso) {
            this.router.navigate(['/primeiro-acesso']);
          } else {
            this.router.navigate([this.authService.obterRotaPadrao()]);
          }
        },
        error: (err) => {
          this.carregando.set(false);
          this.mensagemErro.set(
            err.error?.mensagem || 'Credenciais inválidas. Verifique usuário e senha.',
          );
        },
      });
  }
}
