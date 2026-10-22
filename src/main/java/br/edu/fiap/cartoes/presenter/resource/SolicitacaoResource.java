package br.edu.fiap.cartoes.presenter.resource;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.responses.*;
import org.eclipse.microprofile.openapi.annotations.media.*;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import br.edu.fiap.cartoes.application.usecase.SolicitacaoService;
import br.edu.fiap.cartoes.presenter.dto.CartaoResponse;
import br.edu.fiap.cartoes.presenter.dto.SolicitacaoRequest;
import br.edu.fiap.cartoes.presenter.dto.SolicitacaoResponse;
import br.edu.fiap.cartoes.presenter.dto.ErroResponse;

/**
 * Camada controller pronta: traduz HTTP, valida o contrato e delega à aplicação.
 * Não escrever consultas ou decisões de estado aqui. Na migração Clean, esta classe
 * será um adaptador de entrada; preservar as rotas e seus contratos.
 */
@Path("/api/solicitacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="Solicitações")
@Authenticated
@SecurityRequirement(name="bearerAuth")
public class SolicitacaoResource {
    @Inject SolicitacaoService solicitacoes;
    @Inject UsuarioAtual usuarioAtual;

    /** Protegida. JPA. Titular vem do JWT. Tipo precisa existir e estar disponível; negar pedido PENDENTE duplicado do mesmo titular e tipo. Estado inicial PENDENTE. */
    @POST
    @Operation(summary="Solicitar um cartão", description="Protegida. JPA. Titular vem do JWT. Tipo precisa existir e estar disponível; negar pedido PENDENTE duplicado do mesmo titular e tipo. Estado inicial PENDENTE.")
    @APIResponses({
        @APIResponse(responseCode="201",description="Operação concluída",content=@Content(schema=@Schema(implementation=SolicitacaoResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(schema=@Schema(implementation=ErroResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="404",description="Recurso inexistente ou não pertencente ao titular"),
        @APIResponse(responseCode="409",description="Duplicidade ou estado incompatível"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public Response criar(@Valid @NotNull SolicitacaoRequest request) {
        return Response.status(Response.Status.CREATED).entity(SolicitacaoResponse.de(solicitacoes.criar(usuarioAtual.id(), request.tipoCartaoId()))).build();
    }

    /** Protegida. Consulta JPQL personalizada, com JOIN FETCH para o tipo, filtro obrigatório por usuarioId e filtro opcional por status. Nenhum pedido de outro titular pode aparecer. */
    @GET
    @Operation(summary="Listar minhas solicitações", description="Protegida. Consulta JPQL personalizada, com JOIN FETCH para o tipo, filtro obrigatório por usuarioId e filtro opcional por status. Nenhum pedido de outro titular pode aparecer.")
    @APIResponses({
        @APIResponse(responseCode="200",description="Operação concluída",content=@Content(schema=@Schema(type=SchemaType.ARRAY, implementation=SolicitacaoResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(schema=@Schema(implementation=ErroResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public List<SolicitacaoResponse> listar(@QueryParam("status") @Parameter(description="Filtro opcional PENDENTE, CANCELADA ou EMITIDA") StatusSolicitacao status) {
        return solicitacoes.listar(usuarioAtual.id(), status).stream().map(SolicitacaoResponse::de).toList();
    }

    /** Protegida. JPA. Buscar por ID e titular conjuntamente; retornar 404 tanto para ID ausente quanto para pedido de outro usuário. */
    @GET
    @Path("/{id}")
    @Operation(summary="Consultar minha solicitação", description="Protegida. JPA. Buscar por ID e titular conjuntamente; retornar 404 tanto para ID ausente quanto para pedido de outro usuário.")
    @APIResponses({
        @APIResponse(responseCode="200",description="Operação concluída",content=@Content(schema=@Schema(implementation=SolicitacaoResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(schema=@Schema(implementation=ErroResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="404",description="Recurso inexistente ou não pertencente ao titular"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public SolicitacaoResponse buscar(@PathParam("id") @Positive @Parameter(description="ID da própria solicitação", example="1") Long id) {
        return SolicitacaoResponse.de(solicitacoes.buscar(usuarioAtual.id(), id));
    }

    /** Protegida. JPA, transação e bloqueio da mesma solicitação para não disputar com emissão. Somente PENDENTE vira CANCELADA; EMITIDA ou CANCELADA gera 409. */
    @PATCH
    @Path("/{id}/cancelar")
    @Operation(summary="Cancelar minha solicitação pendente", description="Protegida. JPA, transação e bloqueio da mesma solicitação para não disputar com emissão. Somente PENDENTE vira CANCELADA; EMITIDA ou CANCELADA gera 409.")
    @APIResponses({
        @APIResponse(responseCode="200",description="Operação concluída",content=@Content(schema=@Schema(implementation=SolicitacaoResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(schema=@Schema(implementation=ErroResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="404",description="Recurso inexistente ou não pertencente ao titular"),
        @APIResponse(responseCode="409",description="Duplicidade ou estado incompatível"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public SolicitacaoResponse cancelar(@PathParam("id") @Positive @Parameter(description="ID da solicitação pendente", example="1") Long id) {
        return SolicitacaoResponse.de(solicitacoes.cancelar(usuarioAtual.id(), id));
    }

    /** Protegida. Simulação didática, não aprovação financeira real. JPA. Bloquear pedido, validar titular e PENDENTE, persistir um cartão ATIVO e atualizar pedido para EMITIDA na mesma transação. Repetição gera 409, nunca outro cartão. */
    @POST
    @Path("/{id}/emitir")
    @Operation(summary="Simular emissão do meu cartão", description="Protegida. Simulação didática, não aprovação financeira real. JPA. Bloquear pedido, validar titular e PENDENTE, persistir um cartão ATIVO e atualizar pedido para EMITIDA na mesma transação. Repetição gera 409, nunca outro cartão.")
    @APIResponses({
        @APIResponse(responseCode="201",description="Operação concluída",content=@Content(schema=@Schema(implementation=CartaoResponse.class))),
        @APIResponse(responseCode="400",description="Entrada inválida",content=@Content(schema=@Schema(implementation=ErroResponse.class))),
        @APIResponse(responseCode="401",description="Credenciais ou token ausentes ou inválidos"),
        @APIResponse(responseCode="404",description="Recurso inexistente ou não pertencente ao titular"),
        @APIResponse(responseCode="409",description="Duplicidade ou estado incompatível"),
        @APIResponse(responseCode="501",description="Casca ainda não implementada",content=@Content(schema=@Schema(implementation=ErroResponse.class)))
    })
    public Response emitir(@PathParam("id") @Positive @Parameter(description="ID da própria solicitação pendente", example="1") Long id) {
        return Response.status(Response.Status.CREATED).entity(CartaoResponse.de(solicitacoes.emitir(usuarioAtual.id(), id))).build();
    }
}
