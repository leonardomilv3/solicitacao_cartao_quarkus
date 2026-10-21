package br.edu.fiap.cartoes.repository;

import java.util.List;
import br.edu.fiap.cartoes.entity.TipoCartao;
/** Contrato de leitura a completar: listarDisponiveis; exclusivo da rota JDBC. */
public interface ConsultaTiposDisponiveis {

    // TODO: declarar a assinatura e o resultado sem expor ResultSet ou Connection.

    List<TipoCartao> listarDisponiveis();

}
