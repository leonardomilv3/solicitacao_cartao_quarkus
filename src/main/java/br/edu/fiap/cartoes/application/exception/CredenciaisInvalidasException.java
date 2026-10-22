package br.edu.fiap.cartoes.application.exception;
/** Login sempre usa mensagem genérica, sem informar se o e-mail existe. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() { super("E-mail ou senha inválidos"); }
}
