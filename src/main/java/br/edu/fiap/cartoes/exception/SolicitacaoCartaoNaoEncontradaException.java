package br.edu.fiap.cartoes.exception;

public class SolicitacaoCartaoNaoEncontradaException extends RuntimeException {
    public SolicitacaoCartaoNaoEncontradaException(String message) {
        super(message);
    }
}
