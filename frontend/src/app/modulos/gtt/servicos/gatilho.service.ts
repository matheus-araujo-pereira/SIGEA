import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GatilhoGtt, GatilhoRequisicao } from '../modelos/gtt.modelos';

export { GatilhoRequisicao };

/**
 * Serviço de gerenciamento dos gatilhos canônicos da metodologia IHI-GTT.
 *
 * Provê operações de listagem, consulta por módulo clínico, criação, atualização,
 * exclusão e controle de vigência dos rastreadores de eventos adversos.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class GatilhoService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/gatilhos';

  /**
   * Lista os gatilhos cadastrados, opcionalmente filtrados por módulo clínico.
   *
   * @param moduloId ID opcional do módulo clínico para restringir os gatilhos.
   * @returns Observable com array de gatilhos GTT.
   */
  listar(moduloId?: number): Observable<GatilhoGtt[]> {
    let params = new HttpParams();
    if (moduloId) {
      params = params.set('moduloId', moduloId.toString());
    }
    return this.http.get<GatilhoGtt[]>(this.url, { params });
  }

  /**
   * Busca um gatilho pelo ID identificador.
   *
   * @param id Identificador do gatilho.
   * @returns Observable com os dados do gatilho.
   */
  buscarPorId(id: number): Observable<GatilhoGtt> {
    return this.http.get<GatilhoGtt>(`${this.url}/${id}`);
  }

  /**
   * Cadastra um novo gatilho no catálogo IHI-GTT.
   *
   * @param dto Dados do novo gatilho (código, módulo, descrição, limiar).
   * @returns Observable com o gatilho criado.
   */
  cadastrar(dto: GatilhoRequisicao): Observable<GatilhoGtt> {
    return this.http.post<GatilhoGtt>(this.url, dto);
  }

  /**
   * Atualiza as informações de um gatilho existente.
   *
   * @param id Identificador do gatilho.
   * @param dto Dados cadastrais atualizados.
   * @returns Observable com o gatilho atualizado.
   */
  editar(id: number, dto: GatilhoRequisicao): Observable<GatilhoGtt> {
    return this.http.put<GatilhoGtt>(`${this.url}/${id}`, dto);
  }

  /**
   * Remove um gatilho do sistema.
   *
   * @param id Identificador do gatilho a ser removido.
   * @returns Observable void.
   */
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  /**
   * Alterna o status (ativo/inativo) de um gatilho.
   *
   * @param id Identificador do gatilho.
   * @returns Observable com o gatilho atualizado.
   */
  alternarStatus(id: number): Observable<GatilhoGtt> {
    return this.http.patch<GatilhoGtt>(`${this.url}/${id}/alternar-status`, {});
  }
}
