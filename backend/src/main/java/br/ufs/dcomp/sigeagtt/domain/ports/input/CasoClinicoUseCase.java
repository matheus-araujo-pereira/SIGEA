package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import java.time.LocalDate;
import java.util.List;

/**
 * Porta de entrada (Input Port / Use Case) para gerenciamento de casos clínicos e prontuários
 * simulados no SIGEA-GTT.
 */
public interface CasoClinicoUseCase {

    /**
     * Lista casos clínicos visíveis para o usuário autenticado.
     *
     * @param usuarioLogado Usuário em sessão (ADMINISTRADOR vê todos; PROFESSOR vê os seus).
     * @return Lista de casos clínicos.
     */
    List<CasoClinico> listar(Usuario usuarioLogado);

    /**
     * Busca um caso clínico pelo ID.
     *
     * @param id Identificador do caso clínico.
     * @return Caso clínico localizado.
     */
    CasoClinico buscarPorId(Long id);

    /**
     * Cadastra um novo caso clínico hospitalar.
     *
     * @param professorLogado Docente autor.
     * @param comando Dados do prontuário simulado a cadastrar.
     * @return Caso clínico cadastrado.
     */
    CasoClinico salvar(Usuario professorLogado, DadosSalvarCasoClinico comando);

    /**
     * Atualiza dados de um caso clínico existente com verificação de autoria/permissão.
     *
     * @param id Identificador do caso.
     * @param usuarioLogado Usuário solicitante.
     * @param comando Novos dados do prontuário simulado.
     * @return Caso clínico atualizado.
     */
    CasoClinico atualizar(Long id, Usuario usuarioLogado, DadosSalvarCasoClinico comando);

    /**
     * Exclui um caso clínico com validação de autoria docente.
     *
     * @param id Identificador do caso.
     * @param usuarioLogado Usuário solicitante.
     */
    void excluir(Long id, Usuario usuarioLogado);

    /**
     * Comando de dados para criação ou edição de caso clínico.
     *
     * @param unidadeHospitalarId Unidade hospitalar de internação.
     * @param titulo Título de referência.
     * @param descricaoCaso Resumo clínico.
     * @param objetivosAprendizagem Objetivos instrucionais.
     * @param numeroAtendimento Número do prontuário fictício.
     * @param idadePaciente Idade do paciente.
     * @param dataAdmissao Data de internação.
     * @param dataAlta Data de encerramento da internação.
     * @param tempoPermanenciaDias Tempo de permanência em dias.
     * @param sumarioAlta Resumo médico de alta.
     * @param prescricoesMedicas Prescrições farmacológicas.
     * @param examesLaboratoriais Resultados de exames.
     * @param relatorioCirurgico Descrição cirúrgica (opcional).
     * @param evolucoesMultiprofissionais Evoluções da equipe multiprofissional.
     */
    record DadosSalvarCasoClinico(
            Long unidadeHospitalarId,
            String titulo,
            String descricaoCaso,
            String objetivosAprendizagem,
            String numeroAtendimento,
            Integer idadePaciente,
            LocalDate dataAdmissao,
            LocalDate dataAlta,
            Integer tempoPermanenciaDias,
            String sumarioAlta,
            String prescricoesMedicas,
            String examesLaboratoriais,
            String relatorioCirurgico,
            String evolucoesMultiprofissionais) {}
}
