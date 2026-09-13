import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CasoClinico,
  SalvarCasoClinicoPayload,
  AtividadeEducacional,
  SalvarAtividadePayload,
  PainelAtividade,
  Submissao,
  SalvarSubmissaoPayload,
  AvaliarSubmissaoPayload,
  MinhaAtividadeItem,
} from '../modelos/educacional.modelos';

/**
 * Categoria de Evento Adverso (EA) conforme classificação internacional de segurança do paciente.
 */
export interface CategoriaEA {
  /** Identificador da categoria de evento adverso. */
  id: number;
  /** Nome ou classificação da categoria (ex: Infecção Relacionada à Assistência à Saúde - IRAS, Queda, Erro de Medicação). */
  nome: string;
  /** Definição técnica e critérios clínicos para enquadramento. */
  definicaoOperacional: string;
}

/**
 * Serviço do Módulo Educacional e de Auditoria do SIGEA-GTT.
 *
 * Centraliza as operações clínicas e pedagógicas:
 * - Gestão de casos clínicos e prontuários simulados;
 * - Distribuição de atividades curriculares de auditoria retrospectiva;
 * - Execução do fluxo de auditoria IHI-GTT pelos discentes;
 * - Painel de acompanhamento, correção e atribuição de notas pelos docentes.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class EducacionalService {
  private readonly http = inject(HttpClient);
  private readonly urlCasos = '/api/casos-clinicos';
  private readonly urlAtividades = '/api/atividades-educacionais';
  private readonly urlSubmissoes = '/api/submissoes';

  // --- CASOS CLÍNICOS ---

  /**
   * Lista os casos clínicos simulados disponíveis, com suporte a filtros por unidade e autoria.
   *
   * @param unidadeId ID opcional da unidade hospitalar.
   * @param apenasMeus Flag para restringir aos casos de autoria do docente logado.
   * @returns Observable com array de casos clínicos.
   */
  listarCasos(unidadeId?: number, apenasMeus?: boolean): Observable<CasoClinico[]> {
    let params = new HttpParams();
    if (unidadeId) params = params.set('unidadeHospitalarId', unidadeId.toString());
    if (apenasMeus !== undefined) params = params.set('apenasMeus', apenasMeus.toString());
    return this.http.get<CasoClinico[]>(this.urlCasos, { params });
  }

  /**
   * Busca os detalhes completos de um caso clínico e seu prontuário simulado.
   *
   * @param id Identificador do caso clínico.
   * @returns Observable com os dados do caso clínico.
   */
  buscarCasoPorId(id: number): Observable<CasoClinico> {
    return this.http.get<CasoClinico>(`${this.urlCasos}/${id}`);
  }

  /**
   * Cadastra um novo caso clínico simulado com dados de prontuário.
   *
   * @param payload Dados do prontuário simulado.
   * @returns Observable com o caso clínico criado.
   */
  salvarCaso(payload: SalvarCasoClinicoPayload): Observable<CasoClinico> {
    return this.http.post<CasoClinico>(this.urlCasos, payload);
  }

  /**
   * Atualiza as informações e seções de prontuário de um caso clínico existente.
   *
   * @param id Identificador do caso clínico.
   * @param payload Dados atualizados.
   * @returns Observable com o caso clínico atualizado.
   */
  editarCaso(id: number, payload: SalvarCasoClinicoPayload): Observable<CasoClinico> {
    return this.http.put<CasoClinico>(`${this.urlCasos}/${id}`, payload);
  }

  /**
   * Exclui um caso clínico do repositório.
   *
   * @param id Identificador do caso clínico.
   * @returns Observable void.
   */
  excluirCaso(id: number): Observable<void> {
    return this.http.delete<void>(`${this.urlCasos}/${id}`);
  }

  // --- ATIVIDADES EDUCACIONAIS ---

  /**
   * Lista atividades educacionais de auditoria, opcionalmente filtradas por turma.
   *
   * @param turmaId ID opcional da turma.
   * @returns Observable com array de atividades educacionais.
   */
  listarAtividades(turmaId?: number): Observable<AtividadeEducacional[]> {
    let params = new HttpParams();
    if (turmaId) params = params.set('turmaId', turmaId.toString());
    return this.http.get<AtividadeEducacional[]>(this.urlAtividades, {
      params,
    });
  }

  /**
   * Busca os detalhes de configuração de uma atividade educacional.
   *
   * @param id Identificador da atividade.
   * @returns Observable com os dados da atividade.
   */
  buscarAtividadePorId(id: number): Observable<AtividadeEducacional> {
    return this.http.get<AtividadeEducacional>(`${this.urlAtividades}/${id}`);
  }

  /**
   * Obtém o painel de monitoramento e submissões dos discentes para uma atividade.
   *
   * @param id Identificador da atividade educacional.
   * @returns Observable com dados consolidados do painel docente.
   */
  buscarPainelAtividade(id: number): Observable<PainelAtividade> {
    return this.http.get<PainelAtividade>(`${this.urlAtividades}/${id}/painel`);
  }

  /**
   * Cadastra uma nova atividade curricular de auditoria retrospectiva.
   *
   * @param payload Configurações da atividade.
   * @returns Observable com a atividade criada.
   */
  salvarAtividade(payload: SalvarAtividadePayload): Observable<AtividadeEducacional> {
    return this.http.post<AtividadeEducacional>(this.urlAtividades, payload);
  }

  /**
   * Atualiza as configurações e prazos de uma atividade existente.
   *
   * @param id Identificador da atividade.
   * @param payload Configurações atualizadas.
   * @returns Observable com a atividade atualizada.
   */
  editarAtividade(id: number, payload: SalvarAtividadePayload): Observable<AtividadeEducacional> {
    return this.http.put<AtividadeEducacional>(`${this.urlAtividades}/${id}`, payload);
  }

  /**
   * Remove uma atividade educacional do sistema.
   *
   * @param id Identificador da atividade.
   * @returns Observable void.
   */
  excluirAtividade(id: number): Observable<void> {
    return this.http.delete<void>(`${this.urlAtividades}/${id}`);
  }

  // --- SUBMISSÕES / AMBIENTE DO ALUNO & CORREÇÃO ---

  /**
   * Lista as atividades distribuídas para o discente autenticado ("Minhas Atividades").
   *
   * @returns Observable com array de itens de atividade do discente.
   */
  listarMinhasAtividades(): Observable<MinhaAtividadeItem[]> {
    return this.http.get<MinhaAtividadeItem[]>(`${this.urlSubmissoes}/minhas`);
  }

  /**
   * Inicia uma nova tentativa de auditoria ou recupera uma em andamento para o discente.
   *
   * @param atividadeId Identificador da atividade curricular.
   * @returns Observable com a entidade de submissão do discente.
   */
  iniciarOuContinuar(atividadeId: number): Observable<Submissao> {
    return this.http.post<Submissao>(`${this.urlSubmissoes}/iniciar/${atividadeId}`, {});
  }

  /**
   * Busca uma submissão pelo ID para visualização ou correção docente.
   *
   * @param id Identificador da submissão.
   * @returns Observable com a submissão completa.
   */
  buscarSubmissao(id: number): Observable<Submissao> {
    return this.http.get<Submissao>(`${this.urlSubmissoes}/${id}`);
  }

  /**
   * Salva o rascunho de progresso da auditoria ou finaliza e submete para avaliação.
   *
   * @param id Identificador da submissão.
   * @param payload Achados de gatilhos, ferramentas de qualidade e tempo gasto.
   * @returns Observable com a submissão atualizada.
   */
  salvarProgresso(id: number, payload: SalvarSubmissaoPayload): Observable<Submissao> {
    return this.http.put<Submissao>(`${this.urlSubmissoes}/${id}/progresso`, payload);
  }

  /**
   * Registra a correção docente, nota e parecer pedagógico para uma submissão.
   *
   * @param id Identificador da submissão.
   * @param payload Nota atribuída e parecer detalhado.
   * @returns Observable com a submissão avaliada.
   */
  avaliarSubmissao(id: number, payload: AvaliarSubmissaoPayload): Observable<Submissao> {
    return this.http.post<Submissao>(`${this.urlSubmissoes}/${id}/avaliar`, payload);
  }

  /**
   * Lista todas as submissões que aguardam correção pelo docente logado.
   *
   * @returns Observable com array de submissões pendentes.
   */
  listarPendentes(): Observable<Submissao[]> {
    return this.http.get<Submissao[]>(`${this.urlSubmissoes}/pendentes`);
  }

  /**
   * Lista as categorias canônicas de Evento Adverso (EA) cadastradas no sistema.
   *
   * @returns Observable com array de categorias de EA.
   */
  listarCategoriasEA(): Observable<CategoriaEA[]> {
    return this.http.get<CategoriaEA[]>(`${this.urlSubmissoes}/categorias-ea`);
  }
}
