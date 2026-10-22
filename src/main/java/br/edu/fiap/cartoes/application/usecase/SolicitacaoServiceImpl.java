package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import br.edu.fiap.cartoes.application.ports.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.TipoCartaoRepository;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import br.edu.fiap.cartoes.domain.exception.SolicitacaoCartaoDuplicadaException;
import br.edu.fiap.cartoes.application.exception.SolicitacaoCartaoNaoEncontradaException;
import br.edu.fiap.cartoes.domain.exception.TipoCartaoNaoDisponivelException;
import br.edu.fiap.cartoes.application.exception.TipoCartaoNaoEncontradoException;
import br.edu.fiap.cartoes.application.exception.UsuarioNaoEncontradoException;
import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.domain.model.Usuario;

/**
 * Camada de aplicação: coordena uma operação, suas regras e transação.
 * Depende só de portas do domínio; o controller converte o resultado para o contrato HTTP.
 */
@ApplicationScoped
public class SolicitacaoServiceImpl implements SolicitacaoService {

    private final SolicitacaoCartaoRepository solicitacaoCartaoRepository;
    private final TipoCartaoRepository tipoCartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CartaoRepository cartaoRepository;

    @Inject
    public SolicitacaoServiceImpl(SolicitacaoCartaoRepository solicitacaoCartaoRepository,
                              TipoCartaoRepository tipoCartaoRepository,
                              UsuarioRepository usuarioRepository,
                              CartaoRepository cartaoRepository) {
        this.solicitacaoCartaoRepository = solicitacaoCartaoRepository;
        this.tipoCartaoRepository = tipoCartaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cartaoRepository = cartaoRepository;
    }

    /** Buscar titular e tipo; tipo indisponível ou pedido pendente duplicado gera 409. */
    @Override
    @Transactional
    public SolicitacaoCartao criar(Long usuarioId, Long tipoCartaoId) {

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));

        TipoCartao tipoCartao = tipoCartaoRepository.buscarPorId(tipoCartaoId)
                .orElseThrow(() -> new TipoCartaoNaoEncontradoException("Tipo de cartão não encontrado."));

        // Tipo indisponivel
        if (!tipoCartao.isDisponivel()) {
            throw new TipoCartaoNaoDisponivelException("Tipo de cartão indisponível.");
        }

        // Pedido duplicado
        if (solicitacaoCartaoRepository.listar(usuarioId, StatusSolicitacao.PENDENTE)
                .stream()
                .anyMatch(solicitacao -> solicitacao.getTipoCartao().getId().equals(tipoCartao.getId()))) {
            throw new SolicitacaoCartaoDuplicadaException("Já existe uma solicitação pendente para este tipo de cartão.");
        }

        return solicitacaoCartaoRepository.salvar(new SolicitacaoCartao(usuario, tipoCartao));
    }

    /** JPQL própria com filtro de titular e status opcional, nunca listar todos os usuários. */
    @Override
    @Transactional
    public List<SolicitacaoCartao> listar(Long usuarioId, StatusSolicitacao status) {
        return solicitacaoCartaoRepository.listar(usuarioId, status);
    }

    /** Buscar por id E titular; retornar 404 para pedido de outro usuário. */
    @Override
    @Transactional
    public SolicitacaoCartao buscar(Long usuarioId, Long id) {
        return buscarSolicitacaoDeUsuario(usuarioId, id);
    }

    /** Em transação, buscar o próprio pedido e chamar cancelar; estado diferente de PENDENTE gera 409. */
    @Override
    @Transactional
    public SolicitacaoCartao cancelar(Long usuarioId, Long id) {
        SolicitacaoCartao solicitacao = buscarSolicitacaoDeUsuario(usuarioId, id);
        solicitacao.cancelar();
        return solicitacaoCartaoRepository.salvar(solicitacao);
    }

    /** Em uma transação, conferir titular/estado, criar cartão e marcar EMITIDA. */
    @Override
    @Transactional
    public Cartao emitir(Long usuarioId, Long id) {
        SolicitacaoCartao solicitacao = buscarSolicitacaoDeUsuario(usuarioId, id);
        solicitacao.marcarEmitida();
        SolicitacaoCartao emitida = solicitacaoCartaoRepository.salvar(solicitacao);
        return cartaoRepository.salvar(new Cartao(emitida));
    }

    /** Mesmo 404 para ID inexistente ou pedido de outro titular: a consulta filtra os dois juntos. */
    private SolicitacaoCartao buscarSolicitacaoDeUsuario(Long usuarioId, Long solicitacaoId) {
        return solicitacaoCartaoRepository.buscarPorIdEUsuario(solicitacaoId, usuarioId)
                .orElseThrow(() -> new SolicitacaoCartaoNaoEncontradaException("Solicitação não encontrada."));
    }
}
