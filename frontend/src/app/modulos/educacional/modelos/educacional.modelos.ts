/**
 * Status do ciclo de vida de uma submissão de auditoria pelo discente.
 */
export type StatusSubmissao = 'EM_ANDAMENTO' | 'SUBMETIDA' | 'AVALIADA';

/**
 * Escala de gravidade de dano ao paciente segundo o NCC MERP (National Coordinating Council for Medication Error Reporting and Prevention).
 * - `CATEGORIA_E`: Dano temporário com necessidade de intervenção.
 * - `CATEGORIA_F`: Dano temporário com prolongamento de hospitalização.
 * - `CATEGORIA_G`: Dano permanente.
 * - `CATEGORIA_H`: Dano com risco iminente de morte (necessidade de intervenção de suporte de vida).
 * - `CATEGORIA_I`: Óbito relacionado ao evento adverso.
 */
export type GravidadeNccMerp =
  'CATEGORIA_E' | 'CATEGORIA_F' | 'CATEGORIA_G' | 'CATEGORIA_H' | 'CATEGORIA_I';

/**
 * Representação de um prontuário clínico simulado para auditoria retrospectiva IHI-GTT no HU-UFS.
 */
export interface CasoClinico {
  /** Identificador único do caso clínico. */
  id: number;
  /** ID do docente criador. */
  professorCriadorId: number;
  /** Nome do docente criador. */
  professorCriadorNome: string;
  /** ID da unidade hospitalar vinculada. */
  unidadeHospitalarId: number;
  /** Nome por extenso do setor assistencial. */
  unidadeHospitalarNome: string;
  /** Sigla do setor (ex: UTI, CLM). */
  unidadeHospitalarSigla: string;
  /** Título do caso clínico e hipótese diagnóstica. */
  titulo: string;
  /** Descrição geral e contextualização do caso. */
  descricaoCaso: string;
  /** Competências e objetivos pedagógicos pretendidos. */
  objetivosAprendizagem: string;
  /** Número fictício de atendimento hospitalar. */
  numeroAtendimento: string;
  /** Idade do paciente simulado. */
  idadePaciente: number;
  /** Data da admissão hospitalar. */
  dataAdmissao: string;
  /** Data da alta ou desfecho hospitalar. */
  dataAlta: string;
  /** Tempo total de permanência do paciente em dias. */
  tempoPermanenciaDias: number;
  /** Sumário de admissão e alta do prontuário. */
  sumarioAlta: string;
  /** Registro de prescrições e medicamentos administrados. */
  prescricoesMedicas: string;
  /** Painel de exames laboratoriais e diagnósticos por imagem. */
  examesLaboratoriais: string;
  /** Relatório e descrição de atos cirúrgicos, quando aplicável. */
  relatorioCirurgico?: string | null;
  /** Evoluções multiprofissionais (médica, enfermagem, fisioterapia, farmácia). */
  evolucoesMultiprofissionais: string;
  /** Data de inclusão do caso no repositório. */
  criadoEm?: string;
}

/**
 * Payload para criação ou edição de um caso clínico simulado.
 */
export interface SalvarCasoClinicoPayload {
  unidadeHospitalarId: number;
  titulo: string;
  descricaoCaso: string;
  objetivosAprendizagem: string;
  numeroAtendimento: string;
  idadePaciente: number;
  dataAdmissao: string;
  dataAlta: string;
  tempoPermanenciaDias: number;
  sumarioAlta: string;
  prescricoesMedicas: string;
  examesLaboratoriais: string;
  relatorioCirurgico?: string | null;
  evolucoesMultiprofissionais: string;
}

/**
 * Atividade curricular de auditoria retrospectiva atribuída a uma turma acadêmica.
 */
export interface AtividadeEducacional {
  /** Identificador da atividade. */
  id: number;
  /** ID da turma acadêmica destinatária. */
  turmaId: number;
  /** Código da disciplina da turma. */
  turmaCodigo: string;
  /** Nome da disciplina. */
  turmaDisciplina: string;
  /** Período letivo correspondente. */
  periodoLetivo?: string;
  /** ID do caso clínico atribuído à auditoria. */
  casoClinicoId: number;
  /** Título do caso clínico associado. */
  casoClinicoTitulo: string;
  /** Título da atividade educacional. */
  titulo: string;
  /** Orientações do docente para condução da auditoria. */
  orientacoesPedagogicas?: string;
  /** Data e hora de abertura da atividade. */
  dataInicio: string;
  /** Prazo limite para envio das auditorias. */
  dataFim: string;
  /** Tempo máximo em minutos para auditoria (padrão IHI-GTT: 20 minutos por prontuário). */
  tempoLimiteMinutos: number;
  /** Flag de vigência da atividade. */
  ativa: boolean;
  /** Data de cadastro da atividade. */
  criadaEm?: string;
  /** Total de alunos matriculados na turma. */
  totalAlunosTurma?: number;
  /** Quantidade de submissões recebidas. */
  totalSubmissoes?: number;
  /** Quantidade de submissões já corrigidas/avaliadas pelo docente. */
  totalAvaliadas?: number;
}

