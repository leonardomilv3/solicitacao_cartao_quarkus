package br.edu.fiap.cartoes.application.usecase;
import br.edu.fiap.cartoes.domain.model.Token;

public interface AutenticacaoService {
    Token login(String email, String senha);
}
