package br.edu.fiap.cartoes.presenter.dto;
import java.time.Instant;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. Formato estável de erro. Sem stack trace, SQL, senha ou token. */
@Schema(description = "Formato estável de erro. Sem stack trace, SQL, senha ou token.")
public record ErroResponse(
     @Schema(description = "Instante do erro", example = "2026-10-20T12:00:00Z") Instant timestamp,
     @Schema(description = "Status HTTP", example = "409") int status,
     @Schema(description = "Código estável", example = "REGRA_NEGOCIO") String codigo,
     @Schema(description = "Explicação segura", example = "Solicitação não está pendente") String mensagem,
     @Schema(description = "Rota chamada", example = "/api/solicitacoes/1/cancelar") String path
) {}