/**
 * Payload para criação ou edição de uma atividade de auditoria.
 */
export interface SalvarAtividadePayload {
  turmaId: number;
  casoClinicoId: number;
  titulo: string;
  orientacoesPedagogicas?: string;
  dataInicio: string;
  dataFim: string;
  tempoLimiteMinutos: number;
  ativa?: boolean;
}

/**
 * Registro de progresso de um aluno individual em uma atividade.
 */
export interface AlunoProgresso {
  alunoId: number;
  alunoNome: string;
  alunoEmail: string;
  alunoMatricula?: string;
  submissaoId?: number | null;
  status?: StatusSubmissao | null;
  tempoGastoSegundos?: number | null;
  dataSubmissao?: string | null;
  nota?: number | null;
  parecerDocente?: string | null;
  dataAvaliacao?: string | null;
}

/**
 * Dados consolidados do painel de desempenho docente da atividade.
 */
export interface PainelAtividade {
  atividade: AtividadeEducacional;
  casoClinico: CasoClinico;
  totalAlunos: number;
  totalSubmissoes: number;
  totalPendentesCorrecao: number;
  totalAvaliadas: number;
  mediaNotas?: number | null;
  alunos: AlunoProgresso[];
}

/**
 * Registro de achado de gatilho positivo rastreado durante a auditoria IHI-GTT.
 */
export interface SubmissaoGatilho {
  id?: number;
  gatilhoId: number;
  gatilhoCodigo?: string;
  gatilhoDescricao?: string;
  moduloCodigo?: string;
  moduloNome?: string;
  categoriaEaId?: number | null;
  categoriaEaNome?: string | null;
  confirmouDano: boolean;
  justificativaDano?: string;
  danoPresenteAdmissao: boolean;
  gravidade?: GravidadeNccMerp | null;
}

/**
 * Ferramenta de Gestão da Qualidade: Diagrama de Causa e Efeito (Ishikawa / 6M).
 */
export interface SubmissaoIshikawa {
  efeitoPrincipal: string;
  metodo?: string;
  maoDeObra?: string;
  material?: string;
  medida?: string;
  meioAmbiente?: string;
  maquina?: string;
}

/**
 * Ferramenta de Gestão da Qualidade: Plano de Ação 5W2H / 5W3H.
 */
export interface SubmissaoPlano5w3h {
  id?: number;
  oQue: string;
  porQue: string;
  quem: string;
  onde: string;
  quando: string;
  como: string;
  quantoCusta?: number | null;
  comoMedir?: string;
}

/**
 * Ferramenta de Gestão da Qualidade: Ciclo de Melhoria Contínua PDCA.
 */
export interface SubmissaoPdca {
  planejar: string;
  fazer: string;
  checar: string;
  agir: string;
}

/**
 * Entidade completa de submissão de auditoria pelo discente.
 */
export interface Submissao {
  id: number;
  atividadeId: number;
  atividadeTitulo: string;
  disciplinaNome: string;
  professorNome: string;
  tempoLimiteMinutos: number;
  casoClinico: CasoClinico;
  alunoId: number;
  alunoNome: string;
  alunoMatricula?: string;
  alunoEmail?: string;
  status: StatusSubmissao;
  tempoGastoSegundos: number;
  dataInicio: string;
  dataSubmissao?: string | null;
  professorCorretorId?: number | null;
  professorCorretorNome?: string | null;
  nota?: number | null;
  parecerDocente?: string | null;
  dataAvaliacao?: string | null;
  achadosGatilhos: SubmissaoGatilho[];
  ishikawa?: SubmissaoIshikawa | null;
  planos5w3h?: SubmissaoPlano5w3h[] | null;
  pdca?: SubmissaoPdca | null;
}

/**
 * Payload de salvamento de progresso ou envio final de auditoria pelo aluno.
 */
export interface SalvarSubmissaoPayload {
  tempoGastoSegundos?: number;
  finalizar: boolean;
  achadosGatilhos: SubmissaoGatilho[];
  ishikawa?: SubmissaoIshikawa | null;
  planos5w3h?: SubmissaoPlano5w3h[] | null;
  pdca?: SubmissaoPdca | null;
}

/**
 * Payload para correção e avaliação pedagógica da submissão pelo docente.
 */
export interface AvaliarSubmissaoPayload {
  nota: number;
  parecerDocente: string;
}

/**
 * Item de listagem no painel do discente "Minhas Atividades".
 */
export interface MinhaAtividadeItem {
  atividadeId: number;
  titulo: string;
  turmaId: number;
  codigoDisciplina: string;
  nomeDisciplina: string;
  professorNome: string;
  casoClinicoId: number;
  casoClinicoTitulo: string;
  unidadeHospitalarSigla: string;
  dataInicio: string;
  dataFim: string;
  tempoLimiteMinutos: number;
  submissaoId?: number | null;
  status?: StatusSubmissao | null;
  nota?: number | null;
  tempoGastoSegundos?: number;
  dataSubmissao?: string | null;
  dataAvaliacao?: string | null;
}
