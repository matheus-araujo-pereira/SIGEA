import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Turma, TurmaRequisicao } from '../modelos/turma.modelos';
import { Usuario } from '../../usuario/modelos/usuario.modelos';

export { TurmaRequisicao };

/**
 * Serviço de gerenciamento de turmas acadêmicas do SIGEA-GTT.
 *
 * Provê funcionalidades para administração de turmas, matrículas de discentes
 * e filtragem por docente responsável.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class TurmaService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/turmas';

  /**
   * Lista as turmas acadêmicas cadastradas, opcionalmente filtrando por docente responsável.
   *
   * @param professorId ID opcional do professor para filtrar apenas suas turmas.
   * @returns Observable com array de turmas.
   */
  listar(professorId?: number): Observable<Turma[]> {
    let params = new HttpParams();
    if (professorId) {
      params = params.set('professorId', professorId.toString());
    }
    return this.http.get<Turma[]>(this.url, { params });
  }

  /**
   * Busca os detalhes de uma turma acadêmica pelo ID.
   *
   * @param id Identificador da turma.
   * @returns Observable com os dados da turma.
   */
  buscarPorId(id: number): Observable<Turma> {
    return this.http.get<Turma>(`${this.url}/${id}`);
  }

  /**
   * Cadastra uma nova turma acadêmica.
   *
   * @param dto Dados da nova turma (código, período letivo, docente responsável).
   * @returns Observable com a turma criada.
   */
  cadastrar(dto: TurmaRequisicao): Observable<Turma> {
    return this.http.post<Turma>(this.url, dto);
  }

  /**
   * Atualiza as informações de uma turma existente.
   *
   * @param id Identificador da turma.
   * @param dto Dados atualizados da turma.
   * @returns Observable com a turma atualizada.
   */
  editar(id: number, dto: TurmaRequisicao): Observable<Turma> {
    return this.http.put<Turma>(`${this.url}/${id}`, dto);
  }

  /**
   * Remove uma turma acadêmica do sistema.
   *
   * @param id Identificador da turma a ser removida.
   * @returns Observable void.
   */
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  /**
   * Alterna o status (ativa/inativa) de uma turma acadêmica.
   *
   * @param id Identificador da turma.
   * @returns Observable com a turma atualizada.
   */
  alternarStatus(id: number): Observable<Turma> {
    return this.http.patch<Turma>(`${this.url}/${id}/alternar-status`, {});
  }

  /**
   * Lista todos os discentes matriculados em uma turma.
   *
   * @param turmaId Identificador da turma.
   * @returns Observable com array de discentes matriculados.
   */
  listarAlunos(turmaId: number): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.url}/${turmaId}/alunos`);
  }

  /**
   * Matricula um discente em uma turma acadêmica.
   *
   * @param turmaId Identificador da turma.
   * @param alunoId Identificador do aluno a ser matriculado.
   * @returns Observable void.
   */
  matricularAluno(turmaId: number, alunoId: number): Observable<void> {
    return this.http.post<void>(`${this.url}/${turmaId}/alunos/${alunoId}`, {});
  }

  /**
   * Remove a matrícula de um discente em uma turma acadêmica.
   *
   * @param turmaId Identificador da turma.
   * @param alunoId Identificador do aluno a ser desmatriculado.
   * @returns Observable void.
   */
  desmatricularAluno(turmaId: number, alunoId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${turmaId}/alunos/${alunoId}`);
  }
}
