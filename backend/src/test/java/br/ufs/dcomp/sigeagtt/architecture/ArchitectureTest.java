package br.ufs.dcomp.sigeagtt.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

/** Testes arquiteturais automatizados com ArchUnit validando o isolamento da Clean Architecture. */
@AnalyzeClasses(
        packages = {"br.ufs.dcomp.sigeagtt"},
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {

    /** O pacote 'domain' não deve importar 'org.springframework.*' nem 'jakarta.persistence.*'. */
    @ArchTest
    static final ArchRule domain_nao_deve_depender_de_spring_ou_jpa =
            noClasses()
                    .that()
                    .resideInAPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "jakarta.persistence..")
                    .allowEmptyShould(true);

    /**
     * Controladores REST residem estritamente na camada de apresentação / web
     * (infrastructure.adapters.in.web).
     */
    @ArchTest
    static final ArchRule controladores_devem_residir_na_camada_web =
            classes()
                    .that()
                    .areAnnotatedWith(RestController.class)
                    .should()
                    .resideInAnyPackage("..infrastructure.adapters.in.web..")
                    .allowEmptyShould(true);

    /**
     * Repositórios Spring Data JPA e adaptadores de persistência residem estritamente na
     * infraestrutura.
     */
    @ArchTest
    static final ArchRule repositorios_devem_residir_na_infraestrutura =
            classes()
                    .that()
                    .areAssignableTo(JpaRepository.class)
                    .should()
                    .resideInAnyPackage("..infrastructure.adapters.out.persistence..")
                    .allowEmptyShould(true);
}
