package br.edu.fiap.cartoes.application.usecase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import jakarta.inject.Inject;
import br.edu.fiap.cartoes.application.ports.ConsultaTiposDisponiveis;
import br.edu.fiap.cartoes.domain.model.TipoCartao;

/**
 * Camada de aplicação: coordena uma operação, suas regras e transação.
 * Depende só de portas do domínio; o controller converte o resultado para o contrato HTTP.
 */
@ApplicationScoped
public class TipoCartaoServiceImpl implements TipoCartaoService {

    private final ConsultaTiposDisponiveis consultaTiposDisponiveis;

    @Inject
    public TipoCartaoServiceImpl(ConsultaTiposDisponiveis consultaTiposDisponiveis) {
        this.consultaTiposDisponiveis = consultaTiposDisponiveis;
    }

    /** Delegar à única consulta JDBC. */
    @Override
    public List<TipoCartao> listarDisponiveis() {
        return consultaTiposDisponiveis.listarDisponiveis();
    }
}
