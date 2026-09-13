package br.ufs.dcomp.sigeagtt.domain.model;

/**
 * Ciclo de vida e estados de uma submissão de atividade educacional no SIGEA-GTT.
 *
 * <p>Controla o fluxo pedagógico desde a resolução inicial pelo discente até a correção formativa e
 * homologação pelo docente responsável.
 */
public enum StatusSubmissao {
    /** Rascunho da resolução iniciado pelo discente, em fase de edição e auditoria. */
    EM_ANDAMENTO("Em andamento"),

    /** Resolução finalizada e entregue pelo discente, aguardando avaliação do docente. */
    SUBMETIDA("Submetida"),

    /** Resolução corrigida pelo docente, com atribuição de nota e parecer formativo. */
    AVALIADA("Avaliada");

    private final String descricao;

    StatusSubmissao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna o nome amigável do status da submissão.
     *
     * @return Descrição do estado.
     */
    public String getDescricao() {
        return descricao;
    }
}
