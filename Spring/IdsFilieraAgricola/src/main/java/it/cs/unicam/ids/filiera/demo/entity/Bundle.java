package it.cs.unicam.ids.filiera.demo.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue("BUNDLE")
public class Bundle extends Prodotto {

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BundleItem> items = new ArrayList<>();

    private boolean isBundle;

    @Column(nullable = false)
    private boolean confermato = false;


    protected Bundle() {
    }

    public Bundle(Long venditoreId, String nome, String categoria, BigDecimal prezzo, LocalDate dataScadenza) {
        super(venditoreId, nome, categoria, prezzo, dataScadenza);
    }

    public void aggiungiItem(Prodotto prodotto, int quantita) {
        this.items.add(new BundleItem(this, prodotto, quantita));
    }

    public List<BundleItem> getItems() {
        return items;
    }

    public boolean isBundle() {
        return isBundle;
    }

    public boolean isConfermato() {
        return confermato;
    }

    public void setConfermato(boolean confermato) {
        this.confermato = confermato;
        notifyObservers("Il bundle " + this.getNome() + "è stato confermato");
    }

    public void setBundle(boolean bundle) {
        this.isBundle = bundle;
    }

}



