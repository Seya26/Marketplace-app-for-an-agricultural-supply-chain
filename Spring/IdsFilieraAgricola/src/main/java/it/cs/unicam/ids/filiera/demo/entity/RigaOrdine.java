package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.*;

@Entity
public class RigaOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Ordine ordine;

    @ManyToOne(optional = false)
    private Prodotto prodotto;

    private int quantita;

    public RigaOrdine() {}

    public RigaOrdine(Ordine ordine, Prodotto prodotto, int quantita) {
        this.ordine = ordine;
        this.prodotto = prodotto;
        this.quantita = quantita;
    }

    public Long getId() { return id; }
    public Ordine getOrdine() { return ordine; }
    public Prodotto getProdotto() { return prodotto; }
    public int getQuantita() { return quantita; }

    public void setOrdine(Ordine ordine) { this.ordine = ordine; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }
    public void setQuantita(int quantita) { this.quantita = quantita; }


}

