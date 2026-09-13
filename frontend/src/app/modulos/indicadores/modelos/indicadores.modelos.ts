/**
 * Ponto de amostragem na série temporal para gráficos de tendência (Run Charts / Shewhart).
 */
export interface SerieTemporalPonto {
  /** Período de competência (ex: 2024-01, 2024-02). */
  periodo: string;
  /** Rótulo legível para o eixo temporal do gráfico. */
  rotulo: string;
  /** Número de prontuários auditados no intervalo. */
  prontuarios: number;
  /** Somatório de dias de internação dos prontuários auditados. */
  dias: number;
  /** Quantidade de eventos adversos detectados. */
  eventos: number;
  /** Taxa canônica IHI: Eventos Adversos por 1.000 pacientes-dia. */
  taxaPorMilDias: number;
  /** Taxa canônica IHI: Eventos Adversos por 100 admissões hospitalares. */
  taxaPorCemAdmissoes: number;
}

/**
 * Métricas de eficácia e acurácia diagnóstica dos gatilhos rastreadores.
 */
export interface EficaciaGatilhos {
  /** Total de gatilhos apontados como positivos nos prontuários. */
  totalGatilhosRastreados: number;
  /** Total de danos reais e eventos adversos confirmados após a detecção dos gatilhos. */
  totalDanosConfirmados: number;
  /** Taxa de conversão/rendimento percentual (VPP - Valor Preditivo Positivo). */
  taxaRendimentoGatilhos: number;
}

/**
 * Contagem e distribuição de eventos adversos por categoria de severidade NCC MERP (E até I).
 */
export interface DistribuicaoSeveridade {
  /** Categoria E: Dano temporário com necessidade de intervenção. */
  CATEGORIA_E: number;
  /** Categoria F: Dano temporário com prolongamento da internação. */
  CATEGORIA_F: number;
  /** Categoria G: Dano permanente ao paciente. */
  CATEGORIA_G: number;
  /** Categoria H: Dano com risco iminente de morte / suporte à vida. */
  CATEGORIA_H: number;
  /** Categoria I: Óbito decorrente do evento adverso. */
  CATEGORIA_I: number;
  [key: string]: number;
}

/**
 * Distribuição percentual ou quantitativa dos eventos adversos pelos módulos clínicos IHI-GTT.
 */
export interface DistribuicaoModulos {
  /** Módulo Cuidados Gerais. */
  CUIDADOS: number;
  /** Módulo Medicamentoso / Farmacêutico. */
  MEDICACAO: number;
  /** Módulo Cirúrgico. */
  CIRURGICO: number;
  /** Módulo Terapia Intensiva. */
  TERAPIA_INTENSIVA: number;
  /** Módulo Perinatal / Obstétrico. */
  PERINATAL: number;
  /** Módulo Urgência e Emergência. */
  URGENCIA: number;
  [key: string]: number;
}

/**
 * Indicadores epidemiológicos e estatísticos globais IHI Global Trigger Tool (IHI-GTT).
 */
