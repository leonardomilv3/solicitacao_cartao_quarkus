package br.edu.fiap.cartoes.usecase;

import br.edu.fiap.cartoes.dto.LoginRequest;
import br.edu.fiap.cartoes.dto.TokenResponse;

public interface AutenticacaoUseCasePort {
    TokenResponse login(LoginRequest request);
}
