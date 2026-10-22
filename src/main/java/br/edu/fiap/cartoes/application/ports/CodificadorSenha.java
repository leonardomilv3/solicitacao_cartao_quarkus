package br.edu.fiap.cartoes.application.ports;
/** Porta de hash de senha: o use case não conhece a biblioteca de BCrypt. */
public interface CodificadorSenha {
    String criarHash(String senha);

    boolean confere(String senha, String hash);
}
