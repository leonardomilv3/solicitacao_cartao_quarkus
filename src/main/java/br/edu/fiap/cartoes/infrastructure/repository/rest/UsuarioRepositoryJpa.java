package br.edu.fiap.cartoes.infrastructure.repository.rest;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.domain.exception.EmailJaCadastradoException;
import br.edu.fiap.cartoes.infrastructure.exception.FalhaInfraestruturaException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.sql.SQLException;
import java.util.Optional;
import br.edu.fiap.cartoes.application.ports.UsuarioRepository;

/**
 * Adaptador JPA: injete EntityManager, escreva JPQL parametrizada e retorne a própria entidade.
 * Não chamar controller nem gerar token nesta camada. Gravações participam da transação aberta pelo use case.
 */
@ApplicationScoped
public class UsuarioRepositoryJpa implements UsuarioRepository {
    private static final String BUSCA_POR_EMAIL = """
        SELECT usuario FROM Usuario usuario
        WHERE lower(usuario.email) = lower(:email)
    """;

    private static final String BUSCA_POR_ID = """
        SELECT usuario FROM Usuario usuario
        WHERE usuario.id = :id
    """;

    private final EntityManager entityManager;

    @Inject
    public UsuarioRepositoryJpa(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email){
        return entityManager.createQuery(BUSCA_POR_EMAIL, Usuario.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                ;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        try{
            Usuario gerenciado = usuario;
            if( usuario.getId() == null ) {
                entityManager.persist(usuario);
            } else {
                gerenciado = entityManager.merge( usuario );
            }

            //Flush força o InSERT, No JDBC isso é o executeUpdate
            // O identity só devolve o id depous que o banco executa o insert
            entityManager.flush();
            return gerenciado;

        }catch(RuntimeException erro){
            if(violouChaveUnica(erro)){
                throw new EmailJaCadastradoException();
            }
            throw new FalhaInfraestruturaException("Falha ao salvar usuário", erro);
        }
    }

    private boolean violouChaveUnica(Throwable erro){
        Throwable atual = erro;
        while(atual != null){
            if(atual instanceof SQLException sql && "23505".equals(sql.getSQLState())){
                return true;
            }
            atual = atual.getCause();
        }
        return false;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return entityManager.createQuery(BUSCA_POR_ID, Usuario.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                ;
    }
}
