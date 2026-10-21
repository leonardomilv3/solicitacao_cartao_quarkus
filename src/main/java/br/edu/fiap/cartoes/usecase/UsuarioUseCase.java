package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.EmailJaCadastradoException;
import br.edu.fiap.cartoes.exception.EntradaInvalidaException;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import br.edu.fiap.cartoes.security.SenhaBCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import br.edu.fiap.cartoes.dto.*;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class UsuarioUseCase {


    private final UsuarioRepository usuarioRepository;
    private final SenhaBCrypt senhaBCrypt;

    @Inject
    public UsuarioUseCase(UsuarioRepository usuarioRepository, SenhaBCrypt senhaBCrypt) {
        this.usuarioRepository = usuarioRepository;
        this.senhaBCrypt = senhaBCrypt;
    }

    /** Normalizar e-mail, rejeitar duplicado, gerar hash BCrypt, persistir e mapear sem senha. */
    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {

        String emailNormalizado = Usuario.normalizarEmail( request.email() );
        if( emailNormalizado == null ) {
            throw new EntradaInvalidaException( "Email não pode ser vazio.");
        }

        if ( usuarioRepository.buscarPorEmail(emailNormalizado).isPresent() ) {
            throw new EmailJaCadastradoException();
        }

        String hash = senhaBCrypt.criarHash(request.senha());

        Usuario novoUsuario = new Usuario(request.nome(), emailNormalizado, hash);
        Usuario usuarioSalvo = usuarioRepository.salvar(novoUsuario);

        return UsuarioResponse.de(usuarioSalvo);
    }
}
