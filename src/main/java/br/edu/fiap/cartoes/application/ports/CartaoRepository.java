package br.edu.fiap.cartoes.application.ports;
import br.edu.fiap.cartoes.domain.model.Cartao;
import java.util.List;

/** Porta de persistência dos cartões; recebe e retorna modelos de domínio. */
public interface CartaoRepository {
    Cartao salvar(Cartao cartao);

    boolean existePorSolicitacao(Long idSolicitacao);

    List<Cartao> listarPorTitular(Long idTitular);
}
