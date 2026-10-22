package br.edu.fiap.cartoes.application.exception;

public class TipoCartaoNaoEncontradoException extends RecursoNaoEncontradoException {
    public TipoCartaoNaoEncontradoException(String message) {
        super(message);
    }
}
