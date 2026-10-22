package br.edu.fiap.cartoes.infrastructure.database;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.h2.tools.Server;
import java.sql.SQLException;

/**
 * Abre o H2 em TCP junto da API para consulta por DBeaver, somente no perfil H2 local.
 * Não inicia PostgreSQL nem usa tcpAllowOthers: não exponha este laboratório na rede.
 */
@Startup
@ApplicationScoped
public class H2TcpServer {
    @ConfigProperty(name = "app.h2.tcp.enabled", defaultValue = "false") boolean ativo;
    @ConfigProperty(name = "app.h2.tcp.port", defaultValue = "9093") int porta;
    private Server servidor;
    @PostConstruct void iniciar() throws java.sql.SQLException {
        if (ativo) servidor = Server.createTcpServer("-tcpPort", String.valueOf(porta), "-tcpDaemon").start();
    }
    @PreDestroy void encerrar() { if (servidor != null) servidor.stop(); }
}
