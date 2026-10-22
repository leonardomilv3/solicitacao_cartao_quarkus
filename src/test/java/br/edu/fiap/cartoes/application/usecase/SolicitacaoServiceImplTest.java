package br.edu.fiap.cartoes.application.usecase;

import br.edu.fiap.cartoes.domain.exception.ErroNegocioException;
import br.edu.fiap.cartoes.domain.exception.SolicitacaoCartaoDuplicadaException;
import br.edu.fiap.cartoes.application.exception.SolicitacaoCartaoNaoEncontradaException;
import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import br.edu.fiap.cartoes.application.ports.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.TipoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitacaoServiceImplTest {
    private SolicitacaoCartaoRepository solicitacoes;
    private TipoCartaoRepository tipos;
    private UsuarioRepository usuarios;
    private CartaoRepository cartoes;
    private SolicitacaoServiceImpl useCase;
    private Usuario titular;
    private TipoCartao tipo;

    @BeforeEach
    void prepararCenario() {
        solicitacoes = mock(SolicitacaoCartaoRepository.class);
        tipos = mock(TipoCartaoRepository.class);
        usuarios = mock(UsuarioRepository.class);
        cartoes = mock(CartaoRepository.class);
        useCase = new SolicitacaoServiceImpl(solicitacoes, tipos, usuarios, cartoes);

        titular = new Usuario(7L, "Ana", "ana@example.com", "hash", Instant.now());
        tipo = new TipoCartao(3L, "ESSENCIAL", "Uso cotidiano", true);
    }

    @Test
    void listarSemStatusRepassaNuloParaORepositorio() {
        List<SolicitacaoCartao> esperadas = List.of(solicitacao(12L), solicitacao(11L));
        when(solicitacoes.listar(7L, null)).thenReturn(esperadas);

        assertEquals(esperadas, useCase.listar(7L, null));
        verify(solicitacoes).listar(7L, null);
    }

    @Test
    void listarComStatusRepassaOEnumParaORepositorio() {
        List<SolicitacaoCartao> esperadas = List.of(solicitacao(11L));
        when(solicitacoes.listar(7L, StatusSolicitacao.PENDENTE)).thenReturn(esperadas);

        assertEquals(esperadas, useCase.listar(7L, StatusSolicitacao.PENDENTE));
    }

    @Test
    void criarAbrePedidoPendenteDoTitularETipo() {
        when(usuarios.buscarPorId(7L)).thenReturn(Optional.of(titular));
        when(tipos.buscarPorId(3L)).thenReturn(Optional.of(tipo));
        when(solicitacoes.listar(7L, StatusSolicitacao.PENDENTE)).thenReturn(List.of());
        when(solicitacoes.salvar(any())).then(returnsFirstArg());

        SolicitacaoCartao criada = useCase.criar(7L, 3L);

        assertEquals(StatusSolicitacao.PENDENTE, criada.getStatus());
        assertEquals(titular, criada.getUsuario());
        assertEquals(tipo, criada.getTipoCartao());
    }

    @Test
    void criarNegaPedidoPendenteDuplicadoDoMesmoTipo() {
        when(usuarios.buscarPorId(7L)).thenReturn(Optional.of(titular));
        when(tipos.buscarPorId(3L)).thenReturn(Optional.of(tipo));
        when(solicitacoes.listar(7L, StatusSolicitacao.PENDENTE)).thenReturn(List.of(solicitacao(11L)));

        assertThrows(SolicitacaoCartaoDuplicadaException.class, () -> useCase.criar(7L, 3L));
        verify(solicitacoes, never()).salvar(any());
    }

    @Test
    void buscarPedidoDeOutroTitularGera404() {
        when(solicitacoes.buscarPorIdEUsuario(11L, 8L)).thenReturn(Optional.empty());

        assertThrows(SolicitacaoCartaoNaoEncontradaException.class, () -> useCase.buscar(8L, 11L));
    }

    @Test
    void emitirMarcaPedidoEmitidoECriaCartaoAtivo() {
        when(solicitacoes.buscarPorIdEUsuario(11L, 7L)).thenReturn(Optional.of(solicitacao(11L)));
        when(solicitacoes.salvar(any())).then(returnsFirstArg());
        when(cartoes.salvar(any())).then(returnsFirstArg());

        Cartao cartao = useCase.emitir(7L, 11L);

        assertEquals(StatusSolicitacao.EMITIDA, cartao.getSolicitacao().getStatus());
        assertEquals(StatusCartao.ATIVO, cartao.getStatus());
    }

    @Test
    void emitirPedidoJaEmitidoGeraConflitoSemCriarOutroCartao() {
        SolicitacaoCartao emitida = solicitacao(11L);
        emitida.marcarEmitida();
        when(solicitacoes.buscarPorIdEUsuario(11L, 7L)).thenReturn(Optional.of(emitida));

        assertThrows(ErroNegocioException.class, () -> useCase.emitir(7L, 11L));
        verify(cartoes, never()).salvar(any());
    }

    private SolicitacaoCartao solicitacao(Long id) {
        SolicitacaoCartao solicitacao = new SolicitacaoCartao(titular, tipo);
        solicitacao.setId(id);
        return solicitacao;
    }
}
