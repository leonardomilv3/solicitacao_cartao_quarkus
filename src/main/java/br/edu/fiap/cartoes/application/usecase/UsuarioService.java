package br.edu.fiap.cartoes.application.usecase;

import br.edu.fiap.cartoes.domain.model.Usuario;

public interface UsuarioService {
    Usuario cadastrar(String nome, String email, String senha);
}
