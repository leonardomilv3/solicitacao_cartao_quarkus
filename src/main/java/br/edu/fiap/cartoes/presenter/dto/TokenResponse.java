package br.edu.fiap.cartoes.presenter.dto;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import br.edu.fiap.cartoes.domain.model.Token;

/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. Resultado do login. Não existe refresh token neste desafio. */
@Schema(description = "Resultado do login. Não existe refresh token neste desafio.")
public record TokenResponse(
     @Schema(description = "JWT assinado HS256, nunca contendo senha", example = "eyJ...") String accessToken,
     @Schema(description = "Tipo do cabeçalho Authorization", example = "Bearer") String tokenType,
     @Schema(description = "Duração em segundos", example = "900") long expiresIn,
     @Schema(description = "Expiração em epoch seconds", example = "1792498500") long expiresAt
) {

    public static TokenResponse de(Token token) {
        return new TokenResponse(token.valor(), token.tipo(), token.duracaoSegundos(), token.expiraEm());
    }
}
