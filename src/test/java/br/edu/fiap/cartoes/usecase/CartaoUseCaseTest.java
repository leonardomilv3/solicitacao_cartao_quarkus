package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.CartaoResponse;
import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.StatusCartao;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartaoUseCaseTest {
    @Test
    void listaSomenteOsCartoesDoTitularEMapeiaOTipoRelacionado() {
        CartaoRepository repository = mock(CartaoRepository.class);
        CartaoUseCase useCase = new CartaoUseCase(repository);

        Usuario titular = new Usuario("Ana", "ana@example.com", "hash");
        titular.setId(7L);
        TipoCartao tipo = new TipoCartao("ESSENCIAL", "Uso cotidiano", true);
        tipo.setId(3L);
        SolicitacaoCartao solicitacao = new SolicitacaoCartao(titular, tipo);
        solicitacao.setId(11L);
        Cartao cartao = new Cartao(solicitacao);
        cartao.setId(13L);
        cartao.setEmitidoEm(Instant.parse("2026-10-20T12:00:00Z"));
        when(repository.listarPorTitular(7L)).thenReturn(List.of(cartao));

        List<CartaoResponse> respostas = useCase.listar(7L);

        assertEquals(List.of(new CartaoResponse(
                13L,
                11L,
                3L,
                "ESSENCIAL",
                Instant.parse("2026-10-20T12:00:00Z"),
                StatusCartao.ATIVO
        )), respostas);
        verify(repository).listarPorTitular(7L);
    }

    @Test
    void retornaListaVaziaQuandoTitularNaoPossuiCartoes() {
        CartaoRepository repository = mock(CartaoRepository.class);
        when(repository.listarPorTitular(7L)).thenReturn(List.of());

        assertEquals(List.of(), new CartaoUseCase(repository).listar(7L));
    }
}
