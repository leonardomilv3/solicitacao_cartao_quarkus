package br.edu.fiap.cartoes.repository;
import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.entity.Usuario;
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
public class CartaoRepositoryJpa implements CartaoRepository {
    // TODO: implementar a interface depois de declarar suas assinaturas.

    private final static String LISTA_POR_TITULAR = """
        SELECT c
        FROM Cartao c
        WHERE c.solicitacao.usuario.id = :idTitular
    """;

    private final EntityManager entityManager;

    @Inject
    public CartaoRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Cartao salvar(Cartao cartao) {

        Cartao entity = Cartao.de(cartao);

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
    public List<Cartao> listarPorTitular(Long idTitular) {

        return entityManager.createQuery(LISTA_POR_TITULAR, Cartao.class)
                .setParameter("idTitular", idTitular)
                .getResultList()
                .stream()
                .map(Cartao::paraDominio)
                .toList();
    }
}
