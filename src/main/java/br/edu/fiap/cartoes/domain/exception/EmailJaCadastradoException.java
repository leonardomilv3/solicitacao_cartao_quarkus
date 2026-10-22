package br.edu.fiap.cartoes.domain.exception;

public class EmailJaCadastradoException extends ErroNegocioException {
    public EmailJaCadastradoException() {
        super("Email já cadastrado");
    }
}
