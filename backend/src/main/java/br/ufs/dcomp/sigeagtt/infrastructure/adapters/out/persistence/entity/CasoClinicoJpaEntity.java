package br.ufs.dcomp.sigeagtt.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

/** Entidade JPA para mapeamento da tabela de casos clínicos simulados com prontuário integrado. */
@Entity
@Table(name = "casos_clinicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CasoClinicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_criador_id", nullable = false)
    private UsuarioJpaEntity professorCriador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_hospitalar_id", nullable = false)
    private UnidadeHospitalarJpaEntity unidadeHospitalar;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao_caso", nullable = false, columnDefinition = "TEXT")
    private String descricaoCaso;

    @Column(name = "objetivos_aprendizagem", nullable = false, columnDefinition = "TEXT")
    private String objetivosAprendizagem;

    @Column(name = "numero_atendimento", nullable = false, length = 50)
    private String numeroAtendimento;

    @Column(name = "idade_paciente", nullable = false)
    private Integer idadePaciente;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Column(name = "data_alta", nullable = false)
    private LocalDate dataAlta;

    @Column(name = "tempo_permanencia_dias", nullable = false)
    private Integer tempoPermanenciaDias;

    @Column(name = "sumario_alta", nullable = false, columnDefinition = "TEXT")
    private String sumarioAlta;

    @Column(name = "prescricoes_medicas", nullable = false, columnDefinition = "TEXT")
    private String prescricoesMedicas;

    @Column(name = "exames_laboratoriais", nullable = false, columnDefinition = "TEXT")
    private String examesLaboratoriais;

    @Column(name = "relatorio_cirurgico", columnDefinition = "TEXT")
    private String relatorioCirurgico;

    @Column(name = "evolucoes_multiprofissionais", nullable = false, columnDefinition = "TEXT")
    private String evolucoesMultiprofissionais;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void aoCriar() {
        if (this.criadoEm == null) this.criadoEm = LocalDateTime.now();
    }
}
