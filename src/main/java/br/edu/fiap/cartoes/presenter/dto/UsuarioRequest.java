package br.edu.fiap.cartoes.presenter.dto;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. Cadastro público. Não registrar o DTO em logs. */
@Schema(description = "Cadastro público. Não registrar o DTO em logs.")
public record UsuarioRequest(
    @NotBlank @Size(max = 100) @Schema(description = "Nome completo", example = "Ana Lima") String nome,
    @NotBlank @Email @Size(max = 160) @Schema(description = "E-mail único, normalizado em minúsculas", example = "ana@example.com") String email,
    @NotBlank @Size(min = 8, max = 72) @Schema(description = "Senha de entrada; validar também máximo de 72 bytes UTF-8 antes do BCrypt", example = "Senha@123", writeOnly = true) String senha
) {}
