package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.EmailJaCadastradoException;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
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
public class UsuarioUseCase {


    private final UsuarioRepository usuarioRepository;
    private final SenhaBCrypt senhaBCrypt;

    @Inject
    public UsuarioUseCase(UsuarioRepository usuarioRepository, SenhaBCrypt senhaBCrypt) {
        this.usuarioRepository = usuarioRepository;
        this.senhaBCrypt = senhaBCrypt;
    }

    /** Normalizar e-mail, rejeitar duplicado, gerar hash BCrypt, persistir e mapear sem senha. */
    public UsuarioResponse cadastrar(UsuarioRequest request) {

        if ( usuarioRepository.buscarPorEmail(request.email()).isPresent() ) {
            throw new EmailJaCadastradoException();
        }

        String hash = senhaBCrypt.criarHash(request.senha());

        Usuario novoUsuario = new Usuario(request.nome(), request.email(), hash);
        Usuario usuarioSalvo = usuarioRepository.salvar(novoUsuario);

        return UsuarioResponse.de(usuarioSalvo);
    }
}
