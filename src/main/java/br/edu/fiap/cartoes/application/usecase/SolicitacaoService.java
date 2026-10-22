package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.domain.model.StatusSolicitacao;
import java.util.List;
import br.edu.fiap.cartoes.domain.model.Cartao;
import br.edu.fiap.cartoes.domain.model.SolicitacaoCartao;

public interface SolicitacaoService {
    SolicitacaoCartao criar(Long usuarioId, Long tipoCartaoId);

    List<SolicitacaoCartao> listar(Long usuarioId, StatusSolicitacao status);

    SolicitacaoCartao buscar(Long usuarioId, Long id);

    SolicitacaoCartao cancelar(Long usuarioId, Long id);

    Cartao emitir(Long usuarioId, Long id);
}
