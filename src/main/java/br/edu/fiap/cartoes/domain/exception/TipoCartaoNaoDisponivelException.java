package br.edu.fiap.cartoes.domain.exception;

public class TipoCartaoNaoDisponivelException extends ErroNegocioException {
    public TipoCartaoNaoDisponivelException(String message) {
        super(message);
    }
}
