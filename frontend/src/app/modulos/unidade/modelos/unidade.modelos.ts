/**
 * Representação de um setor assistencial ou enfermaria do Hospital Universitário (HU-UFS / EBSERH).
 */
export interface UnidadeHospitalar {
  /** Identificador único da unidade hospitalar. */
  id: number;
  /** Nome por extenso do setor (ex: Unidade de Terapia Intensiva Adulto). */
  nome: string;
  /** Sigla canônica utilizada em prontuários e auditorias (ex: UTI, CLM, CIR). */
  sigla: string;
  /** Flag que indica se a unidade está ativa para novas auditorias e casos. */
  ativa: boolean;
}

/**
 * Payload para criação ou edição de uma unidade hospitalar.
 */
export interface UnidadeRequisicao {
  /** Nome por extenso do setor. */
  nome: string;
  /** Sigla do setor. */
  sigla: string;
}
