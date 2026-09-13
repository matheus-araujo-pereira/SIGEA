package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.model.UnidadeHospitalar;
import br.ufs.dcomp.sigeagtt.domain.ports.input.UnidadeHospitalarUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.UnidadeHospitalarRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link UnidadeHospitalarUseCase} no SIGEA-GTT.
 *
 * <p>Gerencia as enfermarias, clínicas e UTIs do Hospital Universitário (HU-UFS/EBSERH) disponíveis
 * para alocação de prontuários simulados.
 */
@Service
public class UnidadeHospitalarService implements UnidadeHospitalarUseCase {

    private final UnidadeHospitalarRepositoryPort repositorio;

    /**
     * Construtor com injeção da porta de saída do repositório.
     *
     * @param repositorio Porta de persistência de unidades hospitalares.
     */
    public UnidadeHospitalarService(UnidadeHospitalarRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<UnidadeHospitalar> listarTodas() {
        return repositorio.listarTodas();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UnidadeHospitalar buscarPorId(Long id) {
        return repositorio
                .buscarPorId(id)
                .orElseThrow(
                        () ->
                                new RecursoNaoEncontradoException(
                                        "Unidade hospitalar não encontrada: " + id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UnidadeHospitalar cadastrar(String sigla, String nome) {
        String siglaLimpa = sigla != null ? sigla.trim().toUpperCase() : "";
        String nomeLimpo = nome != null ? nome.trim() : "";

        if (repositorio.buscarPorSigla(siglaLimpa).isPresent()) {
            throw new ConflitoDadosException(
                    "Já existe uma unidade cadastrada com a sigla: " + siglaLimpa);
        }

        UnidadeHospitalar unidade = new UnidadeHospitalar();
        unidade.setSigla(siglaLimpa);
        unidade.setNome(nomeLimpo);
        unidade.setAtiva(true);
        unidade.validarInvariantes();

        return repositorio.salvar(unidade);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UnidadeHospitalar editar(Long id, String sigla, String nome) {
        UnidadeHospitalar unidade = buscarPorId(id);

        String siglaLimpa = sigla != null ? sigla.trim().toUpperCase() : "";
        String nomeLimpo = nome != null ? nome.trim() : "";

        if (repositorio.buscarPorSiglaEIdDiferente(siglaLimpa, id).isPresent()) {
            throw new ConflitoDadosException(
                    "A sigla '" + siglaLimpa + "' já está em uso por outra unidade.");
        }

        unidade.setSigla(siglaLimpa);
        unidade.setNome(nomeLimpo);
        unidade.validarInvariantes();

        return repositorio.salvar(unidade);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void excluir(Long id) {
        buscarPorId(id);
        repositorio.excluir(id);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UnidadeHospitalar alternarStatus(Long id) {
        UnidadeHospitalar unidade = buscarPorId(id);
        unidade.setAtiva(!Boolean.TRUE.equals(unidade.getAtiva()));
        return repositorio.salvar(unidade);
    }
}
