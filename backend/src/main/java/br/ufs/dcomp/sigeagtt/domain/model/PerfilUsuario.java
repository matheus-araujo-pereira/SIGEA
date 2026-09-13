package br.ufs.dcomp.sigeagtt.domain.model;

/**
 * Perfis de acesso de usuários no ecossistema acadêmico-hospitalar do SIGEA-GTT.
 *
 * <p>Define as permissões funcionais e papéis de segurança no sistema:
 *
 * <ul>
 *   <li>{@link #ADMINISTRADOR}: Gestor do sistema, responsável pelo gerenciamento de usuários,
 *       turmas, unidades hospitalares, gatilhos IHI-GTT e análise de auditoria global.
 *   <li>{@link #PROFESSOR}: Docente responsável pela criação de turmas, casos clínicos, atividades
 *       de auditoria e avaliação formativa das submissões dos discentes.
 *   <li>{@link #ALUNO}: Discente que executa a revisão retrospectiva dos prontuários simulados,
 *       detectando gatilhos, avaliando danos e aplicando ferramentas da qualidade (Ishikawa, 5W3H,
 *       PDCA).
 * </ul>
 */
public enum PerfilUsuario {
    /** Perfil de gestão administrativa com privilégios completos. */
    ADMINISTRADOR,

    /** Perfil docente com permissão de criação e validação de atividades educacionais. */
    PROFESSOR,

    /** Perfil discente para treinamento e auditoria retrospectiva de prontuários. */
    ALUNO
}
