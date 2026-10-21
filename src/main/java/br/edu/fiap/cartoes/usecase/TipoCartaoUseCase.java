package br.edu.fiap.cartoes.usecase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import br.edu.fiap.cartoes.dto.*;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;
import jakarta.inject.Inject;
import br.edu.fiap.cartoes.repository.ConsultaTiposDisponiveis;

/**
 * Camada de aplicação a implementar: coordena uma operação, suas regras e transação.
 * O controller já chama estas assinaturas. Na migração para Clean, substituam
 * a dependência de DTO HTTP por entradas/saídas da aplicação e portas de saída.
 */
@ApplicationScoped
public class TipoCartaoUseCase {


    @Inject
    ConsultaTiposDisponiveis consultaTiposDisponiveis;

    /** Delegar à única consulta JDBC e mapear a resposta. */
    public List<TipoCartaoResponse> listarDisponiveis() {
        // TODO: implementar sem colocar SQL ou regras no controller.

        return consultaTiposDisponiveis.listarDisponiveis()
                .stream()
                .map(tipo -> new TipoCartaoResponse(
                        tipo.getId(),
                        tipo.getNome(),
                        tipo.getDescricao(),
                        tipo.isDisponivel()
                ))
                .toList();
        }
    }

