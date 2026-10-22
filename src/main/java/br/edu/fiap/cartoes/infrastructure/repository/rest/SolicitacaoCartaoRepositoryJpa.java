package br.edu.fiap.cartoes.infrastructure.repository.rest;
import br.edu.fiap.cartoes.application.ports.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import br.edu.fiap.cartoes.infrastructure.exception.FalhaInfraestruturaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne entidades.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class SolicitacaoCartaoRepositoryJpa implements SolicitacaoCartaoRepository {

    private static final String LISTAR_POR_USUARIO = """
        SELECT s
        FROM SolicitacaoCartao s
        JOIN FETCH s.tipoCartao
        WHERE s.usuario.id = :usuarioId
        """;

    // Filtro adicionado só quando há status: evita (:status IS NULL OR ...) com enum nulo no Hibernate 6.
    private static final String FILTRO_STATUS = " AND s.status = :status";

    private static final String ORDENACAO = " ORDER BY s.solicitadaEm DESC, s.id DESC";

    private static final String BUSCAR_POR_ID_E_USUARIO = """
        SELECT s
        FROM SolicitacaoCartao s
        JOIN FETCH s.tipoCartao
        WHERE s.id = :id
          AND s.usuario.id = :usuarioId
        """;

    private final EntityManager entityManager;

    @Inject
    public SolicitacaoCartaoRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public SolicitacaoCartao salvar(SolicitacaoCartao solicitacaoCartao) {
        try {
            SolicitacaoCartao gerenciada = solicitacaoCartao;
            if (solicitacaoCartao.getId() == null) {
                entityManager.persist(solicitacaoCartao);
            } else {
                gerenciada = entityManager.merge(solicitacaoCartao);
            }

            // Flush força o INSERT/UPDATE; o IDENTITY só devolve o id depois que o banco executa o insert.
            entityManager.flush();
            return gerenciada;

        } catch (RuntimeException erro) {
            throw new FalhaInfraestruturaException("Falha ao salvar solicitação de cartão", erro);
        }
    }

    @Override
    public Optional<SolicitacaoCartao> buscarPorIdEUsuario(Long id, Long usuarioId) {
        return entityManager.createQuery(BUSCAR_POR_ID_E_USUARIO, SolicitacaoCartao.class)
                .setParameter("id", id)
                .setParameter("usuarioId", usuarioId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<SolicitacaoCartao> listar(Long usuarioId, StatusSolicitacao status) {
        String jpql = LISTAR_POR_USUARIO + (status != null ? FILTRO_STATUS : "") + ORDENACAO;

        var query = entityManager.createQuery(jpql, SolicitacaoCartao.class)
                .setParameter("usuarioId", usuarioId);
        if (status != null) {
            query.setParameter("status", status);
        }
        return query.getResultList();
    }

}
