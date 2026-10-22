package br.edu.fiap.cartoes.domain.model;

import br.edu.fiap.cartoes.domain.exception.TransicaoStatusInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Estrutura de teste do grupo; não contém teste resolvido nem @Test de exemplo. */
class SolicitacaoCartaoTest {

    private Usuario usuario;
    private TipoCartao tipoCartao;
    private SolicitacaoCartao solicitacao;

    @BeforeEach
    void prepararCenario() {
        usuario = new Usuario(
                "Leonardo",
                "leonardo@email.com",
                "hash-bcrypt"
        );

        tipoCartao = new TipoCartao(
                "Platinum",
                "Cartão Platinum",
                true
        );

        solicitacao = new SolicitacaoCartao(
                usuario,
                tipoCartao
        );
    }

    @Test
    void deveAlterarNomeDoUsuario() {
        usuario.alterarNome("Leonardo Vitoriano");

        assertEquals("Leonardo Vitoriano", usuario.getNome());
    }

    @Test
    void deveAtualizarSenhaHashDoUsuario() {
        usuario.atualizarSenhaHash("novo-hash");

        assertEquals("novo-hash", usuario.getSenhaHash());
    }

    @Test
    void deveAlterarDisponibilidadeDoTipoCartao() {
        tipoCartao.alterarDisponibilidade(false);

        assertFalse(tipoCartao.isDisponivel());
    }

    @Test
    void deveCriarSolicitacaoComoPendente() {
        assertEquals(StatusSolicitacao.PENDENTE, solicitacao.getStatus());
        assertNotNull(solicitacao.getSolicitadaEm());
        assertSame(usuario, solicitacao.getUsuario());
        assertSame(tipoCartao, solicitacao.getTipoCartao());
    }

    @Test
    void deveCancelarSolicitacaoPendente() {
        solicitacao.cancelar();

        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
    }

    @Test
    void deveMarcarSolicitacaoPendenteComoEmitida() {
        solicitacao.marcarEmitida();

        assertEquals(StatusSolicitacao.EMITIDA, solicitacao.getStatus());
    }

    @Test
    void naoCancelaSolicitacaoQueNaoEstaPendente() {
        solicitacao.marcarEmitida();

        assertThrows(TransicaoStatusInvalidaException.class, solicitacao::cancelar);
        assertEquals(StatusSolicitacao.EMITIDA, solicitacao.getStatus());
    }

    @Test
    void naoEmiteSolicitacaoQueNaoEstaPendente() {
        solicitacao.cancelar();

        assertThrows(TransicaoStatusInvalidaException.class, solicitacao::marcarEmitida);
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
    }
}
