package br.edu.fiap.cartoes.infrastructure.repository.rest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.application.ports.TipoCartaoRepository;

/** Adaptador JPA: buscar o tipo por ID ao criar uma solicitação, sem chamar o adaptador JDBC. */
@ApplicationScoped
public class TipoCartaoRepositoryJpa implements TipoCartaoRepository {

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
                .findFirst();
    }
}
