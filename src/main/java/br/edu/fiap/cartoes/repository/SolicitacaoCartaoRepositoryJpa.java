package br.edu.fiap.cartoes.repository;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.exception.FalhaInfraestruturaException;
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
    // TODO: implementar a interface depois de declarar suas assinaturas.

    private static final String BUSCAR_POR_STATUS = """
        SELECT s
        FROM SolicitacaoCartao s
        WHERE s.usuario.id = :usuarioId
          AND s.status = :status
        ORDER BY s.solicitadaEm DESC
        """;


    private static final String BUSCAR_POR_ID = """
        SELECT s
        FROM SolicitacaoCartao s
        WHERE s.id = :id
        ORDER BY s.solicitadaEm DESC
        """;


    private final EntityManager entityManager;


    @Inject
    public SolicitacaoCartaoRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public SolicitacaoCartao salvar(SolicitacaoCartao solicitacaoCartao) {

        SolicitacaoCartao entity = SolicitacaoCartao.de(solicitacaoCartao);

        try{
            entityManager.persist(entity);

            //Flush força o InSERT, No JDBC isso é o executeUpdate
            // O identity só devolve o id depous que o banco executa o insert
            entityManager.flush();

            return entity.paraDominio();

        }catch(RuntimeException erro){
            throw new FalhaInfraestruturaException("Falhou", erro);
        }

    }


    @Override
    public Optional<SolicitacaoCartao> buscarPorId(Long id) {
        return entityManager.createQuery(BUSCAR_POR_ID, SolicitacaoCartao.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }


    @Override
    public List<SolicitacaoCartao> listarPorStatus(
            Long usuarioId,
            StatusSolicitacao status) {

        return entityManager
                .createQuery(BUSCAR_POR_STATUS, SolicitacaoCartao.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("status", status)
                .getResultList();
    }

}
