package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.CartaoResponse;
import br.edu.fiap.cartoes.dto.SolicitacaoRequest;
import br.edu.fiap.cartoes.dto.SolicitacaoResponse;
import br.edu.fiap.cartoes.entity.StatusSolicitacao;

import java.util.List;

public interface SolicitacaoUseCasePort {
    SolicitacaoResponse criar(Long usuarioId, SolicitacaoRequest request);

    List<SolicitacaoResponse> listar(Long usuarioId, StatusSolicitacao status);

    SolicitacaoResponse buscar(Long usuarioId, Long id);

    SolicitacaoResponse cancelar(Long usuarioId, Long id);

    CartaoResponse emitir(Long usuarioId, Long id);
}
