package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.AcessoProibidoException;
import br.ufs.dcomp.sigeagtt.domain.model.CasoClinico;
import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.model.Usuario;
import br.ufs.dcomp.sigeagtt.domain.ports.input.CasoClinicoUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.CasoClinicoRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UnidadeHospitalarRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link CasoClinicoUseCase} no SIGEA-GTT.
 *
 * <p>Gerencia prontuários simulados hospitalares, controle de autoria docente e vínculo com as
 * unidades hospitalares.
 */
@Service
public class CasoClinicoService implements CasoClinicoUseCase {

    private final CasoClinicoRepositoryPort casoRepositorio;
    private final UnidadeHospitalarRepositoryPort unidadeRepositorio;

    /**
     * Construtor com injeção das portas de persistência.
     *
     * @param casoRepositorio Porta de saída para casos clínicos.
     * @param unidadeRepositorio Porta de saída para unidades hospitalares.
     */
    public CasoClinicoService(
            CasoClinicoRepositoryPort casoRepositorio,
            UnidadeHospitalarRepositoryPort unidadeRepositorio) {
        this.casoRepositorio = casoRepositorio;
        this.unidadeRepositorio = unidadeRepositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<CasoClinico> listar(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR) {
            return casoRepositorio.listarTodos();
        }
        return casoRepositorio.listarPorProfessorCriadorId(usuarioLogado.getId());
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public CasoClinico buscarPorId(Long id) {
        return casoRepositorio
                .buscarPorId(id)
                .orElseThrow(
                        () ->
                                new RecursoNaoEncontradoException(
                                        "Caso clínico não encontrado (ID: " + id + ")"));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CasoClinico salvar(Usuario professorLogado, DadosSalvarCasoClinico comando) {
        UnidadeHospitalar unidade =
                unidadeRepositorio
                        .buscarPorId(comando.unidadeHospitalarId())
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Unidade hospitalar não encontrada"));

        CasoClinico c = new CasoClinico();
        c.setProfessorCriador(professorLogado);
        c.setUnidadeHospitalar(unidade);
        aplicarDados(c, comando);
        c.validarInvariantes();

        return casoRepositorio.salvar(c);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CasoClinico atualizar(Long id, Usuario usuarioLogado, DadosSalvarCasoClinico comando) {
        CasoClinico c = buscarPorId(id);

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && !c.getProfessorCriador().getId().equals(usuarioLogado.getId())) {
            throw new AcessoProibidoException(
                    "Você não tem permissão para editar este caso clínico.");
        }

        UnidadeHospitalar unidade =
                unidadeRepositorio
                        .buscarPorId(comando.unidadeHospitalarId())
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Unidade hospitalar não encontrada"));

        c.setUnidadeHospitalar(unidade);
        aplicarDados(c, comando);
        c.validarInvariantes();

        return casoRepositorio.salvar(c);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void excluir(Long id, Usuario usuarioLogado) {
        CasoClinico c = buscarPorId(id);

        if (usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR
                && !c.getProfessorCriador().getId().equals(usuarioLogado.getId())) {
            throw new AcessoProibidoException(
                    "Você não tem permissão para excluir este caso clínico.");
        }

        casoRepositorio.excluir(c.getId());
    }

    private void aplicarDados(CasoClinico c, DadosSalvarCasoClinico comando) {
        c.setTitulo(comando.titulo());
        c.setDescricaoCaso(comando.descricaoCaso());
        c.setObjetivosAprendizagem(comando.objetivosAprendizagem());
        c.setNumeroAtendimento(comando.numeroAtendimento());
        c.setIdadePaciente(comando.idadePaciente());
        c.setDataAdmissao(comando.dataAdmissao());
        c.setDataAlta(comando.dataAlta());
        c.setTempoPermanenciaDias(comando.tempoPermanenciaDias());
        c.setSumarioAlta(comando.sumarioAlta());
        c.setPrescricoesMedicas(comando.prescricoesMedicas());
        c.setExamesLaboratoriais(comando.examesLaboratoriais());
        c.setRelatorioCirurgico(comando.relatorioCirurgico());
        c.setEvolucoesMultiprofissionais(comando.evolucoesMultiprofissionais());
    }
}
