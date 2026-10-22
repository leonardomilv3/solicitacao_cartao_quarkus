package br.edu.fiap.cartoes.application.ports;
import java.util.List;
import br.edu.fiap.cartoes.domain.model.TipoCartao;

/** Porta de leitura exclusiva da rota JDBC, sem expor ResultSet ou Connection. */
public interface ConsultaTiposDisponiveis {

    List<TipoCartao> listarDisponiveis();

}
