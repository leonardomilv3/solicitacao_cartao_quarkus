package br.edu.fiap.cartoes.application.ports;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import java.util.List;
import java.util.Optional;

/** Porta de persistência de solicitações; recebe e retorna modelos de domínio. */
public interface SolicitacaoCartaoRepository {

    SolicitacaoCartao salvar(SolicitacaoCartao solicitacaoCartao);

    /** Busca por id e titular juntos: pedido de outro usuário resulta em vazio. */
    Optional<SolicitacaoCartao> buscarPorIdEUsuario(Long id, Long usuarioId);

    /** Solicitações do titular, mais recentes primeiro; status nulo retorna todas. */
    List<SolicitacaoCartao> listar(Long usuarioId, StatusSolicitacao status);

}
