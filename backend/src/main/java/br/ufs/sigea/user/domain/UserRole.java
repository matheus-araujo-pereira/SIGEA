package br.ufs.sigea.user.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Níveis de perfil de acesso homologados no SIGEA.
 */
@Schema(description = "Perfis de acesso ao sistema")
public enum UserRole {

    /**
     * Administrador do sistema: acesso completo à gestão de usuários e configurações.
     */
    ADMIN,

    /**
     * Professor: criação de turmas, casos clínicos, atividades e correção de submissões.
     */
    PROFESSOR,

    /**
     * Estudante: visualização de casos clínicos, aplicação de gatilhos GTT e preenchimento de ferramentas de qualidade.
     */
    STUDENT
}
