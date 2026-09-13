import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { Usuario } from '../../usuario/modelos/usuario.modelos';
import { CredenciaisLogin, PrimeiroAcessoPayload } from '../modelos/autenticacao.modelos';

export { CredenciaisLogin, PrimeiroAcessoPayload };

/**
 * Serviço de autenticação institucional do SIGEA-GTT.
 *
 * Gerencia o ciclo de vida da sessão do usuário, autenticação com credenciais,
 * fluxo obrigatório de primeiro acesso e emissão de sinais reativos com o estado da sessão.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class AutenticacaoService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly chave = 'sigea_sessao';

  /** Sinal reativo que armazena os dados do usuário autenticado ou `null`. */
  readonly usuarioLogado = signal<Usuario | null>(this.recuperarSessao());

  /**
   * Realiza login no sistema com credenciais institucionais.
   *
   * @param credenciais Objeto contendo identificador (matrícula/e-mail) e senha.
   * @returns Observable com os dados do usuário autenticado incluindo o token JWT.
   */
  entrar(credenciais: CredenciaisLogin): Observable<Usuario> {
    return this.http
      .post<Usuario>('/api/autenticacao/entrar', credenciais)
      .pipe(tap((usuario) => this.salvarSessao(usuario)));
  }

  /**
   * Redefine a senha do usuário durante o fluxo de primeiro acesso.
   *
   * @param payload Objeto contendo ID do usuário, senha atual e nova senha confirmada.
   * @returns Observable com os dados atualizados do usuário.
   */
  redefinirPrimeiroAcesso(payload: PrimeiroAcessoPayload): Observable<Usuario> {
    return this.http
      .post<Usuario>('/api/autenticacao/primeiro-acesso', payload)
      .pipe(tap((usuario) => this.salvarSessao(usuario)));
  }

  /**
   * Encerra a sessão ativa do usuário tanto no backend quanto no frontend.
   */
  sair(): void {
    this.http.post('/api/autenticacao/sair', {}).subscribe({
      complete: () => this.limparSessao(),
      error: () => this.limparSessao(),
    });
  }

  /**
   * Verifica se existe um usuário autenticado com token válido em memória/localStorage.
   */
  estaAutenticado(): boolean {
    const usuario = this.usuarioLogado();
    return usuario !== null && Boolean(usuario.token);
  }

  /**
   * Retorna o token JWT ativo ou `null`.
   */
  obterToken(): string | null {
    return this.usuarioLogado()?.token || null;
  }

  /**
   * Indica se o usuário autenticado precisa realizar o primeiro acesso para trocar de senha.
   */
  requerPrimeiroAcesso(): boolean {
    return Boolean(this.usuarioLogado()?.primeiroAcesso);
  }

  /**
   * Obtém a rota inicial adequada com base no perfil institucional do usuário.
   */
  obterRotaPadrao(): string {
    const perfil = this.usuarioLogado()?.perfil;
    switch (perfil) {
      case 'ADMINISTRADOR':
        return '/usuarios';
      case 'PROFESSOR':
        return '/minhas-turmas';
      case 'ALUNO':
        return '/minhas-atividades';
      default:
        return '/login';
    }
  }

  /**
   * Persiste a sessão do usuário no localStorage e atualiza o sinal reativo.
   *
   * @param usuario Dados do usuário autenticado.
   */
  salvarSessao(usuario: Usuario): void {
    localStorage.setItem(this.chave, JSON.stringify(usuario));
    this.usuarioLogado.set(usuario);
  }

  /**
   * Limpa os dados de sessão no localStorage, reseta o sinal reativo e redireciona para `/login`.
   */
  limparSessao(): void {
    localStorage.removeItem(this.chave);
    this.usuarioLogado.set(null);
    this.router.navigate(['/login']);
  }

  private recuperarSessao(): Usuario | null {
    const dados = localStorage.getItem(this.chave);
    if (!dados) return null;
    try {
      const usuario = JSON.parse(dados) as Usuario;
      // Se a sessão salva for antiga e não contiver o token de autenticação, invalida para forçar novo login
      if (!usuario.token) {
        localStorage.removeItem(this.chave);
        return null;
      }
      return usuario;
    } catch {
      localStorage.removeItem(this.chave);
      return null;
    }
  }
}
