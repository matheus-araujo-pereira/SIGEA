package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.ports.input.ModuloGttUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.ModuloGttRepositoryPort;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link ModuloGttUseCase} no SIGEA-GTT.
 *
 * <p>Gerencia os módulos estruturais da metodologia IHI Global Trigger Tool (Cuidados, Cirúrgico,
 * Medicamentoso, UTI e Perinatal).
 */
@Service
public class ModuloGttService implements ModuloGttUseCase {

    private final ModuloGttRepositoryPort repositorio;

    /**
     * Construtor com injeção da porta de persistência de módulos GTT.
     *
     * @param repositorio Porta de saída para módulos GTT.
     */
    public ModuloGttService(ModuloGttRepositoryPort repositorio) {
        this.repositorio = repositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<ModuloGtt> listarTodos() {
        return repositorio.listarTodos();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ModuloGtt buscarPorId(Long id) {
        return repositorio
                .buscarPorId(id)
                .orElseThrow(
                        () ->
                                new RecursoNaoEncontradoException(
                                        "Módulo GTT não encontrado: " + id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ModuloGtt cadastrar(String codigo, String nome, String descricao) {
        String codigoLimpo = codigo != null ? codigo.trim().toUpperCase() : "";
        String nomeLimpo = nome != null ? nome.trim() : "";
        String descLimpa = descricao != null ? descricao.trim() : null;

        if (repositorio.buscarPorCodigo(codigoLimpo).isPresent()) {
            throw new ConflitoDadosException(
                    "Já existe um módulo registrado com o código: " + codigoLimpo);
        }

        ModuloGtt modulo = new ModuloGtt();
        modulo.setCodigo(codigoLimpo);
        modulo.setNome(nomeLimpo);
        modulo.setDescricao(descLimpa);
        modulo.setAtivo(true);
        modulo.validarInvariantes();

        return repositorio.salvar(modulo);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ModuloGtt editar(Long id, String codigo, String nome, String descricao) {
        ModuloGtt modulo = buscarPorId(id);

        String codigoLimpo = codigo != null ? codigo.trim().toUpperCase() : "";
        String nomeLimpo = nome != null ? nome.trim() : "";
        String descLimpa = descricao != null ? descricao.trim() : null;

        if (repositorio.buscarPorCodigoEIdDiferente(codigoLimpo, id).isPresent()) {
            throw new ConflitoDadosException(
                    "O código '" + codigoLimpo + "' já pertence a outro módulo.");
        }

        modulo.setCodigo(codigoLimpo);
        modulo.setNome(nomeLimpo);
        modulo.setDescricao(descLimpa);
        modulo.validarInvariantes();

        return repositorio.salvar(modulo);
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
    public ModuloGtt alternarStatus(Long id) {
        ModuloGtt modulo = buscarPorId(id);
        modulo.setAtivo(!Boolean.TRUE.equals(modulo.getAtivo()));
        return repositorio.salvar(modulo);
    }
}
