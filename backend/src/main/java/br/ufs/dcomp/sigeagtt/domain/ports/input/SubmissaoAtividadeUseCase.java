package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.GravidadeNccMerp;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Porta de entrada (Input Port / Use Case) para fluxo de resolução, salvamento e avaliação das
 * submissões de auditoria clínica no SIGEA-GTT.
 */
public interface SubmissaoAtividadeUseCase {

    /**
     * Lista atividades liberadas para o discente autenticado com status da respectiva submissão.
     *
     * @param alunoLogado Discente em sessão.
     * @return Lista de itens de atividades do aluno.
     */
    List<ItemMinhaAtividade> listarMinhasAtividades(Usuario alunoLogado);

    /**
     * Inicia uma nova submissão em rascunho ou recupera uma submissão em andamento.
     *
     * @param atividadeId Identificador da atividade educacional.
     * @param alunoLogado Discente participante.
     * @return Submissão iniciada ou em andamento.
     */
    SubmissaoAtividade iniciarOuContinuar(Long atividadeId, Usuario alunoLogado);

    /**
     * Busca uma submissão pelo ID com validação de perfil (aluno autor, docente ou admin).
     *
     * @param id Identificador da submissão.
     * @param usuarioLogado Usuário autenticado.
     * @return Submissão com todos os 4 componentes pedagógicos carregados.
     */
    SubmissaoAtividade buscarPorId(Long id, Usuario usuarioLogado);

    /**
     * Salva o rascunho ou entrega a resolução definitiva do discente.
     *
     * @param id Identificador da submissão.
     * @param alunoLogado Discente autor.
     * @param comando Dados completos da resolução (gatilhos, Ishikawa, 5W3H e PDCA).
     * @return Submissão salva ou submetida.
     */
    SubmissaoAtividade salvarOuSubmeter(Long id, Usuario alunoLogado, DadosSalvarSubmissao comando);

    /**
     * Registra a correção docente atribuindo nota e parecer pedagógico formativo.
     *
     * @param id Identificador da submissão.
     * @param professorLogado Docente avaliador.
     * @param nota Nota atribuída (0.00 a 10.00).
     * @param parecerDocente Parecer qualitativo do professor.
     * @return Submissão avaliada e homologada.
     */
    SubmissaoAtividade avaliar(
            Long id, Usuario professorLogado, BigDecimal nota, String parecerDocente);

    /**
     * Lista todas as submissões entregues pendentes de correção para o docente ou administrador.
     *
     * @param professorLogado Docente autenticado.
     * @return Lista de submissões aguardando avaliação.
     */
    List<SubmissaoAtividade> listarPendentesCorrecao(Usuario professorLogado);

    /**
     * Lista todas as categorias de eventos adversos ativas para preenchimento de achados.
     *
     * @return Lista de categorias ativas.
     */
    List<br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso> listarCategoriasAtivas();

    /** Dados de item de atividade listada para o discente. */
    record ItemMinhaAtividade(
            Long atividadeId,
            String titulo,
            Long turmaId,
            String codigoDisciplina,
            String nomeDisciplina,
            String professorNome,
            Long casoClinicoId,
            String casoClinicoTitulo,
            String unidadeSigla,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Integer tempoLimiteMinutos,
            Long submissaoId,
            StatusSubmissao status,
            BigDecimal nota,
            Integer tempoGastoSegundos,
            LocalDateTime dataSubmissao,
            LocalDateTime dataAvaliacao) {}

    /** Dados de um gatilho clínico identificado na submissão. */
    record DadosGatilho(
            Long gatilhoId,
            Long categoriaEventoAdversoId,
            Boolean confirmouDano,
            String justificativaDano,
            Boolean danoPresenteAdmissao,
            GravidadeNccMerp gravidade) {}

    /** Dados do Diagrama de Ishikawa 6M na submissão. */
    record DadosIshikawa(
            String efeitoPrincipal,
            String metodo,
            String maoDeObra,
            String material,
            String medida,
            String meioAmbiente,
            String maquina) {}

    /** Dados de um item do Plano de Ação 5W3H. */
    record DadosPlano5w3h(
            String oQue,
            String porQue,
            String quem,
            String onde,
            String quando,
            String como,
            BigDecimal quantoCusta,
            String comoMedir) {}

    /** Dados do Ciclo PDCA na submissão. */
    record DadosPdca(String planejar, String fazer, String checar, String agir) {}

    /** Dados completos para persistência de submissão discente. */
    record DadosSalvarSubmissao(
            Boolean finalizar,
            Integer tempoGastoSegundos,
            List<DadosGatilho> gatilhos,
            DadosIshikawa ishikawa,
            List<DadosPlano5w3h> planos5w3h,
            DadosPdca pdca) {}
}
