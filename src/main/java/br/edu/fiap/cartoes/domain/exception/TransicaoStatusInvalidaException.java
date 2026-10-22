package br.edu.fiap.cartoes.domain.exception;

/** Mudança de status não permitida pelo ciclo de vida da solicitação (ex.: cancelar pedido já emitido). */
public class TransicaoStatusInvalidaException extends ErroNegocioException {
    public TransicaoStatusInvalidaException(String mensagem) {
        super(mensagem);
    }
}
