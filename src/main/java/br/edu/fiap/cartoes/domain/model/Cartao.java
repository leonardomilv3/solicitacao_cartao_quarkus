package br.edu.fiap.cartoes.domain.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Modelo de domínio de Cartao: estado persistido (JPA) e comportamentos simples.
 * Não retornem esta entidade diretamente no resource: usem os DTOs.
 */
@Entity
@Table(name = "cartoes")
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false, unique = true)
    private SolicitacaoCartao solicitacao;

    @Column(name = "emitido_em", nullable = false)
    private Instant emitidoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCartao status;

    /** Construtor sem argumentos exigido pelo JPA. */
    protected Cartao() {}

    /** Construtor do cadastro com propriedades e estado inicial preenchidos. */
    public Cartao(SolicitacaoCartao solicitacao) {
        this.solicitacao = solicitacao;
        this.emitidoEm = Instant.now();
        this.status = StatusCartao.ATIVO;
    }

    /** Informar se o cartão está ATIVO. Não criar outra rota obrigatória. */
    public boolean estaAtivo() {
        return status == StatusCartao.ATIVO;
    }
    // Getters e setters prontos; não usar setters de status para contornar as regras.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SolicitacaoCartao getSolicitacao() { return solicitacao; }
    public void setSolicitacao(SolicitacaoCartao solicitacao) { this.solicitacao = solicitacao; }
    public Instant getEmitidoEm() { return emitidoEm; }
    public void setEmitidoEm(Instant emitidoEm) { this.emitidoEm = emitidoEm; }
    public StatusCartao getStatus() { return status; }

    public void setStatus(StatusCartao status) { this.status = status; }
}
