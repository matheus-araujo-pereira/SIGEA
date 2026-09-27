/**
 * @file activity.model.ts
 * @description Modelos de dados e contratos DTO para Atividades Pedagógicas, Prontuários Simulados (JSONB), Ferramentas da Qualidade e Submissões de Auditoria GTT.
 * @module ActivityModel
 */

/**
 * Registro cronológico de evolução clínica multiprofissional do prontuário simulado.
 */
export interface EvolutionNoteData {
  /** Data e hora da evolução (ex: '2026-03-10T14:30:00Z') */
  dateTime: string;
  /** Papel profissional responsável (ex: 'Médico Assistente', 'Enfermeiro') */
  professionalRole: string;
  /** Texto descritivo da anotação clínica */
  note: string;
}

/**
 * Item de prescrição medicamentosa ou terapêutica no prontuário simulado.
 */
export interface PrescriptionData {
  /** Nome comercial ou princípio ativo do fármaco */
  medication: string;
  /** Posologia e dose prescrita (ex: '1g IV') */
  dosage: string;
  /** Via de administração (ex: 'Endovenosa', 'Oral') */
  route: string;
  /** Intervalo e frequência de administração (ex: '12/12h') */
  frequency: string;
  /** Checagem de enfermagem e horário de aprazamento/administração */
  administrationCheck?: string;
}

/**
 * Resultado de exame laboratorial ou complementar no prontuário simulado.
 */
export interface LabExamData {
  /** Nome do exame (ex: 'Creatinina Sérica', 'Hemoglobina') */
  examName: string;
  /** Valor ou laudo obtido (ex: '2.4 mg/dL') */
  result: string;
  /** Faixa de referência fisiológica padrão (ex: '0.7 - 1.3 mg/dL') */
  referenceValue?: string;
  /** Data e hora da coleta ou liberação do laudo */
  date?: string;
}

/**
 * Registro de intervenção cirúrgica ou procedimento invasivo realizado.
 */
export interface ProcedureData {
  /** Denominação do procedimento ou cirurgia (ex: 'Apendicectomia Videolaparoscópica') */
  procedureName: string;
  /** Descrição sumária da técnica, intercorrências ou achados */
  description: string;
  /** Data e hora de realização */
  date?: string;
}

/**
 * Prontuário Clínico Simulado completo armazenado em formato estruturado (JSONB).
 * Totalmente fictício em conformidade com o parecer ético CEP/UFS.
 */
export interface ClinicalCaseData {
  /** Nome fictício do paciente (ex: 'Givaldo Santos (Simulado)') */
  patientName: string;
  /** Idade em anos */
  age?: number;
  /** Sexo biológico (ex: 'M', 'F') */
  gender?: string;
  /** Identificação do leito ou enfermaria */
  bed?: string;
  /** Data e hora de admissão hospitalar */
  admissionDate?: string;
  /** Tempo total de internação em dias-paciente para cálculo de taxas GTT */
  patientDays?: number;
  /** História clínica resumida e queixa principal de admissão */
  admissionNotes?: string;
  /** Lista cronológica de anotações multiprofissionais */
  evolutionNotes?: EvolutionNoteData[];
  /** Prescrições médicas registradas no prontuário */
  prescriptions?: PrescriptionData[];
  /** Exames complementares e laboratoriais realizados */
  labExams?: LabExamData[];
  /** Cirurgias e procedimentos invasivos executados */
  procedures?: ProcedureData[];
}

/**
 * Gatilho Clínico identificado pelo auditor discente durante a resolução da atividade.
 */
export interface IdentifiedTriggerData {
  /** Identificador opcional UUID do gatilho cadastrado */
  triggerId?: string;
  /** Código canônico do gatilho (ex: 'C1', 'M5', 'S2') */
  triggerCode: string;
  /** Nome descritivo do gatilho */
  triggerName?: string;
  /** Código do módulo assistencial vinculado (ex: 'C', 'M') */
  moduleCode?: string;
  /** Nome do módulo assistencial */
  moduleName?: string;
  /** Notas e trecho do prontuário onde o gatilho foi localizado */
  notes?: string;
  /** Justificativa clínica da detecção */
  clinicalJustification?: string;
  /** Racional analítico de suporte */
  rationale?: string;
  /** Indica se o gatilho culminou em Dano Real / Evento Adverso */
  isHarm?: boolean;
  /** Letra de gravidade NCC MERP atribuída ('E' a 'I' se isHarm=true, ou 'A' a 'D') */
  harmSeverityLetter?: string;
  /** Categoria descritiva de gravidade */
  harmCategory?: string;
  /** Descrição detalhada da gravidade */
  harmSeverityDescription?: string;
}

