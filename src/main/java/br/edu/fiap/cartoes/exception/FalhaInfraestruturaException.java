package br.edu.fiap.cartoes.exception;

public class FalhaInfraestruturaException extends RuntimeException {
    public FalhaInfraestruturaException(String message) {
        super(message);
    }

    public FalhaInfraestruturaException(String mensagem, Throwable causa) {
        super(causa == null || causa.getMessage() == null ? mensagem : mensagem + ": " + causa.getMessage(), causa);
    }
}
