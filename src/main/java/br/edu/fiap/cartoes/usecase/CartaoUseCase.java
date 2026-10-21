package br.edu.fiap.cartoes.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import br.edu.fiap.cartoes.dto.CartaoResponse;
import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.repository.CartaoRepository;

/**
 * Coordena a listagem de cartões e converte entidades persistidas para o contrato HTTP.
 * Na migração para Clean, substitua o DTO HTTP por modelos da aplicação e portas de saída.
 */
@ApplicationScoped
public class CartaoUseCase {
    private final CartaoRepository cartoes;

    @Inject
    public CartaoUseCase(CartaoRepository cartoes) {
        this.cartoes = cartoes;
    }

    /** JPQL com relacionamento cartão -> solicitação -> usuário e tipo; somente cartões próprios. */
    @Transactional
    public List<CartaoResponse> listar(Long usuarioId) {
        return cartoes.listarPorTitular(usuarioId).stream()
                .map(this::paraResponse)
                .toList();
    }

    private CartaoResponse paraResponse(Cartao cartao) {
        var solicitacao = cartao.getSolicitacao();
        var tipoCartao = solicitacao.getTipoCartao();
        return new CartaoResponse(
                cartao.getId(),
                solicitacao.getId(),
                tipoCartao.getId(),
                tipoCartao.getNome(),
                cartao.getEmitidoEm(),
                cartao.getStatus()
        );
    }
}