/**
 * Diagrama de Causa e Efeito (Ishikawa / 6M) estruturado.
 */
export interface IshikawaData {
  /** Definição do problema central / evento adverso */
  centralProblem?: string;
  /** Problema resumido */
  problem?: string;
  /** Método de trabalho */
  method?: string;
  /** Mão de obra / capacitação */
  manpower?: string;
  /** Materiais e insumos */
  material?: string;
  /** Máquinas e equipamentos */
  machine?: string;
  /** Meio ambiente e infraestrutura */
  environment?: string;
  /** Medidas e monitoramento */
  measurement?: string;
  /** Lista detalhada de causas em Método */
  methodCauses?: string[];
  /** Lista detalhada de causas em Mão de Obra */
  manpowerCauses?: string[];
  /** Lista detalhada de causas em Material */
  materialCauses?: string[];
  /** Lista detalhada de causas em Máquina */
  machineCauses?: string[];
  /** Lista detalhada de causas em Meio Ambiente */
  environmentCauses?: string[];
  /** Lista detalhada de causas em Medida */
  measurementCauses?: string[];
}

/**
 * Item individual da Matriz de Priorização GUT (Gravidade, Urgência, Tendência).
 */
export interface GutItemData {
  /** Descrição do problema priorizado */
  problem: string;
  /** Gravidade do impacto (1 a 5) */
  gravity: number;
  /** Urgência temporal de resolução (1 a 5) */
  urgency: number;
  /** Tendência de agravamento se nada for feito (1 a 5) */
  trend: number;
  /** Alias legado para tendência */
  tendency?: number;
  /** Escore ponderado (G × U × T: 1 a 125) */
  score?: number;
}

/**
 * Plano de Ação 5W2H para intervenção corretiva sobre os eventos adversos.
 */
export interface FiveWTwoHItemData {
  /** What: O que será feito */
  what: string;
  /** Why: Por que será feito / justificativa */
  why: string;
  /** Where: Onde será implementado */
  where: string;
  /** When: Quando / prazo limite */
  when: string;
  /** Who: Quem é o responsável */
  who: string;
  /** How: Como será executado */
  how: string;
  /** How much: Quanto custará / recursos necessários */
  howMuch: string;
}

/**
 * Ciclo PDCA (Plan, Do, Check, Act) para melhoria contínua de segurança do paciente.
 */
export interface PdcaData {
  /** Fase de Planejamento (Plan) */
  plan?: string;
  /** Fase de Execução (Do) */
  doPhase?: string;
  /** Ações práticas de execução */
  doAction?: string;
  /** Fase de Verificação (Check) */
  checkPhase?: string;
  /** Critérios de auditoria e checagem */
  checkAction?: string;
  /** Fase de Ação Corretiva / Padronização (Act) */
  actPhase?: string;
  /** Padronização dos processos */
  act?: string;
}

/**
 * Análise SWOT (Forças, Fraquezas, Oportunidades, Ameaças) do cenário institucional.
 */
export interface SwotData {
  /** Forças internas (Strengths) */
  strengths?: string[];
  /** Fraquezas internas (Weaknesses) */
  weaknesses?: string[];
  /** Oportunidades externas (Opportunities) */
  opportunities?: string[];
  /** Ameaças externas (Threats) */
  threats?: string[];
}

/**
 * Conjunto completo das Ferramentas da Qualidade anexadas à auditoria.
 */
export interface QualityToolsData {
  /** Diagrama de Ishikawa */
  ishikawa?: IshikawaData;
  /** Itens da Matriz GUT */
  gutItems?: GutItemData[];
  /** Itens do Plano 5W2H */
  fiveWTwoHItems?: FiveWTwoHItemData[];
  /** Ciclo PDCA */
  pdca?: PdcaData;
  /** Análise SWOT */
  swot?: SwotData;
  /** Anotações de brainstorming livre */
  brainstormingNotes?: string[];
}

/**
 * DTO de resposta para uma Submissão de Atividade realizada por um estudante.
 */
