package br.edu.fiap.cartoes.application.usecase;

import br.edu.fiap.cartoes.domain.model.TipoCartao;

import java.util.List;

public interface TipoCartaoService {
    List<TipoCartao> listarDisponiveis();
}
