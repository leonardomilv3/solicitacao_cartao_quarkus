package br.edu.fiap.cartoes.domain.exception;

public class SolicitacaoCartaoDuplicadaException extends ErroNegocioException {
    public SolicitacaoCartaoDuplicadaException(String message) {
        super(message);
    }
}
