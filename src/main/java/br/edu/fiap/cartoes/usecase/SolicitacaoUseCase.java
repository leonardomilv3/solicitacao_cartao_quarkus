package br.edu.fiap.cartoes.usecase;
import br.edu.fiap.cartoes.entity.*;
import br.edu.fiap.cartoes.exception.*;
import br.edu.fiap.cartoes.repository.CartaoRepository;
import br.edu.fiap.cartoes.repository.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.repository.TipoCartaoRepository;
import br.edu.fiap.cartoes.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import br.edu.fiap.cartoes.dto.*;
import jakarta.inject.Inject;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class SolicitacaoUseCase implements SolicitacaoUseCasePort {


    private final SolicitacaoCartaoRepository solicitacaoCartaoRepository;
    private final TipoCartaoRepository tipoCartaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CartaoRepository cartaoRepository;

    @Inject
    public SolicitacaoUseCase(SolicitacaoCartaoRepository solicitacaoCartaoRepository,
                              TipoCartaoRepository tipoCartaoRepository,
                              UsuarioRepository usuarioRepository,
                              CartaoRepository cartaoRepository) {
        this.solicitacaoCartaoRepository = solicitacaoCartaoRepository;
        this.tipoCartaoRepository = tipoCartaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cartaoRepository = cartaoRepository;
    }


    /** Buscar titular e tipo por JPA; tipo indisponível ou pedido pendente duplicado gera 409. */
    @Override
    public SolicitacaoResponse criar(Long usuarioId, SolicitacaoRequest request) {

        Usuario usuario = buscarUsuario(usuarioId);

        TipoCartao tipoCartao = tipoCartaoRepository.buscarPorId(request.tipoCartaoId())
                .orElseThrow(() -> new TipoCartaoNaoEncontradoException("Tipo de cartão não encontrado."));

        // Tipo indisponivel
        if (!tipoCartao.isDisponivel()) {
            throw new TipoCartaoNaoDisponivelException("Tipo de cartão indisponível.");
        }

        // Pedido duplicado
        if (solicitacaoCartaoRepository.listarPorStatus(usuarioId, StatusSolicitacao.PENDENTE)
                .stream()
                .anyMatch(
                        solicitacao -> solicitacao.getTipoCartao().getId().equals(tipoCartao.getId()
                        ))) {
            throw new SolicitacaoCartaoDuplicadaException("Já existe uma solicitação pendente para este tipo de cartão.");
        }

        SolicitacaoCartao solicitacaoCartao = new SolicitacaoCartao(
                usuario,
                tipoCartao
        );

        SolicitacaoCartao solicitacaoSalva = solicitacaoCartaoRepository.salvar(solicitacaoCartao);

        return SolicitacaoResponse.de(solicitacaoSalva);
    }



    /** JPQL própria com filtro de titular e status opcional, nunca listar todos os usuários. */
    @Override
    public List<SolicitacaoResponse> listar(Long usuarioId, StatusSolicitacao status) {
        List<SolicitacaoCartao> solicitacoes = solicitacaoCartaoRepository.listarPorStatus(usuarioId, status);

        return solicitacoes.stream()
                .filter(solicitacao -> solicitacao.getUsuario().getId().equals(usuarioId))
                .map(SolicitacaoResponse::de)
                .toList();

    }

    /** Buscar por id E titular; retornar 404 para pedido de outro usuário. */
    @Override
    public SolicitacaoResponse buscar(Long usuarioId, Long id) {
        SolicitacaoCartao solicitacao = buscarSolicitacaoDeUsuario(usuarioId, id);
        return SolicitacaoResponse.de(solicitacao);
    }

    /** Em transação, buscar o próprio pedido e chamar cancelar; estado diferente de PENDENTE gera 409. */
    @Override
    public SolicitacaoResponse cancelar(Long usuarioId, Long id) {
        SolicitacaoCartao solicitacao = buscarSolicitacaoDeUsuario(usuarioId, id);
        solicitacao.cancelar();
        solicitacaoCartaoRepository.salvar(solicitacao);
        return SolicitacaoResponse.de(solicitacao);

    }

    /** Em uma transação, bloquear o pedido para escrita, conferir titular/estado, criar cartão e marcar EMITIDA. */
    @Override
    public CartaoResponse emitir(Long usuarioId, Long id) {
        SolicitacaoCartao solicitacao = buscarSolicitacaoDeUsuario(usuarioId, id);
        solicitacao.marcarEmitida();
        solicitacaoCartaoRepository.salvar(solicitacao);
        Cartao cartaoSalvo = cartaoRepository.salvar(new Cartao(solicitacao));
        return CartaoResponse.de(cartaoSalvo);
    }


    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado."));
    }

    private SolicitacaoCartao buscarSolicitacao(Long solicitacaoId){
        return solicitacaoCartaoRepository.buscarPorId(solicitacaoId)
                .orElseThrow(() -> new SolicitacaoCartaoNaoEncontradaException("Solicitação não encontrada."));
    }

    private SolicitacaoCartao buscarSolicitacaoDeUsuario(Long usuarioId, Long solicitacaoId) {
        buscarUsuario(usuarioId);
        SolicitacaoCartao solicitacao = buscarSolicitacao(solicitacaoId);
        if (!solicitacao.getUsuario().getId().equals(usuarioId)) {
            throw new SolicitacaoCartaoNaoEncontradaException("Solicitação não encontrada para este usuário.");
        }
        return solicitacao;
    }
}
