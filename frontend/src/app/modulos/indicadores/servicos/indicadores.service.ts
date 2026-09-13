import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  IndicadoresIHI,
  FiltrosIndicadores,
  QuadroResumoResultado,
  DesempenhoGatilhosResultado,
} from '../modelos/indicadores.modelos';

export * from '../modelos/indicadores.modelos';

/**
 * Serviço de inteligência epidemiológica e indicadores IHI Global Trigger Tool (IHI-GTT).
 *
 * Provê cálculos agregados das métricas canônicas de segurança do paciente recomendadas pelo
 * Institute for Healthcare Improvement (IHI):
 * - Taxa de Danos por 1.000 pacientes-dia;
 * - Frequência de Danos por 100 admissões;
 * - Prevalência percentual de internações com dano;
 * - Gráficos de controle estatístico e análise temporal;
 * - Análise de rendimento e Valor Preditivo Positivo (VPP) dos gatilhos.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class IndicadoresService {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/indicadores';

  /**
   * Obtém os indicadores globais IHI consolidados de acordo com os filtros informados.
   *
   * @param filtros Filtros opcionais (turma, período letivo, unidade, gatilho, datas).
   * @returns Observable com o compilado de indicadores IHI.
   */
  obterIndicadores(filtros: FiltrosIndicadores = {}): Observable<IndicadoresIHI> {
    let params = new HttpParams();
    for (const [chave, valor] of Object.entries(filtros)) {
      if (valor !== undefined && valor !== null && valor !== '') {
        params = params.set(chave, valor.toString());
      }
    }
    return this.http.get<IndicadoresIHI>(this.url, { params });
  }

  /**
   * Obtém os dados tabulares do Quadro Resumo Epidemiológico para auditoria clínica detalhada.
   *
   * @param filtros Filtros aplicáveis na consulta.
   * @returns Observable com resultado paginado do Quadro Resumo.
   */
  obterQuadroResumo(filtros: Record<string, unknown> = {}): Observable<QuadroResumoResultado> {
    let params = new HttpParams();
    for (const [chave, valor] of Object.entries(filtros)) {
      if (valor !== undefined && valor !== null && valor !== '') {
        params = params.set(chave, valor.toString());
      }
    }
    return this.http.get<QuadroResumoResultado>(`${this.url}/quadro-resumo`, {
      params,
    });
  }

  /**
   * Obtém a avaliação analítica do desempenho e eficácia diagnóstica dos gatilhos rastreadores.
   *
   * @param filtros Filtros opcionais de amostragem.
   * @returns Observable com o relatório de rendimento de gatilhos e módulos.
   */
  obterDesempenhoGatilhos(
    filtros: FiltrosIndicadores = {},
  ): Observable<DesempenhoGatilhosResultado> {
    let params = new HttpParams();
    for (const [chave, valor] of Object.entries(filtros)) {
      if (valor !== undefined && valor !== null && valor !== '') {
        params = params.set(chave, valor.toString());
      }
    }
    return this.http.get<DesempenhoGatilhosResultado>(`${this.url}/gatilhos`, {
      params,
    });
  }
}
