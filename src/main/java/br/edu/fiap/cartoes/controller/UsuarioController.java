package br.edu.fiap.cartoes.controller;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.*;
import org.eclipse.microprofile.openapi.annotations.media.*;
import br.edu.fiap.cartoes.dto.*;
import br.edu.fiap.cartoes.usecase.*;

/**
 * Camada controller pronta: traduz HTTP, valida o contrato e delega à aplicação.
 * Não escrever consultas ou decisões de estado aqui. Na migração Clean, esta classe
 * será um adaptador de entrada; preservar as rotas e seus contratos.
 */
@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Cadastro")
@PermitAll
public class UsuarioController {

    private final UsuarioUseCase usuarios;

    @Inject
    public UsuarioController(UsuarioUseCase usuarios) {
        this.usuarios = usuarios;
    }

    /** Pública. Validar entrada, normalizar e-mail, verificar unicidade, gerar BCrypt e persistir por JPA.
     * Retornar somente dados públicos. */
    @POST
    @Operation(summary="Cadastrar usuário", description="Pública. Validar entrada, normalizar e-mail, verificar " +
            "unicidade, gerar BCrypt e persistir por JPA. Retornar somente dados públicos.")
    @APIResponses({
        @APIResponse(responseCode="201",description="Operação concluída",content=@Content(
                schema=@Schema(implementation=UsuarioResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(
                schema=@Schema(implementation=ErroResponse.class))),
        @APIResponse(responseCode="409",description="Duplicidade ou estado incompatível", content = @Content(
                schema = @Schema(implementation = ErroResponse.class))),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(
                schema=@Schema(implementation=ErroResponse.class)))
    })
    public Response cadastrar(@Valid @NotNull UsuarioRequest request) {
        return Response.status(Response.Status.CREATED).entity(usuarios.cadastrar(request)).build();
    }
}
