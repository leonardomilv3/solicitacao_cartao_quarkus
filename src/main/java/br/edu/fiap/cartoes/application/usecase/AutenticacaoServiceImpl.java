package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.application.exception.CredenciaisInvalidasException;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.edu.fiap.cartoes.domain.model.Token;
import br.edu.fiap.cartoes.application.ports.CodificadorSenha;
import br.edu.fiap.cartoes.application.ports.EmissorToken;

/**
 * Camada de aplicação: coordena uma operação, suas regras e transação.
 * Depende só de portas do domínio; o controller converte o resultado para o contrato HTTP.
 */
@ApplicationScoped
public class AutenticacaoServiceImpl implements AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final CodificadorSenha codificadorSenha;
    private final EmissorToken emissorToken;

    @Inject
    public AutenticacaoServiceImpl(UsuarioRepository usuarioRepository,
                               CodificadorSenha codificadorSenha,
                               EmissorToken emissorToken) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorSenha = codificadorSenha;
        this.emissorToken = emissorToken;
    }

    /** Buscar e-mail normalizado, conferir BCrypt, negar genericamente ou emitir JWT. */
    @Override
    public Token login(String email, String senha) {

        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if ( !codificadorSenha.confere(senha, usuario.getSenhaHash()) ) {
            throw new CredenciaisInvalidasException();
        }

        return emissorToken.emitir(usuario);
    }
}
