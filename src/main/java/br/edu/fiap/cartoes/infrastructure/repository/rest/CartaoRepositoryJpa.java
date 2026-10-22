package br.edu.fiap.cartoes.infrastructure.repository.rest;
import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.infrastructure.exception.FalhaInfraestruturaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne a própria entidade.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class CartaoRepositoryJpa implements CartaoRepository {

    // Solicitação e tipo no mesmo SELECT: o DTO lê o nome do tipo fora da transação, sem N+1.
    private final static String LISTA_POR_TITULAR = """
        SELECT c
        FROM Cartao c
        JOIN FETCH c.solicitacao s
        JOIN FETCH s.tipoCartao
        WHERE s.usuario.id = :idTitular
        ORDER BY c.emitidoEm DESC, c.id DESC
    """;


    private final static String EXISTE_POR_SOLICITACAO = """
        SELECT c.id
        FROM Cartao c
        WHERE c.solicitacao.id = :idSolicitacao
    """;

    private final EntityManager entityManager;

    @Inject
    public CartaoRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Cartao salvar(Cartao cartao) {
        try {
            entityManager.persist(cartao);
            entityManager.flush();
            return cartao;
        } catch (RuntimeException erro) {
            throw new FalhaInfraestruturaException("Falha ao salvar cartão", erro);
        }
    }

    @Override
    public boolean existePorSolicitacao(Long idSolicitacao) {
        return !entityManager.createQuery(EXISTE_POR_SOLICITACAO, Long.class)
                .setParameter("idSolicitacao", idSolicitacao)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }

    @Override
    public List<Cartao> listarPorTitular(Long idTitular) {
        return entityManager.createQuery(LISTA_POR_TITULAR, Cartao.class)
                .setParameter("idTitular", idTitular)
                .getResultList();
    }
}
