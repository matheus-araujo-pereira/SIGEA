import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ModuloGtt, ModuloRequisicao } from '../modelos/gtt.modelos';

export { ModuloRequisicao };

/**
 * Serviço de gerenciamento dos módulos clínicos da metodologia IHI-GTT.
 *
 * Provê métodos para administração das categorias canônicas (Cuidados Gerais, Medicamentoso, Cirúrgico, etc.)
 * sob as quais os gatilhos e eventos adversos são estruturados.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class ModuloGttService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/modulos-gtt';

  /**
   * Obtém todos os módulos clínicos GTT cadastrados no sistema.
   *
   * @returns Observable com array de módulos clínicos.
   */
  listar(): Observable<ModuloGtt[]> {
    return this.http.get<ModuloGtt[]>(this.url);
  }

  /**
   * Busca um módulo clínico pelo seu identificador.
   *
   * @param id Identificador do módulo.
   * @returns Observable com o módulo clínico.
   */
  buscarPorId(id: number): Observable<ModuloGtt> {
    return this.http.get<ModuloGtt>(`${this.url}/${id}`);
  }

  /**
   * Cadastra um novo módulo clínico GTT.
   *
   * @param dto Dados do novo módulo (código, nome, descrição).
   * @returns Observable com o módulo criado.
   */
  cadastrar(dto: ModuloRequisicao): Observable<ModuloGtt> {
    return this.http.post<ModuloGtt>(this.url, dto);
  }

  /**
   * Atualiza as informações de um módulo clínico existente.
   *
   * @param id Identificador do módulo.
   * @param dto Novos dados para o módulo.
   * @returns Observable com o módulo atualizado.
   */
  editar(id: number, dto: ModuloRequisicao): Observable<ModuloGtt> {
    return this.http.put<ModuloGtt>(`${this.url}/${id}`, dto);
  }

  /**
   * Exclui um módulo clínico caso não possua gatilhos vinculados.
   *
   * @param id Identificador do módulo a ser excluído.
   * @returns Observable void.
   */
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  /**
   * Alterna o status (ativo/inativo) de um módulo clínico.
   *
   * @param id Identificador do módulo.
   * @returns Observable com o módulo atualizado.
   */
  alternarStatus(id: number): Observable<ModuloGtt> {
    return this.http.patch<ModuloGtt>(`${this.url}/${id}/alternar-status`, {});
  }
}
