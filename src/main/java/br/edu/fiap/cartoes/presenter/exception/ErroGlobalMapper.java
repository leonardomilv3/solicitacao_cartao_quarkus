package br.edu.fiap.cartoes.presenter.exception;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;
import java.time.Instant;
import org.jboss.logging.Logger;
import br.edu.fiap.cartoes.application.exception.CredenciaisInvalidasException;
import br.edu.fiap.cartoes.domain.exception.EntradaInvalidaException;
import br.edu.fiap.cartoes.application.exception.EtapaNaoImplementadaException;
import br.edu.fiap.cartoes.application.exception.RecursoNaoEncontradoException;
import br.edu.fiap.cartoes.domain.exception.ErroNegocioException;
import br.edu.fiap.cartoes.presenter.dto.ErroResponse;

/** Borda HTTP pronta: traduz falhas sem enviar entidades ou detalhes internos ao cliente. */
@Provider
public class ErroGlobalMapper implements ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(ErroGlobalMapper.class);
    @Context UriInfo uri;
    @Override public Response toResponse(Exception erro) {
        int status=500; String codigo="ERRO_INTERNO"; String mensagem="Não foi possível concluir a operação";
        if (erro instanceof EtapaNaoImplementadaException) {
            status=501;codigo="TODO";mensagem=erro.getMessage();
        } else if (erro instanceof RecursoNaoEncontradoException) {
            status=404;codigo="NAO_ENCONTRADO";mensagem=erro.getMessage();
        } else if (erro instanceof ErroNegocioException) {
            status=409;codigo="REGRA_NEGOCIO";mensagem=erro.getMessage();
        } else if (erro instanceof CredenciaisInvalidasException) {
            status=401;codigo="CREDENCIAIS_INVALIDAS";mensagem=erro.getMessage();
        } else if (erro instanceof ConstraintViolationException) {
            status=400;codigo="ENTRADA_INVALIDA";mensagem="Confira os campos obrigatórios e seus limites";
        } else if (erro instanceof WebApplicationException web) {
            status=web.getResponse().getStatus();codigo="HTTP_"+status;
            mensagem=switch(status) {
                case 400 -> "Requisição ou parâmetro inválido";
                case 401 -> "Autenticação necessária ou token inválido";
                case 403 -> "Acesso não permitido";
                case 404 -> "Recurso não encontrado";
                default -> "Não foi possível concluir a requisição";
            };
        } else if(erro instanceof EntradaInvalidaException) {
            status = 400; codigo = "ENTRADA_INVALIDA"; mensagem=erro.getMessage();
        }
        // O cliente recebe mensagem genérica; o stack trace fica só no log do servidor.
        if (status==500) LOG.errorf(erro, "Falha não tratada em %s", uri.getPath());
        var resposta=Response.status(status).type(MediaType.APPLICATION_JSON_TYPE)
            .entity(new ErroResponse(Instant.now(),status,codigo,mensagem,uri.getPath()));
        if (status==401) resposta.header("WWW-Authenticate","Bearer");
        return resposta.build();
    }
}