export interface IndicadoresIHI {
  /** Total de prontuários médicos analisados nas auditorias. */
  totalProntuariosRevistos: number;
  /** Somatório dos dias de internação de todas as admissões auditadas. */
  totalDiasInternacao: number;
  /** Média do tempo de permanência hospitalar (dias). */
  mediaPermanenciaDias: number;
  /** Contagem absoluta de eventos adversos (danos) identificados. */
  totalEventosAdversos: number;
  /** Danos ocorridos durante a internação hospitalar (intrahospitalares). */
  eventosIntrahospitalares: number;
  /** Danos já presentes no momento da admissão do paciente. */
  eventosPresentesAdmissao: number;
  /** Proporção percentual de danos presentes na admissão. */
  percentualPresenteAdmissao: number;
  /** Proporção percentual de danos intrahospitalares. */
  percentualIntrahospitalar: number;
  /** Quantidade de prontuários com pelo menos 1 dano confirmado. */
  prontuariosComDano: number;
  /** Indicador IHI Primário: Danos por 1.000 pacientes-dia. */
  taxaDanosPorMilDias: number;
  /** Indicador IHI Secundário: Danos por 100 admissões. */
  frequenciaPorCemAdmissoes: number;
  /** Indicador IHI Terciário: Porcentagem de admissões hospitalares com dano. */
  prevalenciaPercentual: number;
  /** Distribuição por categorias de gravidade NCC MERP. */
  distribuicaoSeveridade: DistribuicaoSeveridade;
  /** Distribuição pelos módulos clínicos IHI-GTT. */
  distribuicaoModulos: DistribuicaoModulos;
  /** Pontos de série temporal para geração de gráficos estatísticos de tendência. */
  serieTemporal: SerieTemporalPonto[];
  /** Mediana temporal da taxa por 1.000 dias. */
  medianaTaxaPorMilDias: number;
  /** Mediana temporal da frequência por 100 admissões. */
  medianaTaxaPorCemAdmissoes: number;
  /** Desempenho e eficácia preditiva dos gatilhos. */
  eficaciaGatilhos: EficaciaGatilhos;
}

/**
 * Parâmetros de filtragem multidimensional para relatórios e gráficos epidemiológicos.
 */
export interface FiltrosIndicadores {
  turmaId?: number;
  periodoLetivo?: string;
  cenarioId?: number;
  unidadeId?: number;
  moduloCodigo?: string;
  gravidade?: string;
  danoPresenteAdmissao?: boolean;
  dataInicio?: string;
  dataFim?: string;
}

/**
 * Linha individual do Quadro Resumo Epidemiológico de Auditorias.
 */
export interface QuadroResumoItem {
  revisaoId: number;
  numeroAtendimento: string;
  idadePaciente: number;
  tempoPermanenciaDias: number;
  unidadeHospitalarNome: string;
  unidadeHospitalarSigla: string;
  codigoDisciplina: string;
  periodoLetivo: string;
  alunoAuditorNome: string;
  alunoMatricula: string;
  dataAuditoria?: string;
  professorValidadorNome: string;
  nota?: number;
  totalGatilhos: number;
  gatilhosDetectados: string[];
  totalDanos: number;
  descricoesDanos: string[];
  gravidadeMaxima: string;
  danoPresenteAdmissao: boolean;
}

/**
 * Totais consolidados apresentados no rodapé do Quadro Resumo.
 */
export interface QuadroResumoTotais {
  totalProntuarios: number;
  totalDiasInternacao: number;
  totalEventosAdversos: number;
  prontuariosComDano: number;
  taxaDanosPorMilDias: number;
  frequenciaPorCemAdmissoes: number;
  prevalenciaPercentual: number;
}

/**
 * Resultado paginado do Quadro Resumo Epidemiológico.
 */
export interface QuadroResumoResultado {
  conteudo: QuadroResumoItem[];
  paginaAtual: number;
  tamanhoPagina: number;
  totalElementos: number;
  totalPaginas: number;
  totais: QuadroResumoTotais;
}

/**
 * Métricas analíticas individuais de um gatilho específico.
 */
export interface DesempenhoGatilho {
  gatilhoId: number;
  codigo: string;
  descricao: string;
  moduloCodigo: string;
  moduloNome: string;
  totalPositivos: number;
  totalDanos: number;
  taxaConversaoPercentual: number;
  danosGraves: number;
  presentesAdmissao: number;
  distribuicaoSeveridade: Record<string, number>;
}

/**
 * Métricas de rendimento consolidadas por módulo clínico GTT.
 */
export interface DesempenhoModulo {
  moduloCodigo: string;
  moduloNome: string;
  totalPositivos: number;
  totalDanos: number;
  taxaConversaoPercentual: number;
}

/**
 * Resultado analítico completo do relatório de desempenho e eficácia dos gatilhos.
 */
export interface DesempenhoGatilhosResultado {
  totalGatilhosRastreados: number;
  totalDanosConfirmados: number;
  taxaConversaoGeral: number;
  gatilhos: DesempenhoGatilho[];
  modulos: DesempenhoModulo[];
}
