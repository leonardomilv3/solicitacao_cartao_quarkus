package br.edu.fiap.cartoes.repository;
import jakarta.enterprise.context.ApplicationScoped;
import br.edu.fiap.cartoes.entity.TipoCartao;
import io.agroal.api.AgroalDataSource;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador JDBC: use AgroalDataSource, PreparedStatement e try-with-resources.
 * Não chamar controller nem gerar token nesta camada. Esta é a única implementação JDBC obrigatória.
 */
@ApplicationScoped
public class TipoCartaoRepositoryJdbc implements ConsultaTiposDisponiveis {
    // TODO: implementar a interface depois de declarar suas assinaturas.

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

                    TipoCartao tipo = new TipoCartao(
                            resultado.getString("nome"),
                            resultado.getString("descricao"),
                            resultado.getBoolean("disponivel")
                    );

                    tipo.setId(resultado.getLong("id"));

                    tipos.add(tipo);
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao consultar tipos de cartão", e);
        }

        return tipos;
    }

}
