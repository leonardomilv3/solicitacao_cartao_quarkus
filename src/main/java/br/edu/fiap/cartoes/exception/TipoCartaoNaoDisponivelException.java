package br.edu.fiap.cartoes.exception;

public class TipoCartaoNaoDisponivelException extends RegraNegocioException {
    public TipoCartaoNaoDisponivelException(String message) {
        super(message);
    }
}
