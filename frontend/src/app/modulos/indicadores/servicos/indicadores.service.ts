import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
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
    return this.http.get<Record<string, unknown>>(this.url, { params }).pipe(
      map((dados: Record<string, unknown>) => {
        const raw = dados as Record<string, unknown>;
        const totalProntuarios = Number(
          raw['totalProntuariosRevistos'] ?? raw['totalProntuariosAuditados'] ?? 0,
        );
        const taxaMil = Number(raw['taxaDanosPorMilDias'] ?? raw['taxaEaPorMilDias'] ?? 0);
        const freqCem = Number(
          raw['frequenciaPorCemAdmissoes'] ?? raw['taxaEaPorCemInternacoes'] ?? 0,
        );
        const prev = Number(raw['prevalenciaPercentual'] ?? raw['percentualInternacoesComEa'] ?? 0);
        const medDias = Number(raw['mediaPermanenciaDias'] ?? raw['mediaDiasPermanencia'] ?? 0);

        const serieRaw = (raw['serieTemporal'] as Record<string, unknown>[]) || [];
        const serieTemporal = serieRaw.map((p) => ({
          periodo: String(p['periodo'] ?? ''),
          rotulo: String(p['rotulo'] ?? ''),
          prontuarios: Number(p['prontuarios'] ?? 0),
          dias: Number(p['dias'] ?? 0),
          eventos: Number(p['eventos'] ?? 0),
          taxaPorMilDias: Number(p['taxaPorMilDias'] ?? 0),
          taxaPorCemAdmissoes: Number(p['taxaPorCemAdmissoes'] ?? p['taxaPorCemInternacoes'] ?? 0),
        }));

        const rawEficacia = (raw['eficaciaGatilhos'] as Record<string, unknown>) || {};
        const eficaciaGatilhos = {
          totalGatilhosRastreados: Number(rawEficacia['totalGatilhosRastreados'] ?? 0),
          totalDanosConfirmados: Number(rawEficacia['totalDanosConfirmados'] ?? 0),
          taxaRendimentoGatilhos: Number(
            rawEficacia['taxaRendimentoGatilhos'] ?? raw['razaoRendimentoGatilho'] ?? 0,
          ),
        };

        const distSev = (raw['distribuicaoSeveridade'] ??
          raw['distribuicaoSeveridadeNccMerp'] ??
          {}) as Record<string, number>;
        const distMod = (raw['distribuicaoModulos'] ?? {}) as Record<string, number>;

        return {
          totalProntuariosRevistos: totalProntuarios,
          totalDiasInternacao: Number(raw['totalDiasInternacao'] ?? 0),
          mediaPermanenciaDias: medDias,
          totalEventosAdversos: Number(raw['totalEventosAdversos'] ?? 0),
          eventosIntrahospitalares: Number(raw['eventosIntrahospitalares'] ?? 0),
          eventosPresentesAdmissao: Number(raw['eventosPresentesAdmissao'] ?? 0),
          percentualPresenteAdmissao: Number(raw['percentualPresenteAdmissao'] ?? 0),
          percentualIntrahospitalar: Number(raw['percentualIntrahospitalar'] ?? 0),
          prontuariosComDano: Number(raw['prontuariosComDano'] ?? 0),
          taxaDanosPorMilDias: taxaMil,
          frequenciaPorCemAdmissoes: freqCem,
          prevalenciaPercentual: prev,
          distribuicaoSeveridade: {
            CATEGORIA_E: distSev['CATEGORIA_E'] ?? 0,
            CATEGORIA_F: distSev['CATEGORIA_F'] ?? 0,
            CATEGORIA_G: distSev['CATEGORIA_G'] ?? 0,
            CATEGORIA_H: distSev['CATEGORIA_H'] ?? 0,
            CATEGORIA_I: distSev['CATEGORIA_I'] ?? 0,
          },
          distribuicaoModulos: {
            CUIDADOS: distMod['CUIDADOS'] ?? 0,
            MEDICACAO: distMod['MEDICACAO'] ?? 0,
            CIRURGICO: distMod['CIRURGICO'] ?? 0,
            TERAPIA_INTENSIVA: distMod['TERAPIA_INTENSIVA'] ?? 0,
            PERINATAL: distMod['PERINATAL'] ?? 0,
            URGENCIA: distMod['URGENCIA'] ?? 0,
          },
          serieTemporal,
          medianaTaxaPorMilDias: Number(raw['medianaTaxaPorMilDias'] ?? taxaMil),
          medianaTaxaPorCemAdmissoes: Number(raw['medianaTaxaPorCemAdmissoes'] ?? freqCem),
          eficaciaGatilhos,
        } as IndicadoresIHI;
      }),
    );
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
    return this.http.get<Record<string, unknown>>(`${this.url}/quadro-resumo`, { params }).pipe(
      map((raw: Record<string, unknown>) => {
        const itensBrutos = (raw['conteudo'] ?? raw['itens'] ?? []) as Record<string, unknown>[];
        const conteudo = itensBrutos.map((item) => ({
          revisaoId: Number(item['revisaoId'] ?? item['submissaoId'] ?? 0),
          numeroAtendimento: String(item['numeroAtendimento'] ?? item['prontuario'] ?? ''),
          idadePaciente: Number(item['idadePaciente'] ?? 0),
          tempoPermanenciaDias: Number(item['tempoPermanenciaDias'] ?? 1),
          unidadeHospitalarNome: String(item['unidadeHospitalarNome'] ?? item['unidade'] ?? ''),
          unidadeHospitalarSigla: String(item['unidadeHospitalarSigla'] ?? item['unidade'] ?? ''),
          codigoDisciplina: String(item['codigoDisciplina'] ?? item['turma'] ?? ''),
          periodoLetivo: String(item['periodoLetivo'] ?? ''),
          alunoAuditorNome: String(item['alunoAuditorNome'] ?? item['auditorDiscente'] ?? ''),
          alunoMatricula: String(item['alunoMatricula'] ?? ''),
          dataAuditoria: item['dataAuditoria'] ? String(item['dataAuditoria']) : undefined,
          professorValidadorNome: String(item['professorValidadorNome'] ?? ''),
          nota: item['nota'] !== undefined ? Number(item['nota']) : undefined,
          totalGatilhos: Number(item['totalGatilhos'] ?? item['gatilhosDetectados'] ?? 0),
          gatilhosDetectados: (item['codigosGatilhos'] ??
            item['gatilhosDetectados'] ??
            []) as string[],
          totalDanos: Number(item['totalDanos'] ?? item['eventosAdversosConfirmados'] ?? 0),
          descricoesDanos: (item['descricoesDanos'] ?? []) as string[],
          gravidadeMaxima: String(item['gravidadeMaxima'] ?? '-'),
          danoPresenteAdmissao: Boolean(item['danoPresenteAdmissao']),
        }));

        const totalElementos = Number(raw['totalElementos'] ?? raw['total'] ?? conteudo.length);
        const paginaAtual = Number(raw['paginaAtual'] ?? raw['pagina'] ?? 0);
        const tamanhoPagina = Number(raw['tamanhoPagina'] ?? raw['tamanho'] ?? 10);
        const totalPaginas = Number(
          raw['totalPaginas'] ?? Math.ceil(totalElementos / tamanhoPagina),
        );

        const totaisRaw = (raw['totais'] as Record<string, unknown>) || {};
        const totais = {
          totalProntuarios: Number(totaisRaw['totalProntuarios'] ?? totalElementos),
          totalDiasInternacao: Number(totaisRaw['totalDiasInternacao'] ?? 0),
          totalEventosAdversos: Number(totaisRaw['totalEventosAdversos'] ?? 0),
          prontuariosComDano: Number(totaisRaw['prontuariosComDano'] ?? 0),
          taxaDanosPorMilDias: Number(totaisRaw['taxaDanosPorMilDias'] ?? 0),
          frequenciaPorCemAdmissoes: Number(totaisRaw['frequenciaPorCemAdmissoes'] ?? 0),
          prevalenciaPercentual: Number(totaisRaw['prevalenciaPercentual'] ?? 0),
        };

        return {
          conteudo,
          paginaAtual,
          tamanhoPagina,
          totalElementos,
          totalPaginas,
          totais,
        } as QuadroResumoResultado;
      }),
    );
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
    return this.http.get<Record<string, unknown>>(`${this.url}/gatilhos`, { params }).pipe(
      map((raw: Record<string, unknown>) => {
        const gatilhosRaw = (raw['gatilhos'] ?? []) as Record<string, unknown>[];
        const gatilhos = gatilhosRaw.map((g) => ({
          gatilhoId: Number(g['gatilhoId'] ?? 0),
          codigo: String(g['codigo'] ?? ''),
          descricao: String(g['descricao'] ?? ''),
          moduloCodigo: String(g['moduloCodigo'] ?? g['modulo'] ?? ''),
          moduloNome: String(g['moduloNome'] ?? g['modulo'] ?? ''),
          totalPositivos: Number(g['totalPositivos'] ?? g['rastreamentos'] ?? 0),
          totalDanos: Number(g['totalDanos'] ?? g['danosConfirmados'] ?? 0),
          taxaConversaoPercentual: Number(
            g['taxaConversaoPercentual'] ?? g['valorPreditivoPositivo'] ?? 0,
          ),
          danosGraves: Number(g['danosGraves'] ?? 0),
          presentesAdmissao: Number(g['presentesAdmissao'] ?? 0),
          distribuicaoSeveridade: (g['distribuicaoSeveridade'] ?? {}) as Record<string, number>,
        }));

        const modulosRaw = (raw['modulos'] ?? []) as Record<string, unknown>[];
        const modulos = modulosRaw.map((m) => ({
          moduloCodigo: String(m['moduloCodigo'] ?? ''),
          moduloNome: String(m['moduloNome'] ?? ''),
          totalPositivos: Number(m['totalPositivos'] ?? 0),
          totalDanos: Number(m['totalDanos'] ?? 0),
          taxaConversaoPercentual: Number(m['taxaConversaoPercentual'] ?? 0),
        }));

        const totalGatilhosRastreados = Number(
          raw['totalGatilhosRastreados'] ??
            gatilhos.reduce((acc, curr) => acc + curr.totalPositivos, 0),
        );
        const totalDanosConfirmados = Number(
          raw['totalDanosConfirmados'] ?? gatilhos.reduce((acc, curr) => acc + curr.totalDanos, 0),
        );
        const taxaConversaoGeral = Number(
          raw['taxaConversaoGeral'] ??
            (totalGatilhosRastreados > 0
              ? (totalDanosConfirmados / totalGatilhosRastreados) * 100
              : 0),
        );

        return {
          totalGatilhosRastreados,
          totalDanosConfirmados,
          taxaConversaoGeral,
          gatilhos,
          modulos,
        } as DesempenhoGatilhosResultado;
      }),
    );
  }
}
