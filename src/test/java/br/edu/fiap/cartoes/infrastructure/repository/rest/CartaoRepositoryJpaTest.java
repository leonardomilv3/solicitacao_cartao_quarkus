package br.edu.fiap.cartoes.infrastructure.repository.rest;

import br.edu.fiap.cartoes.application.ports.CartaoRepository;
import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.domain.model.Usuario;
import br.edu.fiap.cartoes.application.usecase.CartaoService;
import io.quarkus.narayana.jta.QuarkusTransaction;
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
    CartaoService service;

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

        var lista = service.listar(titular.getId());

        assertEquals(1, lista.size());
        assertEquals(cartaoTitular.getId(), lista.getFirst().getId());
        assertEquals("ESSENCIAL-" + sufixo, lista.getFirst().getSolicitacao().getTipoCartao().getNome());
        assertTrue(cartoes.existePorSolicitacao(solicitacaoTitular.getId()));
        assertFalse(cartoes.existePorSolicitacao(-1L));
    }

    @Test
    void listagemCarregaSolicitacaoETipoParaUsoForaDaTransacao() {
        String sufixo = UUID.randomUUID().toString();
        Long titularId = QuarkusTransaction.requiringNew().call(() -> {
            Usuario titular = criarUsuario("lazy-" + sufixo);
            SolicitacaoCartao solicitacao = new SolicitacaoCartao(titular, criarTipo("LAZY-" + sufixo));
            entityManager.persist(solicitacao);
            entityManager.persist(new Cartao(solicitacao));
            return titular.getId();
        });

        // O resource monta o DTO depois do commit: sem JOIN FETCH, ler o tipo lançaria LazyInitializationException.
        Cartao cartao = service.listar(titularId).getFirst();

        assertEquals("LAZY-" + sufixo, cartao.getSolicitacao().getTipoCartao().getNome());
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
