package br.edu.fiap.cartoes.entity;

import jakarta.persistence.*;
import java.time.Instant;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;

/**
 * Camada entity: representa o estado persistido de TipoCartao e seus comportamentos simples.
 * As anotações JPA já estão prontas. Na migração para Clean, discutam se o modelo
 * de domínio continuará anotado ou se haverá um modelo de persistência separado.
 * Não retornem esta entidade diretamente no controller: usem os DTOs.
 */
@Entity
@Table(name = "tipos_cartao")
public class TipoCartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false)
    private boolean disponivel;

    /** Construtor sem argumentos exigido pelo JPA. */
    protected TipoCartao() {}

    /** Construtor do cadastro com propriedades e estado inicial preenchidos. */
    public TipoCartao(String nome, String descricao, boolean disponivel) {
        this.nome = nome;
        this.descricao = descricao;
        this.disponivel = disponivel;
    }

    /** Alterar a oferta do tipo sem apagar pedidos anteriores. */
    public void alterarDisponibilidade(boolean disponivel) {
        this.disponivel = disponivel;
    }

    // Getters e setters prontos; não usar setters de status para contornar as regras.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}
