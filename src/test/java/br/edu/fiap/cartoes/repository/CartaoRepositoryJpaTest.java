package br.edu.fiap.cartoes.repository;

import br.edu.fiap.cartoes.entity.Cartao;
import br.edu.fiap.cartoes.entity.SolicitacaoCartao;
import br.edu.fiap.cartoes.entity.TipoCartao;
import br.edu.fiap.cartoes.entity.Usuario;
import br.edu.fiap.cartoes.usecase.CartaoUseCase;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class CartaoRepositoryJpaTest {
    @Inject
    CartaoRepository cartoes;

    @Inject
    CartaoUseCase useCase;

    @Inject
    EntityManager entityManager;

    @Test
    void listaVaziaParaTitularSemCartoes() {
        assertTrue(cartoes.listarPorTitular(-1L).isEmpty());
    }

    @Test
    @Transactional
    void listaSomenteCartoesDoTitularComDadosDoTipoRelacionado() {
        String sufixo = UUID.randomUUID().toString();
        Usuario titular = criarUsuario("titular-" + sufixo);
        Usuario outroTitular = criarUsuario("outro-" + sufixo);
        TipoCartao tipoTitular = criarTipo("ESSENCIAL-" + sufixo);
        TipoCartao tipoOutro = criarTipo("VIAGEM-" + sufixo);
        entityManager.flush();

        SolicitacaoCartao solicitacaoTitular = new SolicitacaoCartao(titular, tipoTitular);
        SolicitacaoCartao solicitacaoOutro = new SolicitacaoCartao(outroTitular, tipoOutro);
        entityManager.persist(solicitacaoTitular);
        entityManager.persist(solicitacaoOutro);
        entityManager.flush();

        Cartao cartaoTitular = cartoes.salvar(new Cartao(solicitacaoTitular));
        cartoes.salvar(new Cartao(solicitacaoOutro));

        var respostas = useCase.listar(titular.getId());

        assertEquals(1, respostas.size());
        assertEquals(cartaoTitular.getId(), respostas.getFirst().id());
        assertEquals("ESSENCIAL-" + sufixo, respostas.getFirst().tipoCartaoNome());
        assertTrue(cartoes.existePorSolicitacao(solicitacaoTitular.getId()));
        assertFalse(cartoes.existePorSolicitacao(-1L));
    }

    private Usuario criarUsuario(String prefixoEmail) {
        Usuario usuario = new Usuario(
                "Usuário de teste",
                prefixoEmail + "@example.com",
                "hash"
        );
        entityManager.persist(usuario);
        return usuario;
    }

    private TipoCartao criarTipo(String nome) {
        TipoCartao tipo = new TipoCartao(nome, "Tipo de teste", true);
        entityManager.persist(tipo);
        return tipo;
    }
}
