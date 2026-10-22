package br.edu.fiap.cartoes.domain.model;
/** Credencial emitida no login. Não contém senha nem hash. */
public record Token(String valor, String tipo, long duracaoSegundos, long expiraEm) {}
