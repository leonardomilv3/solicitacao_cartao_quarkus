package br.edu.fiap.cartoes.dto;
import java.time.Instant;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.StatusCartao;
import br.edu.fiap.cartoes.entity.Usuario;
import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
/** Camada DTO: contrato HTTP pronto; não é entidade nem modelo de tabela. Dados públicos do cadastro, sem senha ou hash. */
@Schema(description = "Dados públicos do cadastro, sem senha ou hash.")
public record UsuarioResponse(
     @Schema(description = "Identificador do usuário", example = "1") Long id,
     @Schema(description = "Nome", example = "Ana Lima") String nome,
     @Schema(description = "E-mail", example = "ana@example.com") String email,
     @Schema(description = "Instante UTC de criação", example = "2026-10-20T12:00:00Z") Instant criadoEm
) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCriadoEm()
        );
    }
}
