package br.edu.fiap.cartoes.application.ports;
import java.util.Optional;
import br.edu.fiap.cartoes.domain.model.TipoCartao;

/** Porta de persistência de tipos de cartão; recebe e retorna modelos de domínio. */
public interface TipoCartaoRepository {
    Optional<TipoCartao> buscarPorId(Long id);
}
