package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.Turma;
import br.ufs.dcomp.sigeagtt.domain.model.TurmaAluno;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.AtividadeEducacionalUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.AtividadeEducacionalRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CasoClinicoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoAtividadeRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaAlunoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.TurmaRepositoryPort;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link AtividadeEducacionalUseCase} no
 * SIGEA-GTT.
 *
 * <p>Orquestra o ciclo das atividades pedagógicas, montagem do painel docente e métricas de adesão
 * discente.
 */
@Service
public class AtividadeEducacionalService implements AtividadeEducacionalUseCase {

    private final AtividadeEducacionalRepositoryPort atividadeRepositorio;
    private final TurmaRepositoryPort turmaRepositorio;
    private final TurmaAlunoRepositoryPort turmaAlunoRepositorio;
    private final CasoClinicoRepositoryPort casoRepositorio;
    private final SubmissaoAtividadeRepositoryPort submissaoRepositorio;

    /**
     * Construtor com injeção das portas de persistência.
     *
     * @param atividadeRepositorio Porta de saída para atividades.
     * @param turmaRepositorio Porta de saída para turmas.
     * @param turmaAlunoRepositorio Porta de saída para vínculos turma-aluno.
     * @param casoRepositorio Porta de saída para casos clínicos.
     * @param submissaoRepositorio Porta de saída para submissões.
     */
    public AtividadeEducacionalService(
            AtividadeEducacionalRepositoryPort atividadeRepositorio,
            TurmaRepositoryPort turmaRepositorio,
            TurmaAlunoRepositoryPort turmaAlunoRepositorio,
            CasoClinicoRepositoryPort casoRepositorio,
            SubmissaoAtividadeRepositoryPort submissaoRepositorio) {
        this.atividadeRepositorio = atividadeRepositorio;
        this.turmaRepositorio = turmaRepositorio;
        this.turmaAlunoRepositorio = turmaAlunoRepositorio;
        this.casoRepositorio = casoRepositorio;
        this.submissaoRepositorio = submissaoRepositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<ItemAtividadeResumo> listar(Usuario usuarioLogado) {
        List<AtividadeEducacional> atividades;
        if (usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            atividades = atividadeRepositorio.listarTodas();
        } else {
            atividades = atividadeRepositorio.listarPorProfessorId(usuarioLogado.getId());
        }

        return atividades.stream()
                .map(
                        a -> {
                            int totalAlunos =
                                    (int)
                                            turmaAlunoRepositorio.contarAlunosPorTurmaId(
                                                    a.getTurma().getId());
                            List<SubmissaoAtividade> subs =
                                    submissaoRepositorio.listarPorAtividadeId(a.getId());
                            int totalSubmissoes =
                                    (int)
                                            subs.stream()
                                                    .filter(
                                                            s ->
                                                                    s.getStatus()
                                                                            != StatusSubmissao
                                                                                    .EM_ANDAMENTO)
                                                    .count();
                            int totalAvaliadas =
                                    (int)
                                            subs.stream()
                                                    .filter(
                                                            s ->
                                                                    s.getStatus()
                                                                            == StatusSubmissao
                                                                                    .AVALIADA)
                                                    .count();
                            return new ItemAtividadeResumo(
                                    a, totalAlunos, totalSubmissoes, totalAvaliadas);
                        })
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ItemAtividadeResumo buscarPorId(Long id) {
        AtividadeEducacional a =
                atividadeRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Atividade educacional não encontrada (ID: "
                                                        + id
                                                        + ")"));
        int totalAlunos = (int) turmaAlunoRepositorio.contarAlunosPorTurmaId(a.getTurma().getId());
        List<SubmissaoAtividade> subs = submissaoRepositorio.listarPorAtividadeId(a.getId());
        int totalSubmissoes =
                (int)
                        subs.stream()
                                .filter(s -> s.getStatus() != StatusSubmissao.EM_ANDAMENTO)
                                .count();
        int totalAvaliadas =
                (int) subs.stream().filter(s -> s.getStatus() == StatusSubmissao.AVALIADA).count();
        return new ItemAtividadeResumo(a, totalAlunos, totalSubmissoes, totalAvaliadas);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public DadosPainelAtividade buscarPainelAtividade(Long atividadeId, Usuario usuarioLogado) {
        AtividadeEducacional a =
                atividadeRepositorio
                        .buscarPorId(atividadeId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Atividade não encontrada"));

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && !a.getTurma().getProfessorResponsavel().getId().equals(usuarioLogado.getId())) {
            throw new AcessoProibidoException(
                    "Você não tem permissão para gerenciar esta atividade.");
        }

        List<TurmaAluno> matriculas = turmaAlunoRepositorio.listarPorTurmaId(a.getTurma().getId());
        List<SubmissaoAtividade> subs = submissaoRepositorio.listarPorAtividadeId(a.getId());
        Map<Long, SubmissaoAtividade> mapaSubs =
                subs.stream()
                        .collect(
                                Collectors.toMap(
                                        s -> s.getAluno().getId(), s -> s, (s1, s2) -> s1));

        List<ProgressoAluno> alunos = new ArrayList<>();
        int totalSubmissoes = 0;
        int totalPendentesCorrecao = 0;
        int totalAvaliadas = 0;
        BigDecimal somaNotas = BigDecimal.ZERO;

        for (TurmaAluno ta : matriculas) {
            Usuario aluno = ta.getAluno();
            SubmissaoAtividade sub = mapaSubs.get(aluno.getId());

            if (sub == null) {
                alunos.add(
                        new ProgressoAluno(
                                aluno.getId(),
                                aluno.getNomeCompleto(),
                                aluno.getEmail(),
                                aluno.getMatriculaSigaa(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null));
            } else {
                if (sub.getStatus() == StatusSubmissao.SUBMETIDA) {
                    totalSubmissoes++;
                    totalPendentesCorrecao++;
                } else if (sub.getStatus() == StatusSubmissao.AVALIADA) {
                    totalSubmissoes++;
                    totalAvaliadas++;
                    if (sub.getNota() != null) {
                        somaNotas = somaNotas.add(sub.getNota());
                    }
                }

                alunos.add(
                        new ProgressoAluno(
                                aluno.getId(),
                                aluno.getNomeCompleto(),
                                aluno.getEmail(),
                                aluno.getMatriculaSigaa(),
                                sub.getId(),
                                sub.getStatus(),
                                sub.getTempoGastoSegundos(),
                                sub.getDataSubmissao(),
                                sub.getNota(),
                                sub.getParecerDocente(),
                                sub.getDataAvaliacao()));
            }
        }

        BigDecimal mediaNotas =
                totalAvaliadas > 0
                        ? somaNotas.divide(
                                BigDecimal.valueOf(totalAvaliadas), 2, RoundingMode.HALF_UP)
                        : null;

        ItemAtividadeResumo atvResumo =
                new ItemAtividadeResumo(a, matriculas.size(), totalSubmissoes, totalAvaliadas);

        return new DadosPainelAtividade(
                atvResumo,
                a.getCasoClinico(),
                matriculas.size(),
                totalSubmissoes,
                totalPendentesCorrecao,
                totalAvaliadas,
                mediaNotas,
                alunos);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ItemAtividadeResumo salvar(Usuario professorLogado, DadosSalvarAtividade comando) {
        Turma turma =
                turmaRepositorio
                        .buscarPorId(comando.turmaId())
                        .orElseThrow(
                                () -> new RecursoNaoEncontradoException("Turma não encontrada"));

        if (professorLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && !turma.getProfessorResponsavel().getId().equals(professorLogado.getId())) {
            throw new RegraNegocioException("Você só pode criar atividades para as suas turmas.");
        }

        CasoClinico caso =
                casoRepositorio
                        .buscarPorId(comando.casoClinicoId())
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Caso clínico não encontrado"));

        if (professorLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && caso.getProfessorCriador() != null
                && !caso.getProfessorCriador().getId().equals(professorLogado.getId())) {
            throw new RegraNegocioException(
                    "Você só pode vincular casos clínicos criados por você.");
        }

        AtividadeEducacional a = new AtividadeEducacional();
        a.setTurma(turma);
        a.setCasoClinico(caso);
        a.setTitulo(comando.titulo());
        a.setOrientacoesPedagogicas(comando.orientacoesPedagogicas());
        a.setDataInicio(comando.dataInicio());
        a.setDataFim(comando.dataFim());
        a.setTempoLimiteMinutos(
                comando.tempoLimiteMinutos() != null ? comando.tempoLimiteMinutos() : 20);
        a.setAtiva(comando.ativa() != null ? comando.ativa() : true);
        a.validarInvariantes();

        AtividadeEducacional salva = atividadeRepositorio.salvar(a);
        int totalAlunos = (int) turmaAlunoRepositorio.contarAlunosPorTurmaId(turma.getId());
        return new ItemAtividadeResumo(salva, totalAlunos, 0, 0);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ItemAtividadeResumo atualizar(
            Long id, Usuario usuarioLogado, DadosSalvarAtividade comando) {
        AtividadeEducacional a =
                atividadeRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Atividade educacional não encontrada"));

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && !a.getTurma().getProfessorResponsavel().getId().equals(usuarioLogado.getId())) {
            throw new AcessoProibidoException("Você não tem permissão para editar esta atividade.");
        }

        Turma turma =
                turmaRepositorio
                        .buscarPorId(comando.turmaId())
                        .orElseThrow(
                                () -> new RecursoNaoEncontradoException("Turma não encontrada"));

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && !turma.getProfessorResponsavel().getId().equals(usuarioLogado.getId())) {
            throw new RegraNegocioException(
                    "Você só pode vincular a atividade às suas próprias turmas.");
        }

        CasoClinico caso =
                casoRepositorio
                        .buscarPorId(comando.casoClinicoId())
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Caso clínico não encontrado"));

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && caso.getProfessorCriador() != null
                && !caso.getProfessorCriador().getId().equals(usuarioLogado.getId())) {
            throw new RegraNegocioException(
                    "Você só pode vincular casos clínicos criados por você.");
        }

        a.setTurma(turma);
        a.setCasoClinico(caso);
        a.setTitulo(comando.titulo());
        a.setOrientacoesPedagogicas(comando.orientacoesPedagogicas());
        a.setDataInicio(comando.dataInicio());
        a.setDataFim(comando.dataFim());
        a.setTempoLimiteMinutos(
                comando.tempoLimiteMinutos() != null ? comando.tempoLimiteMinutos() : 20);
        if (comando.ativa() != null) {
            a.setAtiva(comando.ativa());
        }
        a.validarInvariantes();

        AtividadeEducacional salva = atividadeRepositorio.salvar(a);
        int totalAlunos = (int) turmaAlunoRepositorio.contarAlunosPorTurmaId(turma.getId());
        List<SubmissaoAtividade> subs = submissaoRepositorio.listarPorAtividadeId(a.getId());
        int totalSubmissoes =
                (int)
                        subs.stream()
                                .filter(s -> s.getStatus() != StatusSubmissao.EM_ANDAMENTO)
                                .count();
        int totalAvaliadas =
                (int) subs.stream().filter(s -> s.getStatus() == StatusSubmissao.AVALIADA).count();
        return new ItemAtividadeResumo(salva, totalAlunos, totalSubmissoes, totalAvaliadas);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void excluir(Long id, Usuario usuarioLogado) {
        AtividadeEducacional a =
                atividadeRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Atividade educacional não encontrada"));

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && !a.getTurma().getProfessorResponsavel().getId().equals(usuarioLogado.getId())) {
            throw new AcessoProibidoException(
                    "Você não tem permissão para excluir esta atividade.");
        }

        atividadeRepositorio.excluir(a.getId());
    }
}
