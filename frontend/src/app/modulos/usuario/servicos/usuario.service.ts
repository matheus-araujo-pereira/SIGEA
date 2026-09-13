import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario, UsuarioRequisicao, AlterarSenhaPayload } from '../modelos/usuario.modelos';

export { UsuarioRequisicao, AlterarSenhaPayload };

/**
 * Serviço de gerenciamento de usuários institucionais do SIGEA-GTT.
 *
 * Provê métodos para CRUD de administradores, docentes e discentes,
 * além de reset de credenciais e alternância de status ativo/inativo.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/usuarios';

  /**
   * Obtém a listagem completa de usuários cadastrados no sistema.
   *
   * @returns Observable com array de usuários.
   */
  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.url);
  }

  /**
   * Busca um usuário por seu ID identificador.
   *
   * @param id ID do usuário.
   * @returns Observable com os dados do usuário.
   */
  buscarPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.url}/${id}`);
  }

  /**
   * Cadastra um novo usuário institucional no sistema.
   *
   * @param dto Dados do novo usuário (nome, e-mail, matrícula, perfil).
   * @returns Observable com o usuário criado.
   */
  cadastrar(dto: UsuarioRequisicao): Observable<Usuario> {
    return this.http.post<Usuario>(this.url, dto);
  }

  /**
   * Atualiza as informações cadastrais de um usuário existente.
   *
   * @param id ID do usuário a ser atualizado.
   * @param dto Dados cadastrais atualizados.
   * @returns Observable com o usuário atualizado.
   */
  editar(id: number, dto: UsuarioRequisicao): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.url}/${id}`, dto);
  }

  /**
   * Reseta a senha do usuário para o padrão inicial do sistema.
   *
   * @param id ID do usuário cuja senha será reiniciada.
   * @returns Observable com o usuário contendo flag de primeiro acesso reativada.
   */
  resetarSenha(id: number): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.url}/${id}/resetar-senha`, {});
  }

  /**
   * Inativa o acesso de um usuário ao sistema.
   *
   * @param id ID do usuário a ser inativado.
   * @returns Observable com o usuário atualizado.
   */
  inativar(id: number): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.url}/${id}/inativar`, {});
  }

  /**
   * Reativa o acesso de um usuário previamente inativado.
   *
   * @param id ID do usuário a ser reativado.
   * @returns Observable com o usuário atualizado.
   */
  reativar(id: number): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.url}/${id}/reativar`, {});
  }

  /**
   * Altera a senha do usuário autenticado após validação da senha atual.
   *
   * @param id ID do usuário.
   * @param payload Objeto contendo senha atual e nova senha confirmada.
   * @returns Observable com o usuário atualizado.
   */
  alterarSenha(id: number, payload: AlterarSenhaPayload): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.url}/${id}/alterar-senha`, payload);
  }
}
