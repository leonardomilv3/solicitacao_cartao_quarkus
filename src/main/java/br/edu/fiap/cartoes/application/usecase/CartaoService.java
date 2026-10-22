package br.edu.fiap.cartoes.application.usecase;

import br.edu.fiap.cartoes.domain.model.Cartao;

import java.util.List;

public interface CartaoService {
    List<Cartao> listar(Long usuarioId);
}
