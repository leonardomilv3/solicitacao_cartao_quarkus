package br.edu.fiap.cartoes.infrastructure.repository.rest;
import jakarta.enterprise.context.ApplicationScoped;
import br.edu.fiap.cartoes.domain.model.TipoCartao;
import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import br.edu.fiap.cartoes.application.ports.ConsultaTiposDisponiveis;
import br.edu.fiap.cartoes.infrastructure.exception.FalhaInfraestruturaException;

/**
 * Adaptador JDBC: use AgroalDataSource, PreparedStatement e try-with-resources.
 * Não chamar controller nem gerar token nesta camada. Esta é a única implementação JDBC obrigatória.
 */
@ApplicationScoped
public class TipoCartaoRepositoryJdbc implements ConsultaTiposDisponiveis {
    @Inject
    AgroalDataSource dataSource;

    @Override
    public List<TipoCartao> listarDisponiveis() {

        List<TipoCartao> tipos = new ArrayList<>();

        String sql = "SELECT id, nome, descricao, disponivel " +
                "FROM tipos_cartao " +
                "WHERE disponivel = ? " +
                "ORDER BY nome";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setBoolean(1, true);

            try (ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {

                    tipos.add(new TipoCartao(
                            resultado.getLong("id"),
                            resultado.getString("nome"),
                            resultado.getString("descricao"),
                            resultado.getBoolean("disponivel")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new FalhaInfraestruturaException("Erro ao consultar tipos de cartão", e);
        }

        return tipos;
    }

}
