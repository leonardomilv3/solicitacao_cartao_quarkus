package br.edu.fiap.cartoes.exception;

public class EmailJaCadastradoException extends RegraNegocioException {
    public EmailJaCadastradoException() {
        super("Email já cadastrado");
    }
}
