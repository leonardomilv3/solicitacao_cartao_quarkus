package br.edu.fiap.cartoes.presenter.resource;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.*;
import org.eclipse.microprofile.openapi.annotations.media.*;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import br.edu.fiap.cartoes.application.usecase.CartaoService;
import br.edu.fiap.cartoes.presenter.dto.CartaoResponse;
import br.edu.fiap.cartoes.presenter.dto.ErroResponse;

/**
 * Camada controller pronta: traduz HTTP, valida o contrato e delega à aplicação.
 * Não escrever consultas ou decisões de estado aqui. Na migração Clean, esta classe
 * será um adaptador de entrada; preservar as rotas e seus contratos.
 */
@Path("/api/cartoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Cartões")
@Authenticated
@SecurityRequirement(name="bearerAuth")
public class CartaoResource {
    @Inject CartaoService cartoes;
    @Inject UsuarioAtual usuarioAtual;

    /** Protegida. JPQL personalizada ligando cartão, solicitação, usuário e tipo. Filtrar pelo titular do JWT e retornar dados pelo DTO, sem senha ou entidades aninhadas. */
    @GET
    @Operation(summary="Listar meus cartões", description="Protegida. JPQL personalizada ligando cartão, solicitação, usuário e tipo. Filtrar pelo titular do JWT e retornar dados pelo DTO, sem senha ou entidades aninhadas.")
    @APIResponses({
        @APIResponse(responseCode="200",description="Operação concluída",content=@Content(schema=@Schema(type=SchemaType.ARRAY, implementation=CartaoResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="500",description="Falha interna",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public List<CartaoResponse> listar() {
        return cartoes.listar(usuarioAtual.id()).stream().map(CartaoResponse::de).toList();
    }
}
