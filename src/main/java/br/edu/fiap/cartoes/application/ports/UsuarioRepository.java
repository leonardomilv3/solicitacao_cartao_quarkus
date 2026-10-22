package br.edu.fiap.cartoes.application.ports;
import br.edu.fiap.cartoes.domain.model.Usuario;
import java.util.Optional;

/** Porta de persistência de usuários; recebe e retorna modelos de domínio. */
public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);

    Optional<Usuario> buscarPorId(Long id);
}
