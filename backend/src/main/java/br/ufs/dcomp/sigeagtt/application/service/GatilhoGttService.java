package br.ufs.dcomp.sigeagtt.application.service;

import br.ufs.dcomp.sigeagtt.domain.model.ConflitoDadosException;
import br.ufs.dcomp.sigeagtt.domain.model.GatilhoGtt;
import br.ufs.dcomp.sigeagtt.domain.model.ModuloGtt;
import br.ufs.dcomp.sigeagtt.domain.model.RecursoNaoEncontradoException;
import br.ufs.dcomp.sigeagtt.domain.ports.input.GatilhoGttUseCase;
import br.ufs.dcomp.sigeagtt.domain.ports.output.GatilhoGttRepositoryPort;
import br.ufs.dcomp.sigeagtt.domain.ports.output.ModuloGttRepositoryPort;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de aplicação que implementa o caso de uso {@link GatilhoGttUseCase} no SIGEA-GTT.
 *
 * <p>Responsável pelo catálogo de 45 gatilhos clínicos da metodologia IHI-GTT e ordenação natural
 * alfanumérica por código (ex: C1, C2, ..., C10).
 */
@Service
public class GatilhoGttService implements GatilhoGttUseCase {

    private final GatilhoGttRepositoryPort gatilhoRepositorio;
    private final ModuloGttRepositoryPort moduloRepositorio;

    /**
     * Construtor com injeção das portas de persistência de gatilhos e módulos.
     *
     * @param gatilhoRepositorio Porta de saída de gatilhos.
     * @param moduloRepositorio Porta de saída de módulos.
     */
    public GatilhoGttService(
            GatilhoGttRepositoryPort gatilhoRepositorio,
            ModuloGttRepositoryPort moduloRepositorio) {
        this.gatilhoRepositorio = gatilhoRepositorio;
        this.moduloRepositorio = moduloRepositorio;
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<GatilhoGtt> listar(Long moduloId) {
        List<GatilhoGtt> lista;
        if (moduloId != null) {
            lista = gatilhoRepositorio.listarPorModuloId(moduloId);
        } else {
            lista = gatilhoRepositorio.listarTodos();
        }
        return ordenarNaturalmente(lista);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public GatilhoGtt buscarPorId(Long id) {
        return gatilhoRepositorio
                .buscarPorId(id)
                .orElseThrow(
                        () ->
                                new RecursoNaoEncontradoException(
                                        "Gatilho não encontrado com o ID: " + id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public GatilhoGtt cadastrar(
            Long moduloId, String codigo, String descricao, String limiarReferencia) {
        String codigoLimpo = codigo != null ? codigo.trim().toUpperCase() : "";
        String descLimpa = descricao != null ? descricao.trim() : "";
        String limiarLimpo = limiarReferencia != null ? limiarReferencia.trim() : null;

        if (gatilhoRepositorio.buscarPorCodigo(codigoLimpo).isPresent()) {
            throw new ConflitoDadosException(
                    "Já existe um gatilho cadastrado com o código: " + codigoLimpo);
        }

        ModuloGtt modulo =
                moduloRepositorio
                        .buscarPorId(moduloId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Módulo não encontrado com o ID: " + moduloId));

        GatilhoGtt gatilho = new GatilhoGtt();
        gatilho.setCodigo(codigoLimpo);
        gatilho.setModulo(modulo);
        gatilho.setDescricao(descLimpa);
        gatilho.setLimiarReferencia(limiarLimpo);
        gatilho.setAtivo(true);
        gatilho.validarInvariantes();

        return gatilhoRepositorio.salvar(gatilho);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public GatilhoGtt editar(
            Long id, Long moduloId, String codigo, String descricao, String limiarReferencia) {
        GatilhoGtt gatilho = buscarPorId(id);

        String codigoLimpo = codigo != null ? codigo.trim().toUpperCase() : "";
        String descLimpa = descricao != null ? descricao.trim() : "";
        String limiarLimpo = limiarReferencia != null ? limiarReferencia.trim() : null;

        if (gatilhoRepositorio.buscarPorCodigoEIdDiferente(codigoLimpo, id).isPresent()) {
            throw new ConflitoDadosException(
                    "O código '" + codigoLimpo + "' já está em uso por outro gatilho.");
        }

        ModuloGtt modulo =
                moduloRepositorio
                        .buscarPorId(moduloId)
                        .orElseThrow(
                                () ->
                                        new RecursoNaoEncontradoException(
                                                "Módulo não encontrado com o ID: " + moduloId));

        gatilho.setCodigo(codigoLimpo);
        gatilho.setModulo(modulo);
        gatilho.setDescricao(descLimpa);
        gatilho.setLimiarReferencia(limiarLimpo);
        gatilho.validarInvariantes();

        return gatilhoRepositorio.salvar(gatilho);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void excluir(Long id) {
        buscarPorId(id);
        gatilhoRepositorio.excluir(id);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public GatilhoGtt alternarStatus(Long id) {
        GatilhoGtt gatilho = buscarPorId(id);
        gatilho.setAtivo(!Boolean.TRUE.equals(gatilho.getAtivo()));
        return gatilhoRepositorio.salvar(gatilho);
    }

    private List<GatilhoGtt> ordenarNaturalmente(List<GatilhoGtt> lista) {
        if (lista == null || lista.isEmpty()) {
            return lista;
        }
        List<GatilhoGtt> mutavel = new ArrayList<>(lista);
        mutavel.sort(
                (g1, g2) -> {
                    String mod1 = extrairCodigoModulo(g1);
                    String mod2 = extrairCodigoModulo(g2);
                    int cmpMod = mod1.compareToIgnoreCase(mod2);
                    if (cmpMod != 0) {
                        return cmpMod;
                    }
                    return compararCodigosNaturalmente(g1.getCodigo(), g2.getCodigo());
                });
        return mutavel;
    }

    private String extrairCodigoModulo(GatilhoGtt g) {
        if (g.getModulo() != null && g.getModulo().getCodigo() != null) {
            return g.getModulo().getCodigo();
        }
        return "";
    }

    int compararCodigosNaturalmente(String cod1, String cod2) {
        if (cod1 == null) return cod2 == null ? 0 : -1;
        if (cod2 == null) return 1;

        String prefix1 = cod1.replaceAll("\\d", "");
        String prefix2 = cod2.replaceAll("\\d", "");
        int cmpPrefix = prefix1.compareToIgnoreCase(prefix2);
        if (cmpPrefix != 0) {
            return cmpPrefix;
        }

        String digits1 = cod1.replaceAll("\\D", "");
        String digits2 = cod2.replaceAll("\\D", "");
        if (!digits1.isEmpty() && !digits2.isEmpty()) {
            try {
                int n1 = Integer.parseInt(digits1);
                int n2 = Integer.parseInt(digits2);
                return Integer.compare(n1, n2);
            } catch (NumberFormatException ignored) {
            }
        }
        return cod1.compareToIgnoreCase(cod2);
    }
}
