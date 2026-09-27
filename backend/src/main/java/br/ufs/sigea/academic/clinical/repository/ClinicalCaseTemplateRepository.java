package br.ufs.sigea.academic.clinical.repository;

import br.ufs.sigea.academic.clinical.domain.ClinicalCaseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório Spring Data JPA para a entidade ClinicalCaseTemplate.
 */
@Repository
public interface ClinicalCaseTemplateRepository extends JpaRepository<ClinicalCaseTemplate, UUID> {

    /**
     * Retorna todos os modelos ordenados pela data de criação decrescente.
     */
    List<ClinicalCaseTemplate> findAllByOrderByCreatedAtDesc();

    /**
     * Retorna modelos filtrados por código de módulo GTT.
     */
    List<ClinicalCaseTemplate> findByModuleCodeOrderByCreatedAtDesc(String moduleCode);

    /**
     * Retorna apenas os modelos canônicos fornecidos pelo sistema.
     */
    List<ClinicalCaseTemplate> findByIsSystemTemplateTrueOrderByCreatedAtDesc();
}
