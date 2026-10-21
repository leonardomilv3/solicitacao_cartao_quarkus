package br.edu.fiap.cartoes.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

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

    @Test
    void copiaDadosPersistidosSemRedefinirEstadoOuDataDeEmissao() {
        SolicitacaoCartao solicitacao = new SolicitacaoCartao(
                new Usuario("Ana", "ana@example.com", "hash"),
                new TipoCartao("ESSENCIAL", "Uso cotidiano", true)
        );
        Cartao entidade = new Cartao(solicitacao);
        Instant emitidoEm = Instant.parse("2026-10-20T12:00:00Z");
        entidade.setId(13L);
        entidade.setEmitidoEm(emitidoEm);
        entidade.setStatus(StatusCartao.BLOQUEADO);

        Cartao dominio = entidade.paraDominio();

        assertEquals(13L, dominio.getId());
        assertEquals(emitidoEm, dominio.getEmitidoEm());
        assertEquals(StatusCartao.BLOQUEADO, dominio.getStatus());
    }
}
