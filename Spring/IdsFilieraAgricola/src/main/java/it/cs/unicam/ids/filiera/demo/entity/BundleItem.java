package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bundle_item")
public class BundleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Bundle bundle;

    @ManyToOne(fetch = FetchType.LAZY)
    private Prodotto prodotto;

    private int quantita;

    // Costruttori, getter, setter
    protected BundleItem() {}

    public BundleItem(Bundle bundle, Prodotto prodotto, int quantita) {
        this.bundle = bundle;
        this.prodotto = prodotto;
        this.quantita = quantita;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public void decrementaQuantita() {
        if (this.quantita > 0) {
            this.quantita--;
        }
    }

    public void incrementaQuantita() {
        this.quantita++;
    }


}