package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.domain.exception.EmailJaCadastradoException;
import br.edu.fiap.cartoes.domain.exception.EntradaInvalidaException;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import br.edu.fiap.cartoes.application.ports.CodificadorSenha;

/**
 * Camada de aplicação: coordena uma operação, suas regras e transação.
 * Depende só de portas do domínio; o controller converte o resultado para o contrato HTTP.
 */
@ApplicationScoped
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CodificadorSenha codificadorSenha;

    @Inject
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, CodificadorSenha codificadorSenha) {
        this.usuarioRepository = usuarioRepository;
        this.codificadorSenha = codificadorSenha;
    }

    /** Normalizar e-mail, rejeitar duplicado, gerar hash BCrypt e persistir. */
    @Override
    @Transactional
    public Usuario cadastrar(String nome, String email, String senha) {

        String emailNormalizado = Usuario.normalizarEmail( email );
        if( emailNormalizado == null ) {
            throw new EntradaInvalidaException( "Email não pode ser vazio.");
        }

        if ( usuarioRepository.buscarPorEmail(emailNormalizado).isPresent() ) {
            throw new EmailJaCadastradoException();
        }

        String hash = codificadorSenha.criarHash(senha);

        return usuarioRepository.salvar(new Usuario(nome, emailNormalizado, hash));
    }
}
