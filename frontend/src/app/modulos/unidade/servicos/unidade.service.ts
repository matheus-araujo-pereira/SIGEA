import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UnidadeHospitalar, UnidadeRequisicao } from '../modelos/unidade.modelos';

export { UnidadeRequisicao };

/**
 * Serviço de gerenciamento de unidades hospitalares do SIGEA-GTT.
 *
 * Responsável pelas operações sobre setores clínicos e enfermarias do HU-UFS / EBSERH
 * que servem de escopo e contexto para casos clínicos e auditorias retrospectivas.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class UnidadeService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/unidades';

  /**
   * Obtém a lista completa de unidades hospitalares cadastradas.
   *
   * @returns Observable contendo array de unidades hospitalares.
   */
  listar(): Observable<UnidadeHospitalar[]> {
    return this.http.get<UnidadeHospitalar[]>(this.url);
  }

  /**
   * Busca uma unidade hospitalar pelo ID.
   *
   * @param id Identificador da unidade hospitalar.
   * @returns Observable com a unidade hospitalar.
   */
  buscarPorId(id: number): Observable<UnidadeHospitalar> {
    return this.http.get<UnidadeHospitalar>(`${this.url}/${id}`);
  }

  /**
   * Cadastra uma nova unidade hospitalar.
   *
   * @param dto Dados da nova unidade hospitalar (nome e sigla).
   * @returns Observable com a unidade criada.
   */
  cadastrar(dto: UnidadeRequisicao): Observable<UnidadeHospitalar> {
    return this.http.post<UnidadeHospitalar>(this.url, dto);
  }

  /**
   * Atualiza o cadastro de uma unidade hospitalar existente.
   *
   * @param id Identificador da unidade hospitalar.
   * @param dto Novos dados da unidade hospitalar.
   * @returns Observable com a unidade atualizada.
   */
  editar(id: number, dto: UnidadeRequisicao): Observable<UnidadeHospitalar> {
    return this.http.put<UnidadeHospitalar>(`${this.url}/${id}`, dto);
  }

  /**
   * Exclui uma unidade hospitalar do sistema.
   *
   * @param id Identificador da unidade hospitalar.
   * @returns Observable void indicando conclusão da exclusão.
   */
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  /**
   * Alterna o status (ativa/inativa) de uma unidade hospitalar.
   *
   * @param id Identificador da unidade hospitalar.
   * @returns Observable com a unidade hospitalar atualizada.
   */
  alternarStatus(id: number): Observable<UnidadeHospitalar> {
    return this.http.patch<UnidadeHospitalar>(`${this.url}/${id}/alternar-status`, {});
  }
}
