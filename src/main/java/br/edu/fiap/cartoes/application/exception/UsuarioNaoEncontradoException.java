package br.edu.fiap.cartoes.application.exception;

public class UsuarioNaoEncontradoException extends RecursoNaoEncontradoException {
    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }
}
