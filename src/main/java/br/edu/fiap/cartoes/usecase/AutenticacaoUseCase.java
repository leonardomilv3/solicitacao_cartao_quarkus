package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.CredenciaisInvalidasException;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import br.edu.fiap.cartoes.security.JwtService;
import br.edu.fiap.cartoes.security.SenhaBCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import br.edu.fiap.cartoes.dto.*;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;
import jakarta.inject.Inject;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class AutenticacaoUseCase implements AutenticacaoUseCasePort {

    private final UsuarioRepository usuarioRepository;
    private final SenhaBCrypt senhaBCrypt;
    private final JwtService token;

    @Inject
    public AutenticacaoUseCase(UsuarioRepository usuarioRepository,
                               SenhaBCrypt senhaBCrypt,
                               JwtService token) {
        this.usuarioRepository = usuarioRepository;
        this.senhaBCrypt = senhaBCrypt;
        this.token = token;
    }

    /** Buscar e-mail normalizado, conferir BCrypt, negar genericamente ou emitir JWT. */
    @Override
    public TokenResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository.buscarPorEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if ( !senhaBCrypt.confere(request.senha(), usuario.getSenhaHash()) ) {
            throw new CredenciaisInvalidasException();
        }

        return token.emitir(usuario);
    }
}
