package br.edu.fiap.cartoes.domain.exception;
/** Use em transições inválidas, tipo indisponível ou duplicidade de pedido. */
public class ErroNegocioException extends RuntimeException {
    public ErroNegocioException(String mensagem) { super(mensagem); }
}
