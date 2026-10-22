package br.edu.fiap.cartoes.entity;

import jakarta.persistence.*;
import java.time.Instant;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;

/**
 * Camada entity: representa o estado persistido de SolicitacaoCartao e seus comportamentos simples.
 * As anotações JPA já estão prontas. Na migração para Clean, discutam se o modelo
 * de domínio continuará anotado ou se haverá um modelo de persistência separado.
 * Não retornem esta entidade diretamente no controller: usem os DTOs.
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


    public SolicitacaoCartao(Long id, Usuario usuario, TipoCartao tipoCartao){
        this.id = id;
        this.usuario = usuario;
        this.tipoCartao = tipoCartao;
    }


    public static SolicitacaoCartao de(SolicitacaoCartao solicitacaoCartao){
        return new SolicitacaoCartao(solicitacaoCartao.getUsuario(), solicitacaoCartao.getTipoCartao());
    }

    public SolicitacaoCartao paraDominio() {
        SolicitacaoCartao copia = new SolicitacaoCartao(usuario, tipoCartao);
        copia.id = id;
        copia.solicitadaEm = solicitadaEm;
        copia.status = status;
        return copia;
    }



    /** Permitir somente PENDENTE para CANCELADA; outros estados geram conflito. */
    public void cancelar() {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Só é possível cancelar uma solicitação pendente.");
        }
        this.status = StatusSolicitacao.CANCELADA;
    }

    /** Permitir somente PENDENTE para EMITIDA; executar na mesma transação da criação do cartão. */
    public void marcarEmitida() {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Só é possível marcar como emitida uma solicitação pendente.");
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
