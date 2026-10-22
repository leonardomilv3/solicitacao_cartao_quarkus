package br.edu.fiap.cartoes.presenter.resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Ponte HTTP pronta: lê o titular do JWT já verificado pelo Quarkus.
 * O cliente não escolhe usuarioId no JSON nem na query string. Os use cases ainda
 * precisam comparar esse ID com o dono do recurso; autenticação não basta.
 */
@RequestScoped
public class UsuarioAtual {
    @Inject JsonWebToken jwt;
    public Long id() {
        Object valor = jwt.getClaim("usuarioId");
        try {
            long id = Long.parseLong(String.valueOf(valor));
            if (id > 0) return id;
        } catch (NumberFormatException ignored) { }
        throw new NotAuthorizedException("Bearer");
    }
}
