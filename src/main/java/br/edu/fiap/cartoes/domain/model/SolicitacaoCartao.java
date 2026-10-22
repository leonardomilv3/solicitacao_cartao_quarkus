package br.edu.fiap.cartoes.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import br.edu.fiap.cartoes.domain.exception.TransicaoStatusInvalidaException;

/**
 * Modelo de domínio de SolicitacaoCartao: estado persistido (JPA) e transições de status.
 * Não retornem esta entidade diretamente no resource: usem os DTOs.
 */
@Entity
@Table(name = "solicitacoes_cartao")
public class SolicitacaoCartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_cartao_id", nullable = false)
    private TipoCartao tipoCartao;

    @Column(name = "solicitada_em", nullable = false)
    private Instant solicitadaEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status;

    /** Construtor sem argumentos exigido pelo JPA. */
    protected SolicitacaoCartao() {}

    /** Construtor do cadastro com propriedades e estado inicial preenchidos. */
    public SolicitacaoCartao(Usuario usuario, TipoCartao tipoCartao) {
        this.usuario = usuario;
        this.tipoCartao = tipoCartao;
        this.solicitadaEm = Instant.now();
        this.status = StatusSolicitacao.PENDENTE;
    }


    /** Permitir somente PENDENTE para CANCELADA; outros estados geram conflito. */
    public void cancelar() {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new TransicaoStatusInvalidaException("Só é possível cancelar uma solicitação pendente.");
        }
        this.status = StatusSolicitacao.CANCELADA;
    }

    /** Permitir somente PENDENTE para EMITIDA; executar na mesma transação da criação do cartão. */
    public void marcarEmitida() {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new TransicaoStatusInvalidaException("Só é possível marcar como emitida uma solicitação pendente.");
        }
        this.status = StatusSolicitacao.EMITIDA;
    }

    // Getters e setters prontos; não usar setters de status para contornar as regras.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TipoCartao getTipoCartao() { return tipoCartao; }
    public void setTipoCartao(TipoCartao tipoCartao) { this.tipoCartao = tipoCartao; }
    public Instant getSolicitadaEm() { return solicitadaEm; }
    public void setSolicitadaEm(Instant solicitadaEm) { this.solicitadaEm = solicitadaEm; }
    public StatusSolicitacao getStatus() { return status; }
    public void setStatus(StatusSolicitacao status) { this.status = status; }
}
