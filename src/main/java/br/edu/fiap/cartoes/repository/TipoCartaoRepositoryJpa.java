package br.edu.fiap.cartoes.repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import br.edu.fiap.cartoes.entity.TipoCartao;

/** Adaptador JPA a implementar: buscar o tipo por ID ao criar uma solicitação. */
@ApplicationScoped
public class TipoCartaoRepositoryJpa implements TipoCartaoRepository {
    // TODO: implementar a busca com EntityManager, sem chamar o adaptador JDBC.

    private static final String BUSCAR_POR_ID = "SELECT t FROM TipoCartao t WHERE t.id = :id";


    private final EntityManager entityManager;

    @Inject
    public TipoCartaoRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<TipoCartao> buscarPorId(Long id) {
        return entityManager.createQuery(BUSCAR_POR_ID, TipoCartao.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .map(TipoCartao::paraDominio);
    }
}
