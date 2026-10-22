package br.edu.fiap.cartoes.application.exception;
/** Mesmo 404 para ID inexistente ou recurso de outro titular, sem revelar sua existência. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) { super(mensagem); }
}
