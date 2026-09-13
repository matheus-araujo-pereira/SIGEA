/**
 * Representação de uma turma acadêmica de disciplina hospitalar no SIGEA-GTT.
 */
export interface Turma {
  /** Identificador único da turma no banco de dados. */
  id: number;
  /** Código oficial da disciplina (ex: MED-0102, ENF-0301). */
  codigoDisciplina: string;
  /** Período letivo correspondente (ex: 2024.1, 2024.2). */
  periodoLetivo: string;
  /** Representação canônica do ano e semestre. */
  anoSemestre: string;
  /** Status da turma (ativa/encerrada). */
  ativa: boolean;
  /** Data e hora de cadastro da turma. */
  criadaEm: string;
  /** ID do docente responsável pela turma. */
  professorResponsavelId: number;
  /** Nome completo do docente responsável. */
  professorResponsavelNome: string;
  /** Nome ou ementa da disciplina associada. */
  nomeDisciplina?: string;
  /** Contagem total de discentes matriculados. */
  totalAlunos: number;
}

/**
 * Payload para criação ou edição de uma turma acadêmica.
 */
export interface TurmaRequisicao {
  /** Código da disciplina. */
  codigoDisciplina: string;
  /** Período letivo no formato `AAAA.S`. */
  periodoLetivo: string;
  /** Identificador ano/semestre. */
  anoSemestre: string;
  /** ID do docente responsável cadastrado no sistema. */
  professorResponsavelId: number;
}
