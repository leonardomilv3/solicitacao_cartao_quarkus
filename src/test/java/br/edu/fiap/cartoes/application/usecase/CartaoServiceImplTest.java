package br.edu.fiap.cartoes.application.usecase;

import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartaoServiceImplTest {
    @Test
    void listaSomenteOsCartoesDoTitular() {
        CartaoRepository repository = mock(CartaoRepository.class);
        CartaoServiceImpl useCase = new CartaoServiceImpl(repository);

        Cartao cartao = new Cartao(new SolicitacaoCartao(
                new Usuario("Ana", "ana@example.com", "hash"),
                new TipoCartao("ESSENCIAL", "Uso cotidiano", true)
        ));
        when(repository.listarPorTitular(7L)).thenReturn(List.of(cartao));

        assertEquals(List.of(cartao), useCase.listar(7L));
        verify(repository).listarPorTitular(7L);
    }

    @Test
    void retornaListaVaziaQuandoTitularNaoPossuiCartoes() {
        CartaoRepository repository = mock(CartaoRepository.class);
        when(repository.listarPorTitular(7L)).thenReturn(List.of());

        assertEquals(List.of(), new CartaoServiceImpl(repository).listar(7L));
    }
}
