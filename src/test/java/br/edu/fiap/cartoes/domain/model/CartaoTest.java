package br.edu.fiap.cartoes.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CartaoTest {
    @Test
    void nasceAtivo() {
        Cartao cartao = new Cartao(new SolicitacaoCartao(
                new Usuario("Ana", "ana@example.com", "hash"),
                new TipoCartao("ESSENCIAL", "Uso cotidiano", true)
        ));

        assertEquals(StatusCartao.ATIVO, cartao.getStatus());
        assertTrue(cartao.estaAtivo());
    }

    @Test
    void identificaCartaoBloqueado() {
        Cartao cartao = new Cartao(new SolicitacaoCartao(
                new Usuario("Ana", "ana@example.com", "hash"),
                new TipoCartao("ESSENCIAL", "Uso cotidiano", true)
        ));
        cartao.setStatus(StatusCartao.BLOQUEADO);

        assertFalse(cartao.estaAtivo());
    }
}
