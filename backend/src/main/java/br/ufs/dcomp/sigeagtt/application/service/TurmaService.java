package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.TurmaAluno;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.TurmaUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaAlunoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UsuarioRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link TurmaUseCase} no SIGEA-GTT.
 *
 * <p>Orquestra o ciclo de turmas acadêmicas, validação de responsabilidade docente e matrícula de
 * discentes.
 */
@Service
public class TurmaService implements TurmaUseCase {

    private final TurmaRepositoryPort turmaRepositorio;
    private final TurmaAlunoRepositoryPort turmaAlunoRepositorio;
    private final UsuarioRepositoryPort usuarioRepositorio;

    /**
     * Construtor com injeção das portas de persistência.
     *
     * @param turmaRepositorio Porta de saída para turmas.
     * @param turmaAlunoRepositorio Porta de saída para vínculos de matrícula.
     * @param usuarioRepositorio Porta de saída para usuários.
     */
    public TurmaService(
            TurmaRepositoryPort turmaRepositorio,
            TurmaAlunoRepositoryPort turmaAlunoRepositorio,
            UsuarioRepositoryPort usuarioRepositorio) {
        this.turmaRepositorio = turmaRepositorio;
        this.turmaAlunoRepositorio = turmaAlunoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<ItemTurmaComTotal> listar(Long professorId) {
        List<Turma> turmas =
                (professorId != null)
                        ? turmaRepositorio.listarPorProfessorId(professorId)
                        : turmaRepositorio.listarTodas();

        return turmas.stream()
                .map(
                        t -> {
                            long total = turmaAlunoRepositorio.contarAlunosPorTurmaId(t.getId());
                            return new ItemTurmaComTotal(t, total);
                        })
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ItemTurmaComTotal buscarPorId(Long id) {
        Turma turma =
                turmaRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Turma não encontrada: " + id));
        long total = turmaAlunoRepositorio.contarAlunosPorTurmaId(turma.getId());
        return new ItemTurmaComTotal(turma, total);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ItemTurmaComTotal cadastrar(
            Long professorResponsavelId,
            String codigoDisciplina,
            String periodoLetivo,
            String anoSemestre) {
        Usuario professor =
                usuarioRepositorio
                        .buscarPorId(professorResponsavelId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Professor responsável não encontrado: "
                                                        + professorResponsavelId));

        if (professor.getPerfil() != PerfilUsuario.PROFESSOR
                && professor.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new RegraNegocioException(
                    "Apenas usuários com perfil docente podem ser responsáveis por turmas.");
        }

        String cod = codigoDisciplina != null ? codigoDisciplina.trim().toUpperCase() : "";
        String periodo = periodoLetivo != null ? periodoLetivo.trim() : "";
        String anoSem = anoSemestre != null ? anoSemestre.trim() : "";

        Turma turma = new Turma();
        turma.setCodigoDisciplina(cod);
        turma.setPeriodoLetivo(periodo);
        turma.setAnoSemestre(anoSem);
        turma.setProfessorResponsavel(professor);
        turma.setAtiva(true);
        turma.validarInvariantes();

        Turma salva = turmaRepositorio.salvar(turma);
        return new ItemTurmaComTotal(salva, 0);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ItemTurmaComTotal editar(
            Long id,
            Long professorResponsavelId,
            String codigoDisciplina,
            String periodoLetivo,
            String anoSemestre) {
        Turma turma =
                turmaRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Turma não encontrada: " + id));

        Usuario professor =
                usuarioRepositorio
                        .buscarPorId(professorResponsavelId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Professor responsável não encontrado: "
                                                        + professorResponsavelId));

        if (professor.getPerfil() != PerfilUsuario.PROFESSOR
                && professor.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new RegraNegocioException(
                    "Apenas usuários com perfil docente podem ser responsáveis por turmas.");
        }

        String cod = codigoDisciplina != null ? codigoDisciplina.trim().toUpperCase() : "";
        String periodo = periodoLetivo != null ? periodoLetivo.trim() : "";
        String anoSem = anoSemestre != null ? anoSemestre.trim() : "";

        turma.setCodigoDisciplina(cod);
        turma.setPeriodoLetivo(periodo);
        turma.setAnoSemestre(anoSem);
        turma.setProfessorResponsavel(professor);
        turma.validarInvariantes();

        Turma salva = turmaRepositorio.salvar(turma);
        long total = turmaAlunoRepositorio.contarAlunosPorTurmaId(salva.getId());
        return new ItemTurmaComTotal(salva, total);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void excluir(Long id) {
        Turma turma =
                turmaRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Turma não encontrada: " + id));
        turmaRepositorio.excluir(turma.getId());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ItemTurmaComTotal alternarStatus(Long id) {
        Turma turma =
                turmaRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Turma não encontrada: " + id));
        turma.setAtiva(!Boolean.TRUE.equals(turma.getAtiva()));
        Turma salva = turmaRepositorio.salvar(turma);
        long total = turmaAlunoRepositorio.contarAlunosPorTurmaId(salva.getId());
        return new ItemTurmaComTotal(salva, total);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarAlunosDaTurma(Long turmaId) {
        return turmaAlunoRepositorio.listarPorTurmaId(turmaId).stream()
                .map(ta -> ta.getAluno())
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void matricularAluno(Long turmaId, Long alunoId) {
        Turma turma =
                turmaRepositorio
                        .buscarPorId(turmaId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Turma não encontrada: " + turmaId));

        Usuario aluno =
                usuarioRepositorio
                        .buscarPorId(alunoId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Aluno não encontrado: " + alunoId));

        if (aluno.getPerfil() != PerfilUsuario.ALUNO) {
            throw new RegraNegocioException(
                    "Apenas usuários com perfil de ALUNO podem ser matriculados em turmas.");
        }

        if (turmaAlunoRepositorio.existeMatricula(turmaId, alunoId)) {
            throw new ConflitoDadosException(
                    "O aluno " + aluno.getNomeCompleto() + " já está matriculado nesta turma.");
        }

        TurmaAluno vinculo = new TurmaAluno(turma, aluno);
        vinculo.validarInvariantes();
        turmaAlunoRepositorio.salvar(vinculo);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void desmatricularAluno(Long turmaId, Long alunoId) {
        if (!turmaAlunoRepositorio.existeMatricula(turmaId, alunoId)) {
            throw new RegraNegocioException("O aluno informado não está vinculado a esta turma.");
        }
        turmaAlunoRepositorio.desmatricular(turmaId, alunoId);
    }
}
