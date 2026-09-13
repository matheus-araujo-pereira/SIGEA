package br.ufs.dcomp.sigeagtt.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração central da especificação OpenAPI 3.1 / Swagger UI do SIGEA-GTT.
 *
 * <p>Define os metadados institucionais hospitalares, informações de contato acadêmico/DCOMP-UFS e
 * o esquema global de autenticação Bearer JWT (RFC 6750) para consumo dos endpoints da API REST.
 */
@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_SEGURANCA_JWT = "bearerAuth";

    /**
     * Constrói a definição customizada do OpenAPI 3.1 para o SIGEA-GTT.
     *
     * @return Instância configurada de {@link OpenAPI} contendo Info, Security e Components.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("SIGEA-GTT API")
                                .version("1.0")
                                .description(
                                        "Sistema Inteligente de Gestão de Eventos Adversos – Global Trigger Tool (SIGEA-GTT).\n\n"
                                                + "Plataforma integrada de ensino, simulação e auditoria clínica hospitalar desenvolvida no "
                                                + "âmbito do Departamento de Computação (DCOMP) da Universidade Federal de Sergipe (UFS) "
                                                + "em parceria com o Hospital Universitário (HU-UFS/EBSERH).\n\n"
                                                + "Recursos fornecidos:\n"
                                                + "• Revisão retrospectiva baseada na metodologia Global Trigger Tool do Institute for Healthcare Improvement (IHI-GTT).\n"
                                                + "• Gestão de 45 gatilhos clínicos distribuídos nos 5 módulos IHI (Cuidados Gerais, Cirúrgico, Medicamentoso, UTI e Perinatal).\n"
                                                + "• Classificação de gravidade do dano pela taxonomia internacional NCC MERP (Categorias E a I).\n"
                                                + "• Ciclo de melhoria contínua da qualidade em saúde: Diagrama de Causa e Efeito (Ishikawa 6M), Plano de Ação 5W3H e Ciclo PDCA.\n"
                                                + "• Painel analítico de indicadores epidemiológicos hospitalares (taxas por 1.000 pacientes-dia, % internações com dano).")
                                .contact(
                                        new Contact()
                                                .name("Suporte SIGEA-GTT / DCOMP UFS")
                                                .email("sigeagtt@academico.ufs.br")
                                                .url("https://sigea-gtt-frontend.onrender.com"))
                                .license(
                                        new License()
                                                .name(
                                                        "UFS / EBSERH - Uso Institucional e Acadêmico")
                                                .url("https://www.ufs.br")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURANCA_JWT))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        ESQUEMA_SEGURANCA_JWT,
                                        new SecurityScheme()
                                                .name(ESQUEMA_SEGURANCA_JWT)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description(
                                                        "Autenticação via JSON Web Token (JWT). Insira o token retornado na autenticação (/api/autenticacao/entrar).")));
    }
}