export interface SubmissionResponseDTO {
  /** Identificador único da submissão */
  id: string;
  /** Identificador da atividade vinculada */
  activityId: string;
  /** Título da atividade vinculada */
  activityTitle: string;
  /** UUID do estudante */
  studentId: string;
  /** Nome completo do estudante */
  studentName: string;
  /** E-mail acadêmico institucional */
  studentEmail: string;
  /** Matrícula acadêmica UFS */
  studentRegistrationNumber?: string;
  /** Lista de gatilhos clínicos identificados na auditoria */
  identifiedTriggers: IdentifiedTriggerData[];
  /** Ferramentas de qualidade estruturadas */
  qualityToolsData?: QualityToolsData;
  /** Alias legado para ferramentas de qualidade */
  qualityTools?: QualityToolsData;
  /** Data e hora ISO da submissão */
  submissionDate: string;
  /** Nota atribuída pelo docente (0 a 10) */
  grade?: number | null;
  /** Parecer e feedback qualitativo docente */
  professorFeedback?: string;
  /** Feedback pedagógico estruturado */
  pedagogicalFeedback?: string | null;
  /** Data e hora da correção */
  gradedAt?: string;
  /** Indica se a atividade já foi corrigida */
  isGraded: boolean;
}

/**
 * DTO para criação / submissão discente de uma resolução de atividade.
 */
export interface SubmissionCreateDTO {
  /** Lista de gatilhos identificados com classificação de dano */
  identifiedTriggers: IdentifiedTriggerData[];
  /** Ferramentas de qualidade aplicadas na análise de causa-raiz */
  qualityToolsData: QualityToolsData;
}

/**
 * DTO para avaliação e lançamento de nota docente em uma submissão.
 */
export interface SubmissionGradeDTO {
  /** Nota de 0 a 10 */
  grade: number;
  /** Feedback textual docente */
  professorFeedback?: string;
  /** Feedback pedagógico adicional */
  pedagogicalFeedback?: string;
}

/**
 * DTO resumido para listagem de Atividades vinculadas a turmas.
 */
export interface ActivityResponseDTO {
  /** UUID da atividade */
  id: string;
  /** UUID da turma acadêmica */
  classId: string;
  /** Alias acadêmico */
  academicClassId?: string;
  /** Nome da turma acadêmica */
  className: string;
  /** Alias do nome da turma */
  academicClassName?: string;
  /** Título da atividade clínica */
  title: string;
  /** Instruções e objetivos pedagógicos */
  description: string;
  /** Prazo limite ISO para entrega */
  deadline: string;
  /** Indica se o prazo limite já expirou */
  isExpired: boolean;
  /** Quantidade de submissões recebidas */
  submissionCount: number;
  /** Data de cadastro */
  createdAt?: string;
}

/**
 * DTO detalhado com prontuário clínico e submissão discente se existente.
 */
export interface ActivityDetailDTO {
  /** UUID da atividade */
  id: string;
  /** UUID da turma */
  classId: string;
  /** Alias da turma */
  academicClassId?: string;
  /** Nome da turma */
  className: string;
  /** Alias do nome da turma */
  academicClassName?: string;
  /** Título da atividade */
  title: string;
  /** Descrição / Instruções */
  description: string;
  /** Prontuário clínico simulado completo */
  clinicalCaseData: ClinicalCaseData;
  /** Prazo de entrega */
  deadline: string;
  /** Indica se expirou */
  isExpired: boolean;
  /** Contagem de submissões */
  submissionCount: number;
  /** Data de criação */
  createdAt: string;
  /** Submissão do estudante autenticado (se houver) */
  studentSubmission?: SubmissionResponseDTO;
}

/**
 * DTO para criação docente de uma nova atividade pedagógica.
 */
export interface ActivityCreateDTO {
  /** UUID da turma à qual a atividade pertence */
  classId: string;
  /** Título da atividade */
  title: string;
  /** Descrição e orientações pedagógicas */
  description: string;
  /** Dados estruturados do prontuário simulado */
  clinicalCaseData: ClinicalCaseData;
  /** Data e hora limite para submissão */
  deadline: string;
}

/**
 * DTO para atualização de uma atividade existente.
 */
export interface ActivityUpdateDTO {
  /** Título atualizado */
  title: string;
  /** Descrição atualizada */
  description: string;
  /** Prontuário simulado atualizado */
  clinicalCaseData: ClinicalCaseData;
  /** Novo prazo limite */
  deadline: string;
}

/**
 * Alias de compatibilidade DTO para os dados consolidados das ferramentas da qualidade.
 */
export type QualityToolsDataDTO = QualityToolsData;

/**
 * Alias de compatibilidade DTO para o registro de gatilho clínico identificado na auditoria.
 */
export type IdentifiedTriggerDTO = IdentifiedTriggerData;


