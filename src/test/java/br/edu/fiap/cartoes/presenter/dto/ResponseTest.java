package br.edu.fiap.cartoes.presenter.dto;

import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.domain.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Conversão domínio -> contrato HTTP; os campos do JSON não podem mudar. */
class ResponseTest {
    private final Usuario titular = new Usuario(7L, "Ana", "ana@example.com", "hash", Instant.parse("2026-10-01T08:00:00Z"));
    private final TipoCartao tipo = new TipoCartao(3L, "ESSENCIAL", "Uso cotidiano", true);
    private final SolicitacaoCartao solicitacao = solicitacaoEmitida();

    private SolicitacaoCartao solicitacaoEmitida() {
        SolicitacaoCartao emitida = new SolicitacaoCartao(titular, tipo);
        emitida.setId(11L);
        emitida.setSolicitadaEm(Instant.parse("2026-10-20T10:00:00Z"));
        emitida.marcarEmitida();
        return emitida;
    }

    @Test
    void solicitacaoResponseMapeiaTitularETipoRelacionado() {
        assertEquals(new SolicitacaoResponse(11L, 7L, 3L, "ESSENCIAL",
                        Instant.parse("2026-10-20T10:00:00Z"), StatusSolicitacao.EMITIDA),
                SolicitacaoResponse.de(solicitacao));
    }

    @Test
    void cartaoResponseMapeiaOTipoPorMeioDaSolicitacao() {
        Cartao cartao = new Cartao(solicitacao);
        cartao.setId(13L);
        cartao.setEmitidoEm(Instant.parse("2026-10-20T12:00:00Z"));

        assertEquals(new CartaoResponse(13L, 11L, 3L, "ESSENCIAL",
                        Instant.parse("2026-10-20T12:00:00Z"), StatusCartao.ATIVO),
                CartaoResponse.de(cartao));
    }

    @Test
    void usuarioResponseNaoExpoeSenha() {
        assertEquals(new UsuarioResponse(7L, "Ana", "ana@example.com", Instant.parse("2026-10-01T08:00:00Z")),
                UsuarioResponse.de(titular));
    }
}
