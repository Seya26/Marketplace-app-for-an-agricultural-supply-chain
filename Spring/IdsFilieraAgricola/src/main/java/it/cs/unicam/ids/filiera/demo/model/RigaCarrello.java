package it.cs.unicam.ids.filiera.demo.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RigaCarrello implements Serializable {
    private Long prodottoId;
    private String nome;
    private BigDecimal prezzoUnitario;
    private Long quantita;

    public RigaCarrello(Long prodottoId, String nome, BigDecimal prezzoUnitario, Long quantita) {
        this.prodottoId = prodottoId;
        this.nome = nome;
        this.prezzoUnitario = prezzoUnitario;
        this.quantita = quantita;
    }

    public Long getProdottoId() { return prodottoId; }
    public String getNome() { return nome; }
    public BigDecimal getPrezzoUnitario() { return prezzoUnitario; }
    public Long getQuantita() { return quantita; }
    public void setQuantita(Long q) { this.quantita = q; }
}
