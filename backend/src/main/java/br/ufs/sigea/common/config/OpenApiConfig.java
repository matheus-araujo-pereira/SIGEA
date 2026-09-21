package br.ufs.sigea.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI 3.1 (Swagger) para a API REST do SIGEA-GTT.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Nome do esquema de segurança JWT para autenticação Bearer.
     */
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Define as informações da API e registra o esquema global de autenticação Bearer JWT.
     *
     * @return Objeto OpenAPI customizado
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SIGEA-GTT API - Sistema Inteligente de Gestão de Eventos Adversos")
                        .description("API REST para gestão de eventos adversos hospitalares segundo a metodologia Global Trigger Tool (IHI-GTT). " +
                                "Desenvolvido na Universidade Federal de Sergipe (UFS) - DCOMP / Enfermagem.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Matheus Araujo Pereira (Líder) | Profª Drª Ana Waleska de Menezes Seixas Souza | Prof Dr Gilton")
                                .email("contato.sigea@academico.ufs.br")
                                .url("https://www.ufs.br")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o token JWT retornado pelo endpoint /api/auth/login")));
    }
}
