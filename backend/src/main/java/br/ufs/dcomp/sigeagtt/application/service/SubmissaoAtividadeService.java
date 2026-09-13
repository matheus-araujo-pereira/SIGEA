package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.AtividadeEducacional;
import br.ufs.dcomp.sigeagtt.domain.model.CategoriaEventoAdverso;
import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.RegraNegocioException;
import br.ufs.dcomp.sigeagtt.domain.model.StatusSubmissao;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoAtividade;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoGatilho;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoIshikawa;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPdca;
import br.ufs.dcomp.sigeagtt.domain.model.SubmissaoPlano5w3h;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.SubmissaoAtividadeUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.AtividadeEducacionalRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CategoriaEventoAdversoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.GatilhoGttRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoAtividadeRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoGatilhoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoIshikawaRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoPdcaRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.SubmissaoPlano5w3hRepositoryPort;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link SubmissaoAtividadeUseCase} no SIGEA-GTT.
 *
 * <p>Orquestra o ciclo completo de resolução discente e homologação docente dos 4 componentes
 * pedagógicos: Gatilhos IHI-GTT, Diagrama de Ishikawa 6M, Planos de Ação 5W3H e Ciclo PDCA.
 */
@Service
public class SubmissaoAtividadeService implements SubmissaoAtividadeUseCase {

    private final SubmissaoAtividadeRepositoryPort submissaoRepositorio;
    private final AtividadeEducacionalRepositoryPort atividadeRepositorio;
    private final GatilhoGttRepositoryPort gatilhoRepositorio;
    private final CategoriaEventoAdversoRepositoryPort categoriaRepositorio;
    private final SubmissaoGatilhoRepositoryPort submissaoGatilhoRepositorio;
    private final SubmissaoIshikawaRepositoryPort submissaoIshikawaRepositorio;
    private final SubmissaoPlano5w3hRepositoryPort submissaoPlano5w3hRepositorio;
    private final SubmissaoPdcaRepositoryPort submissaoPdcaRepositorio;

