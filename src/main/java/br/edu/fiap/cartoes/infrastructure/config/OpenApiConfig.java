package br.edu.fiap.cartoes.infrastructure.config;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;

/** Configuração pronta: a UI mostra Authorize e os contratos definidos nos controllers e DTOs. */
@OpenAPIDefinition(info = @Info(title = "Desafio Solicitação de Cartões", version = "1.0",
    description = "Casca didática. Cadastro e login públicos; sete operações de negócio protegidas. TODO retorna 501."))
@SecurityScheme(securitySchemeName = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig extends Application {}
