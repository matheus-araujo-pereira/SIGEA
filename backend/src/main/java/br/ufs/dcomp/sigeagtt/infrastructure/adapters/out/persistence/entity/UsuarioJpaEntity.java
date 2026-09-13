package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import br.ufs.dcomp.sigeagtt.domain.model.PerfilUsuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Entidade JPA para mapeamento da tabela de usuários no PostgreSQL. */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "primeiro_acesso", nullable = false)
    @Builder.Default
    private Boolean primeiroAcesso = true;

    @Column(name = "matricula_sigaa", unique = true, length = 12)
    private String matriculaSigaa;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "perfil", nullable = false, columnDefinition = "perfil_usuario_enum")
    private PerfilUsuario perfil;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void aoCriar() {
        if (this.criadoEm == null) this.criadoEm = LocalDateTime.now();
        if (this.ativo == null) this.ativo = true;
        if (this.primeiroAcesso == null) this.primeiroAcesso = true;
    }
}
