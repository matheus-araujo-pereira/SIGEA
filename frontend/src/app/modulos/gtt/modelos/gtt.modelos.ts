/**
 * Representação de um módulo clínico da metodologia IHI Global Trigger Tool (IHI-GTT).
 * Exemplos canônicos: Cuidados Gerais (C), Medicamentoso (M), Cirúrgico (S), Terapia Intensiva (I), Perinatal (P), Emergência (E).
 */
export interface ModuloGtt {
  /** Identificador único do módulo. */
  id: number;
  /** Código ou letra identificadora da categoria (ex: C, M, S, I). */
  codigo: string;
  /** Nome por extenso do módulo clínico. */
  nome: string;
  /** Descrição detalhada do escopo clínico do módulo. */
  descricao?: string;
  /** Status do módulo no sistema (ativo/inativo). */
  ativo: boolean;
  /** Data e hora de inclusão no catálogo. */
  criadoEm?: string;
}

/**
 * Payload para criação ou edição de um módulo clínico GTT.
 */
export interface ModuloRequisicao {
  /** Código ou sigla do módulo. */
  codigo: string;
  /** Nome por extenso do módulo. */
  nome: string;
  /** Descrição do escopo clínico do módulo. */
  descricao?: string;
}

/**
 * Representação de um gatilho canônico da metodologia Global Trigger Tool (IHI-GTT).
 *
 * Utilizado por auditores clínicos para busca ativa retrospectiva de eventos adversos (EA)
 * em prontuários hospitalares.
 */
export interface GatilhoGtt {
  /** Identificador único do gatilho. */
  id: number;
  /** Código do gatilho (ex: C-01, M-02, S-05). */
  codigo: string;
  /** Módulo clínico associado (se retornado como objeto). */
  modulo?: ModuloGtt;
  /** Identificador do módulo clínico associado (quando retornado plano). */
  moduloId?: number;
  /** Nome do módulo clínico associado (quando retornado plano). */
  moduloNome?: string;
  /** Descrição operacional e evento que o gatilho rastreia. */
  descricao: string;
  /** Limiar ou valor de corte para disparo do gatilho (ex: INR > 5.0, Creatinina > 2x valor basal). */
  limiarReferencia?: string;
  /** Status de vigência do rastreador no catálogo. */
  ativo: boolean;
}

/**
 * Payload para criação ou edição de um gatilho IHI-GTT.
 */
export interface GatilhoRequisicao {
  /** Código do gatilho (ex: C-01, M-04). */
  codigo: string;
  /** ID do módulo clínico ao qual o gatilho pertence. */
  moduloId: number;
  /** Descrição operacional do rastreador. */
  descricao: string;
  /** Limiar ou parâmetro laboratorial/clínico de corte. */
  limiarReferencia?: string;
}
