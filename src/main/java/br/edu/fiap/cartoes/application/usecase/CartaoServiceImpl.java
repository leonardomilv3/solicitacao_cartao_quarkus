package br.edu.fiap.cartoes.application.usecase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;

/**
 * Coordena a listagem de cartões. Depende só de portas do domínio;
 * o controller converte o resultado para o contrato HTTP.
 */
@ApplicationScoped
public class CartaoServiceImpl implements CartaoService {
    private final CartaoRepository cartoes;

    @Inject
    public CartaoServiceImpl(CartaoRepository cartoes) {
        this.cartoes = cartoes;
    }

    /** JPQL com relacionamento cartão -> solicitação -> usuário e tipo; somente cartões próprios. */
    @Override
    @Transactional
    public List<Cartao> listar(Long usuarioId) {
        return cartoes.listarPorTitular(usuarioId);
    }

}
