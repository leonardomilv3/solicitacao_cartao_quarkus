package br.edu.fiap.cartoes.infrastructure.repository.rest;

import br.edu.fiap.cartoes.application.ports.SolicitacaoCartaoRepository;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import br.edu.fiap.cartoes.domain.model.Usuario;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class SolicitacaoCartaoRepositoryJpaTest {
    @Inject
    SolicitacaoCartaoRepository solicitacoes;

    @Inject
    EntityManager entityManager;

    @Test
    @TestTransaction
    void semStatusRetornaTodasAsSolicitacoesDoTitularDaMaisRecenteParaAMaisAntiga() {
        Cenario cenario = new Cenario();

        List<Long> ids = solicitacoes.listar(cenario.titular.getId(), null).stream()
                .map(SolicitacaoCartao::getId)
                .toList();

        assertEquals(List.of(cenario.cancelada.getId(), cenario.emitida.getId(), cenario.pendente.getId()), ids);
    }

    @Test
    @TestTransaction
    void comStatusRetornaSomenteAsSolicitacoesDaqueleStatus() {
        Cenario cenario = new Cenario();

        List<SolicitacaoCartao> pendentes = solicitacoes.listar(cenario.titular.getId(), StatusSolicitacao.PENDENTE);
        List<SolicitacaoCartao> emitidas = solicitacoes.listar(cenario.titular.getId(), StatusSolicitacao.EMITIDA);

        assertEquals(List.of(cenario.pendente.getId()), pendentes.stream().map(SolicitacaoCartao::getId).toList());
        assertEquals(List.of(cenario.emitida.getId()), emitidas.stream().map(SolicitacaoCartao::getId).toList());
    }

    @Test
    @TestTransaction
    void nuncaRetornaSolicitacoesDeOutroTitular() {
        Cenario cenario = new Cenario();

        List<SolicitacaoCartao> semStatus = solicitacoes.listar(cenario.titular.getId(), null);
        List<SolicitacaoCartao> pendentes = solicitacoes.listar(cenario.titular.getId(), StatusSolicitacao.PENDENTE);
        List<SolicitacaoCartao> doOutro = solicitacoes.listar(cenario.outroTitular.getId(), null);

        assertTrue(semStatus.stream().noneMatch(s -> s.getId().equals(cenario.doOutroTitular.getId())));
        assertTrue(pendentes.stream().noneMatch(s -> s.getId().equals(cenario.doOutroTitular.getId())));
        assertEquals(List.of(cenario.doOutroTitular.getId()), doOutro.stream().map(SolicitacaoCartao::getId).toList());
    }

    @Test
    @TestTransaction
    void buscaPorIdSomenteQuandoPertenceAoTitular() {
        Cenario cenario = new Cenario();

        assertTrue(solicitacoes.buscarPorIdEUsuario(cenario.pendente.getId(), cenario.titular.getId()).isPresent());
        assertTrue(solicitacoes.buscarPorIdEUsuario(cenario.pendente.getId(), cenario.outroTitular.getId()).isEmpty());
        assertTrue(solicitacoes.buscarPorIdEUsuario(-1L, cenario.titular.getId()).isEmpty());
    }

    @Test
    void listagemCarregaOTipoSemTransacaoAberta() {
        String sufixo = UUID.randomUUID().toString();
        Long titularId = QuarkusTransaction.requiringNew().call(() -> {
            Usuario titular = criarUsuario("lazy-" + sufixo);
            entityManager.persist(new SolicitacaoCartao(titular, criarTipo("LAZY-" + sufixo)));
            return titular.getId();
        });

        // Fora da transação a entidade fica desanexada: sem JOIN FETCH, ler o tipo lançaria LazyInitializationException.
        SolicitacaoCartao solicitacao = QuarkusTransaction.requiringNew()
                .call(() -> solicitacoes.listar(titularId, null).getFirst());

        assertEquals("LAZY-" + sufixo, solicitacao.getTipoCartao().getNome());
    }

    @Test
    @TestTransaction
    void salvarPreservaDataEStatusERetornaAMesmaInstancia() {
        String sufixo = UUID.randomUUID().toString();
        SolicitacaoCartao nova = new SolicitacaoCartao(criarUsuario("salvar-" + sufixo), criarTipo("SALVAR-" + sufixo));
        Instant solicitadaEm = nova.getSolicitadaEm();

        SolicitacaoCartao salva = solicitacoes.salvar(nova);

        assertEquals(nova, salva);
        assertNotNull(salva.getId());
        assertEquals(solicitadaEm, salva.getSolicitadaEm());
        assertEquals(StatusSolicitacao.PENDENTE, salva.getStatus());
    }

    /** Dois titulares com datas fixas para a ordenação ser determinística. */
    private class Cenario {
        final Usuario titular;
        final Usuario outroTitular;
        final SolicitacaoCartao pendente;
        final SolicitacaoCartao emitida;
        final SolicitacaoCartao cancelada;
        final SolicitacaoCartao doOutroTitular;

        Cenario() {
            String sufixo = UUID.randomUUID().toString();
            titular = criarUsuario("titular-" + sufixo);
            outroTitular = criarUsuario("outro-" + sufixo);
            TipoCartao essencial = criarTipo("ESSENCIAL-" + sufixo);
            TipoCartao viagem = criarTipo("VIAGEM-" + sufixo);

            pendente = criarSolicitacao(titular, essencial, "2026-10-20T10:00:00Z");
            emitida = criarSolicitacao(titular, viagem, "2026-10-20T11:00:00Z");
            emitida.marcarEmitida();
            cancelada = criarSolicitacao(titular, essencial, "2026-10-20T12:00:00Z");
            cancelada.cancelar();
            doOutroTitular = criarSolicitacao(outroTitular, essencial, "2026-10-20T13:00:00Z");
            entityManager.flush();
            entityManager.clear();
        }
    }

    private SolicitacaoCartao criarSolicitacao(Usuario usuario, TipoCartao tipo, String solicitadaEm) {
        SolicitacaoCartao solicitacao = new SolicitacaoCartao(usuario, tipo);
        solicitacao.setSolicitadaEm(Instant.parse(solicitadaEm));
        entityManager.persist(solicitacao);
        return solicitacao;
    }

    private Usuario criarUsuario(String prefixoEmail) {
        Usuario usuario = new Usuario("Usuário de teste", prefixoEmail + "@example.com", "hash");
        entityManager.persist(usuario);
        return usuario;
    }

    private TipoCartao criarTipo(String nome) {
        TipoCartao tipo = new TipoCartao(nome, "Tipo de teste", true);
        entityManager.persist(tipo);
        return tipo;
    }
}
