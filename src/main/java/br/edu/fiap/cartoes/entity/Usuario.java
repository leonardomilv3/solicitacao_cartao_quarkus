package br.edu.fiap.cartoes.entity;

import jakarta.persistence.*;
import java.time.Instant;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;

/**
 * Camada entity: representa o estado persistido de Usuario e seus comportamentos simples.
 * As anotações JPA já estão prontas. Na migração para Clean, discutam se o modelo
 * de domínio continuará anotado ou se haverá um modelo de persistência separado.
 * Não retornem esta entidade diretamente no controller: usem os DTOs.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    /** Construtor sem argumentos exigido pelo JPA. */
    protected Usuario() {}

    /** Construtor do cadastro com propriedades e estado inicial preenchidos. */
    public Usuario(String nome, String email, String senhaHash) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.criadoEm = Instant.now();
    }

    /** Validar o nome antes de alterá-lo. */
    public void alterarNome(String nome) {
        this.nome = nome;
    }

    /** Aceitar somente hash produzido pelo componente BCrypt, nunca senha em texto. */
    public void atualizarSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    // Getters e setters prontos; não usar setters de status para contornar as regras.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}
