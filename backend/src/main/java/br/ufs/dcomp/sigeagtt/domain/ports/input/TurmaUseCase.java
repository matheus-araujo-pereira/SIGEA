package br.ufs.dcomp.sigeagtt.domain.ports.input;

import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import java.util.List;

/**
 * Porta de entrada (Input Port / Use Case) para gerenciamento de turmas e matrículas no SIGEA-GTT.
 */
public interface TurmaUseCase {

    /**
     * Lista turmas cadastradas, opcionalmente filtradas por docente.
     *
     * @param professorId Identificador do docente (opcional).
     * @return Lista de turmas com o respectivo total de alunos matriculados.
     */
    List<ItemTurmaComTotal> listar(Long professorId);

    /**
     * Busca uma turma pelo ID.
     *
     * @param id Identificador da turma.
     * @return Turma com total de alunos matriculados.
     */
    ItemTurmaComTotal buscarPorId(Long id);

    /**
     * Cadastra uma nova turma acadêmica.
     *
     * @param professorResponsavelId Docente responsável.
     * @param codigoDisciplina Código da disciplina.
     * @param periodoLetivo Período acadêmico.
     * @param anoSemestre Identificador do semestre.
     * @return Turma cadastrada.
     */
    ItemTurmaComTotal cadastrar(
            Long professorResponsavelId,
            String codigoDisciplina,
            String periodoLetivo,
            String anoSemestre);

    /**
     * Edita dados de uma turma existente.
     *
     * @param id Identificador da turma.
     * @param professorResponsavelId Novo docente responsável.
     * @param codigoDisciplina Novo código de disciplina.
     * @param periodoLetivo Novo período acadêmico.
     * @param anoSemestre Novo identificador de semestre.
     * @return Turma atualizada.
     */
    ItemTurmaComTotal editar(
            Long id,
            Long professorResponsavelId,
            String codigoDisciplina,
            String periodoLetivo,
            String anoSemestre);

    /**
     * Exclui uma turma do sistema.
     *
     * @param id Identificador da turma.
     */
    void excluir(Long id);

    /**
     * Alterna o status de vigência da turma.
     *
     * @param id Identificador da turma.
     * @return Turma com status alternado.
     */
    ItemTurmaComTotal alternarStatus(Long id);

    /**
     * Lista todos os alunos matriculados na turma.
     *
     * @param turmaId Identificador da turma.
     * @return Lista de discentes matriculados.
     */
    List<Usuario> listarAlunosDaTurma(Long turmaId);

    /**
     * Matricula um aluno em uma turma.
     *
     * @param turmaId Identificador da turma.
     * @param alunoId Identificador do discente.
     */
    void matricularAluno(Long turmaId, Long alunoId);

    /**
     * Desmatricula um discente de uma turma.
     *
     * @param turmaId Identificador da turma.
     * @param alunoId Identificador do discente.
     */
    void desmatricularAluno(Long turmaId, Long alunoId);

    /**
     * Registro com os dados da turma e a contagem de discentes matriculados.
     *
     * @param turma Entidade de domínio da turma.
     * @param totalAlunos Quantidade de alunos vinculados.
     */
    record ItemTurmaComTotal(Turma turma, long totalAlunos) {}
}
