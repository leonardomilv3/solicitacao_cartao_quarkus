package br.edu.fiap.cartoes.repository;

import br.edu.fiap.cartoes.entity.Cartao;

import java.util.List;

/**
 * Contrato de persistência dos cartões. Ao migrar para Clean Architecture, mova-o para
 * uma porta da aplicação, sem tipos de EntityManager, ResultSet ou DTO HTTP.
 */
public interface CartaoRepository {
    Cartao salvar(Cartao cartao);

    boolean existePorSolicitacao(Long idSolicitacao);

    List<Cartao> listarPorTitular(Long idTitular);
}
