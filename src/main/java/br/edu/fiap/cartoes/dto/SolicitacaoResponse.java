package br.edu.fiap.cartoes.dto;
import java.time.Instant;

import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.StatusCartao;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. Pedido do usuário autenticado com o relacionamento de tipo. */
@Schema(description = "Pedido do usuário autenticado com o relacionamento de tipo.")
public record SolicitacaoResponse(
     @Schema(description = "ID da solicitação", example = "1") Long id,
     @Schema(description = "Titular obtido da sessão", example = "1") Long usuarioId,
     @Schema(description = "Tipo relacionado", example = "1") Long tipoCartaoId,
     @Schema(description = "Nome obtido por relacionamento JPA", example = "ESSENCIAL") String tipoCartaoNome,
     @Schema(description = "Data de abertura em UTC", example = "2026-10-20T12:00:00Z") Instant solicitadaEm,
     @Schema(description = "PENDENTE, CANCELADA ou EMITIDA", example = "PENDENTE") StatusSolicitacao status
) {

    public static SolicitacaoResponse de(SolicitacaoCartao solicitacao) {
        return new SolicitacaoResponse(
                solicitacao.getId(),
                solicitacao.getUsuario().getId(),
                solicitacao.getTipoCartao().getId(),
                solicitacao.getTipoCartao().getNome(),
                solicitacao.getSolicitadaEm(),
                solicitacao.getStatus()
        );
    }

}
