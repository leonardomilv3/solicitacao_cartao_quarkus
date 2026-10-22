package br.edu.fiap.cartoes.infrastructure.database;
import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Infraestrutura pronta do laboratório: aplica esquema e carga uma vez por inicialização.
 * Os scripts são idempotentes e preservam dados existentes. Não é ferramenta de migração
 * de produção: em um sistema real usem migrações versionadas, como Flyway.
 * A database PostgreSQL precisa existir; CREATE DATABASE não roda nesta transação.
 */
@Startup
@ApplicationScoped
public class DatabaseInitializer {
    @Inject AgroalDataSource dataSource;
    @ConfigProperty(name = "app.database.init", defaultValue = "true") boolean ativo;

    @PostConstruct
    void iniciar() {
        if (!ativo) return;
        try (var connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (String arquivo : new String[]{"db/01_criar_tabelas.sql", "db/02_carga_inicial.sql"}) {
                    try (InputStream input = getClass().getClassLoader().getResourceAsStream(arquivo)) {
                        if (input == null) throw new IllegalStateException("SQL ausente: " + arquivo);
                        String sql = new String(input.readAllBytes(), StandardCharsets.UTF_8)
                                .replaceAll("(?m)^\\s*--.*$", "");
                        // Scripts deste projeto não contêm DO, funções ou ponto e vírgula dentro de strings.
                        for (String comando : sql.split(";")) {
                            if (!comando.isBlank()) try (var statement = connection.createStatement()) {
                                statement.execute(comando.trim());
                            }
                        }
                    }
                }
                connection.commit();
            } catch (Exception erro) {
                connection.rollback();
                throw erro;
            }
        } catch (Exception erro) {
            throw new IllegalStateException("Não foi possível preparar o banco. Confira URL, database e credenciais.", erro);
        }
    }
}
