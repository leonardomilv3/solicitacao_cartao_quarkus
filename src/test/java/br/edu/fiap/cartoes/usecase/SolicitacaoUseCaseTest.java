package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.SolicitacaoRequest;
import br.edu.fiap.cartoes.dto.SolicitacaoResponse;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.exception.SolicitacaoCartaoDuplicadaException;
import br.edu.fiap.cartoes.exception.SolicitacaoCartaoNaoEncontradaException;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import br.edu.fiap.cartoes.repository.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.repository.TipoCartaoRepository;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitacaoUseCaseTest {
    private SolicitacaoCartaoRepository solicitacoes;
    private TipoCartaoRepository tipos;
    private UsuarioRepository usuarios;
    private SolicitacaoUseCase useCase;
    private Usuario titular;
    private TipoCartao tipo;

    @BeforeEach
    void prepararCenario() {
        solicitacoes = mock(SolicitacaoCartaoRepository.class);
        tipos = mock(TipoCartaoRepository.class);
        usuarios = mock(UsuarioRepository.class);
        useCase = new SolicitacaoUseCase(solicitacoes, tipos, usuarios, mock(CartaoRepository.class));

        titular = new Usuario("Ana", "ana@example.com", "hash");
        titular.setId(7L);
        tipo = new TipoCartao("ESSENCIAL", "Uso cotidiano", true);
        tipo.setId(3L);
    }

    @Test
    void listarSemStatusRepassaNuloParaORepositorio() {
        SolicitacaoCartao pendente = solicitacao(11L);
        SolicitacaoCartao emitida = solicitacao(12L);
        emitida.marcarEmitida();
        when(solicitacoes.listar(7L, null)).thenReturn(List.of(emitida, pendente));

        List<SolicitacaoResponse> respostas = useCase.listar(7L, null);

        assertEquals(List.of(12L, 11L), respostas.stream().map(SolicitacaoResponse::id).toList());
        verify(solicitacoes).listar(7L, null);
    }

    @Test
    void listarComStatusRepassaOEnumParaORepositorio() {
        when(solicitacoes.listar(7L, StatusSolicitacao.PENDENTE)).thenReturn(List.of(solicitacao(11L)));

        List<SolicitacaoResponse> respostas = useCase.listar(7L, StatusSolicitacao.PENDENTE);

        assertEquals(1, respostas.size());
        assertEquals(StatusSolicitacao.PENDENTE, respostas.getFirst().status());
        assertEquals("ESSENCIAL", respostas.getFirst().tipoCartaoNome());
    }

    @Test
    void criarNegaPedidoPendenteDuplicadoDoMesmoTipo() {
        when(usuarios.buscarPorId(7L)).thenReturn(Optional.of(titular));
        when(tipos.buscarPorId(3L)).thenReturn(Optional.of(tipo));
        when(solicitacoes.listar(7L, StatusSolicitacao.PENDENTE)).thenReturn(List.of(solicitacao(11L)));

        assertThrows(SolicitacaoCartaoDuplicadaException.class,
                () -> useCase.criar(7L, new SolicitacaoRequest(3L)));
        verify(solicitacoes, never()).salvar(any());
    }

    @Test
    void buscarPedidoDeOutroTitularGera404() {
        when(solicitacoes.buscarPorIdEUsuario(11L, 8L)).thenReturn(Optional.empty());

        assertThrows(SolicitacaoCartaoNaoEncontradaException.class, () -> useCase.buscar(8L, 11L));
    }

    private SolicitacaoCartao solicitacao(Long id) {
        SolicitacaoCartao solicitacao = new SolicitacaoCartao(titular, tipo);
        solicitacao.setId(id);
        return solicitacao;
    }
}
