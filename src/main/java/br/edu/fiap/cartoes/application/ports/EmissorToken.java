package br.edu.fiap.cartoes.application.ports;
import br.edu.fiap.cartoes.domain.model.Token;
import br.edu.fiap.cartoes.domain.model.Usuario;

/** Porta de emissão de token: o use case não conhece SmallRye JWT. */
public interface EmissorToken {
    Token emitir(Usuario usuario);
}
