package br.edu.fiap.cartoes.domain.model;

import br.edu.fiap.cartoes.domain.exception.EntradaInvalidaException;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Locale;

/**
 * Modelo de domínio de Usuario: estado persistido (JPA) e comportamentos simples.
 * Não retornem esta entidade diretamente no resource: usem os DTOs.
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

    public Usuario(Long id, String nome, String email, String senhaHash, Instant criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.criadoEm = criadoEm;
    }


    /** Validar o nome antes de alterá-lo. */
    public void alterarNome(String nome) {
        if( nome == null || nome.isBlank() ) {
            throw new EntradaInvalidaException( "Nome não pode ser nulo ou vazio.");
        }
        String normalizado = nome.trim();
        if( normalizado.length() > 100 ) {
            throw new EntradaInvalidaException( "Nome não pode exceder 100 caracteres." );
        }
        this.nome = normalizado;
    }

    public static String normalizarEmail( String email ) {
        if( email == null ) {
            return null;
        }
        String normalizado = email.trim().toLowerCase(Locale.ROOT);
        return normalizado.isEmpty() ? null : normalizado;
    }

    /** Aceitar somente hash produzido pelo componente BCrypt, nunca senha em texto. */
    public void atualizarSenhaHash(String senhaHash) {
        if( senhaHash == null || senhaHash.isBlank() ) {
            throw new EntradaInvalidaException( "Hash de senha não pode ser vazio." );
        }
        //if( !senhaHash.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$"))
        this.senhaHash = senhaHash;
    }


    // Getters e setters prontos; não usar setters de status para contornar as regras.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenhaHash() { return senhaHash; }
    public Instant getCriadoEm() { return criadoEm; }
}