    /**
     * Construtor com injeção de todas as portas de saída de persistência necessárias.
     *
     * @param submissaoRepositorio Porta de submissões.
     * @param atividadeRepositorio Porta de atividades.
     * @param gatilhoRepositorio Porta de gatilhos GTT.
     * @param categoriaRepositorio Porta de categorias de eventos adversos.
     * @param submissaoGatilhoRepositorio Porta de achados de gatilhos.
     * @param submissaoIshikawaRepositorio Porta de Ishikawa.
     * @param submissaoPlano5w3hRepositorio Porta de planos 5W3H.
     * @param submissaoPdcaRepositorio Porta de PDCA.
     */
    public SubmissaoAtividadeService(
            SubmissaoAtividadeRepositoryPort submissaoRepositorio,
            AtividadeEducacionalRepositoryPort atividadeRepositorio,
            GatilhoGttRepositoryPort gatilhoRepositorio,
            CategoriaEventoAdversoRepositoryPort categoriaRepositorio,
            SubmissaoGatilhoRepositoryPort submissaoGatilhoRepositorio,
            SubmissaoIshikawaRepositoryPort submissaoIshikawaRepositorio,
            SubmissaoPlano5w3hRepositoryPort submissaoPlano5w3hRepositorio,
            SubmissaoPdcaRepositoryPort submissaoPdcaRepositorio) {
        this.submissaoRepositorio = submissaoRepositorio;
        this.atividadeRepositorio = atividadeRepositorio;
        this.gatilhoRepositorio = gatilhoRepositorio;
        this.categoriaRepositorio = categoriaRepositorio;
        this.submissaoGatilhoRepositorio = submissaoGatilhoRepositorio;
        this.submissaoIshikawaRepositorio = submissaoIshikawaRepositorio;
        this.submissaoPlano5w3hRepositorio = submissaoPlano5w3hRepositorio;
        this.submissaoPdcaRepositorio = submissaoPdcaRepositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<ItemMinhaAtividade> listarMinhasAtividades(Usuario alunoLogado) {
        List<AtividadeEducacional> atividades =
                atividadeRepositorio.listarParaAluno(alunoLogado.getId());
        List<SubmissaoAtividade> minhasSubs =
                submissaoRepositorio.listarPorAlunoId(alunoLogado.getId());
        Map<Long, SubmissaoAtividade> mapaSubs =
                minhasSubs.stream()
                        .collect(
                                Collectors.toMap(
                                        s -> s.getAtividade().getId(), s -> s, (s1, s2) -> s1));

        return atividades.stream()
                .map(
                        a -> {
                            SubmissaoAtividade sub = mapaSubs.get(a.getId());
                            return new ItemMinhaAtividade(
                                    a.getId(),
                                    a.getTitulo(),
                                    a.getTurma().getId(),
                                    a.getTurma().getCodigoDisciplina(),
                                    a.getTurma().getCodigoDisciplina(),
                                    a.getTurma().getProfessorResponsavel().getNomeCompleto(),
                                    a.getCasoClinico().getId(),
                                    a.getCasoClinico().getTitulo(),
                                    a.getCasoClinico().getUnidadeHospitalar().getSigla(),
                                    a.getDataInicio(),
                                    a.getDataFim(),
                                    a.getTempoLimiteMinutos(),
                                    sub != null ? sub.getId() : null,
                                    sub != null ? sub.getStatus() : null,
                                    sub != null ? sub.getNota() : null,
                                    sub != null ? sub.getTempoGastoSegundos() : 0,
                                    sub != null ? sub.getDataSubmissao() : null,
                                    sub != null ? sub.getDataAvaliacao() : null);
                        })
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public SubmissaoAtividade iniciarOuContinuar(Long atividadeId, Usuario alunoLogado) {
        AtividadeEducacional atividade =
                atividadeRepositorio
                        .buscarPorId(atividadeId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Atividade não encontrada"));

        return submissaoRepositorio
                .buscarPorAtividadeEAluno(atividadeId, alunoLogado.getId())
                .orElseGet(
                        () -> {
                            SubmissaoAtividade nova =
                                    new SubmissaoAtividade(atividade, alunoLogado);
                            return submissaoRepositorio.salvar(nova);
                        });
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public SubmissaoAtividade buscarPorId(Long id, Usuario usuarioLogado) {
        SubmissaoAtividade submissao =
                submissaoRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Submissão não encontrada"));

        boolean isAlunoDono =
                usuarioLogado.getPerfil() == PerfilUsuario.ALUNO
                        && submissao.getAluno().getId().equals(usuarioLogado.getId());
        boolean isProfessorOuAdmin =
                usuarioLogado.getPerfil() == PerfilUsuario.PROFESSOR
                        || usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR;

        if (!isAlunoDono && !isProfessorOuAdmin) {
            throw new AcessoProibidoException(
                    "Você não tem permissão para visualizar esta submissão.");
        }

        submissao.setAchadosGatilhos(submissaoGatilhoRepositorio.listarPorSubmissaoId(id));
        submissao.setIshikawa(submissaoIshikawaRepositorio.buscarPorSubmissaoId(id).orElse(null));
        submissao.setPlanos5w3h(submissaoPlano5w3hRepositorio.listarPorSubmissaoId(id));
        submissao.setPdca(submissaoPdcaRepositorio.buscarPorSubmissaoId(id).orElse(null));

        return submissao;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public SubmissaoAtividade salvarOuSubmeter(
            Long id, Usuario alunoLogado, DadosSalvarSubmissao comando) {
        SubmissaoAtividade submissao =
                submissaoRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Submissão não encontrada"));

        if (!submissao.getAluno().getId().equals(alunoLogado.getId())) {
            throw new AcessoProibidoException(
                    "Você não tem permissão para modificar esta submissão.");
        }

        if (submissao.getStatus() != StatusSubmissao.EM_ANDAMENTO) {
            throw new RegraNegocioException(
                    "Esta atividade já foi finalizada e não permite novas alterações.");
        }

        if (comando.tempoGastoSegundos() != null) {
            submissao.setTempoGastoSegundos(comando.tempoGastoSegundos());
        }

        // 1. Atualizar Gatilhos
        submissaoGatilhoRepositorio.excluirPorSubmissaoId(id);
        List<SubmissaoGatilho> novosGatilhos = new ArrayList<>();
        if (comando.gatilhos() != null) {
            for (DadosGatilho gDto : comando.gatilhos()) {
                GatilhoGtt gatilho =
                        gatilhoRepositorio
                                .buscarPorId(gDto.gatilhoId())
                                .orElseThrow(
                                        () ->
                                                new RecursoNaoEncontradoException(
                                                        "Gatilho não encontrado: "
                                                                + gDto.gatilhoId()));

                CategoriaEventoAdverso categoria = null;
                if (gDto.categoriaEventoAdversoId() != null) {
                    categoria =
                            categoriaRepositorio
                                    .buscarPorId(gDto.categoriaEventoAdversoId())
                                    .orElse(null);
                }

                SubmissaoGatilho achado =
                        new SubmissaoGatilho(
                                null,
                                submissao,
                                gatilho,
                                categoria,
                                gDto.confirmouDano(),
                                gDto.justificativaDano(),
                                gDto.danoPresenteAdmissao(),
                                gDto.gravidade());
                novosGatilhos.add(submissaoGatilhoRepositorio.salvar(achado));
            }
        }
        submissao.setAchadosGatilhos(novosGatilhos);

        // 2. Atualizar Ishikawa
        submissaoIshikawaRepositorio.excluirPorSubmissaoId(id);
        if (comando.ishikawa() != null && comando.ishikawa().efeitoPrincipal() != null) {
            SubmissaoIshikawa ish =
                    new SubmissaoIshikawa(
                            null,
                            submissao,
                            comando.ishikawa().efeitoPrincipal(),
                            comando.ishikawa().metodo(),
                            comando.ishikawa().maoDeObra(),
                            comando.ishikawa().material(),
                            comando.ishikawa().medida(),
                            comando.ishikawa().meioAmbiente(),
                            comando.ishikawa().maquina());
            submissao.setIshikawa(submissaoIshikawaRepositorio.salvar(ish));
        }

        // 3. Atualizar 5W3H
        submissaoPlano5w3hRepositorio.excluirPorSubmissaoId(id);
        List<SubmissaoPlano5w3h> novosPlanos = new ArrayList<>();
        if (comando.planos5w3h() != null) {
            for (DadosPlano5w3h pDto : comando.planos5w3h()) {
                if (pDto.oQue() != null && !pDto.oQue().isBlank()) {
                    SubmissaoPlano5w3h plano =
                            new SubmissaoPlano5w3h(
                                    null,
                                    submissao,
                                    pDto.oQue(),
                                    pDto.porQue(),
                                    pDto.quem(),
                                    pDto.onde(),
                                    pDto.quando(),
                                    pDto.como(),
                                    pDto.quantoCusta(),
                                    pDto.comoMedir());
                    novosPlanos.add(submissaoPlano5w3hRepositorio.salvar(plano));
                }
            }
        }
        submissao.setPlanos5w3h(novosPlanos);

        // 4. Atualizar PDCA
        submissaoPdcaRepositorio.excluirPorSubmissaoId(id);
        if (comando.pdca() != null) {
            SubmissaoPdca pdca =
                    new SubmissaoPdca(
                            null,
                            submissao,
                            comando.pdca().planejar(),
                            comando.pdca().fazer(),
                            comando.pdca().checar(),
                            comando.pdca().agir());
            submissao.setPdca(submissaoPdcaRepositorio.salvar(pdca));
        }

        if (Boolean.TRUE.equals(comando.finalizar())) {
            submissao.submeter(comando.tempoGastoSegundos());
        }

        return submissaoRepositorio.salvar(submissao);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public SubmissaoAtividade avaliar(
            Long id, Usuario professorLogado, BigDecimal nota, String parecerDocente) {
        SubmissaoAtividade submissao =
                submissaoRepositorio
                        .buscarPorId(id)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Submissão não encontrada"));

        submissao.avaliar(professorLogado, nota, parecerDocente);
        return submissaoRepositorio.salvar(submissao);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<SubmissaoAtividade> listarPendentesCorrecao(Usuario professorLogado) {
        List<SubmissaoAtividade> todas = submissaoRepositorio.listarTodas();
        return todas.stream()
                .filter(s -> s.getStatus() == StatusSubmissao.SUBMETIDA)
                .filter(
                        s -> {
                            if (professorLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
                                return true;
                            }
                            return s.getAtividade() != null
                                    && s.getAtividade().getTurma() != null
                                    && s.getAtividade().getTurma().getProfessorResponsavel() != null
                                    && s.getAtividade()
                                            .getTurma()
                                            .getProfessorResponsavel()
                                            .getId()
                                            .equals(professorLogado.getId());
                        })
                .peek(
                        s -> {
                            s.setAchadosGatilhos(
                                    submissaoGatilhoRepositorio.listarPorSubmissaoId(s.getId()));
                            s.setIshikawa(
                                    submissaoIshikawaRepositorio
                                            .buscarPorSubmissaoId(s.getId())
                                            .orElse(null));
                            s.setPlanos5w3h(
                                    submissaoPlano5w3hRepositorio.listarPorSubmissaoId(s.getId()));
                            s.setPdca(
                                    submissaoPdcaRepositorio
                                            .buscarPorSubmissaoId(s.getId())
                                            .orElse(null));
                        })
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaEventoAdverso> listarCategoriasAtivas() {
        return categoriaRepositorio.listarTodas().stream()
                .filter(c -> Boolean.TRUE.equals(c.getAtiva()))
                .toList();
    }
}
